package com.crudApp.repository.impl;

import com.crudApp.model.Label;
import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.repository.PostRepository;
import com.crudApp.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JdbcPostRepositoryImpl implements PostRepository {

    private final JdbcLabelRepositoryImpl labelRepository = new JdbcLabelRepositoryImpl();

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO post (content, created, updated, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getContent());
            ps.setTimestamp(2, Timestamp.valueOf(post.getCreated()));
            ps.setTimestamp(3, Timestamp.valueOf(post.getUpdated()));
            ps.setString(4, post.getStatus().name());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getLong(1));
            }

            savePostLabels(post);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Post update(Post post) {
        String sql = "UPDATE post SET content = ?, updated = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getContent());
            ps.setTimestamp(2, Timestamp.valueOf(post.getUpdated()));
            ps.setString(3, post.getStatus().name());
            ps.setLong(4, post.getId());
            ps.executeUpdate();

            deletePostLabels(post.getId());
            savePostLabels(post);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Post findById(Long id) {
        String sql = """
        SELECT
            p.id AS post_id,
            p.content,
            p.created,
            p.updated,
            p.status,
            l.id AS label_id,
            l.name AS label_name
        FROM post p
        LEFT JOIN post_label pl ON p.id = pl.post_id
        LEFT JOIN label l ON pl.label_id = l.id
        WHERE p.id = ?
        """;
        Post post = null;
        try (PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (post == null) {
                        post = new Post(
                                rs.getLong("post_id"),
                                rs.getString("content"),
                                rs.getTimestamp("created").toLocalDateTime(),
                                rs.getTimestamp("updated").toLocalDateTime(),
                                new ArrayList<>(),
                                PostStatus.valueOf(rs.getString("status"))
                        );
                    }

                    Long labelId = rs.getLong("label_id");
                    if (!rs.wasNull()) {
                        String labelName = rs.getString("label_name");
                        post.getLabels().add(new Label(labelId, labelName));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM post";
        try (
                Statement statement = JdbcUtil.getInstance().getConnection().createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                Post post = mapPost(rs);
                post.setLabels(getLabelsByPostId(post.getId()));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM post WHERE id = ?";
        try {
            deletePostLabels(id);
            try (
                    PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePostLabels(Post post) throws SQLException {
        String sql = "INSERT INTO post_label (post_id, label_id) VALUES (?, ?)";
        try (
                PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            for (Label label : post.getLabels()) {
                ps.setLong(1, post.getId());
                ps.setLong(2, label.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deletePostLabels(Long postId) throws SQLException {
        String sql = "DELETE FROM post_label WHERE post_id = ?";
        try (
                PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setLong(1, postId);
            ps.executeUpdate();
        }
    }

    private List<Label> getLabelsByPostId(Long postId) throws SQLException {
        List<Label> labels = new ArrayList<>();
        String sql = "SELECT l.id, l.name FROM label l " +
                "JOIN post_label pl ON l.id = pl.label_id WHERE pl.post_id = ?";
        try (
                PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setLong(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                labels.add(new Label(rs.getLong("id"), rs.getString("name")));
            }
        }
        return labels;
    }

    private Post mapPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getLong("id"),
                rs.getString("content"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime(),
                new ArrayList<>(), // временно, потом подставим
                PostStatus.valueOf(rs.getString("status"))
        );
    }
}