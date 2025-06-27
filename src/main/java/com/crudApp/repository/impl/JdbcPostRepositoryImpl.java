package com.crudApp.repository.impl;

import com.crudApp.model.Label;
import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.repository.PostRepository;
import com.crudApp.util.JdbcUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class JdbcPostRepositoryImpl implements PostRepository {

    private static final String URL = "jdbc:mysql://localhost:3306/prjct_app";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private final JdbcLabelRepositoryImpl labelRepository = new JdbcLabelRepositoryImpl();

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO post (content, created, updated, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getContent());
            ps.setTimestamp(2, Timestamp.valueOf(post.getCreated()));
            ps.setTimestamp(3, Timestamp.valueOf(post.getUpdated()));
            ps.setString(4, post.getStatus().name());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getLong(1));
            }

            savePostLabels(connection, post);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Post update(Post post) {
        String sql = "UPDATE post SET content = ?, updated = ?, status = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, post.getContent());
            ps.setTimestamp(2, Timestamp.valueOf(post.getUpdated()));
            ps.setString(3, post.getStatus().name());
            ps.setLong(4, post.getId());
            ps.executeUpdate();

            // Удалим старые связи и добавим новые
            deletePostLabels(connection, post.getId());
            savePostLabels(connection, post);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        Post post = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                post = mapPost(rs);
                post.setLabels(getLabelsByPostId(connection, post.getId()));
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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Post post = mapPost(rs);
                post.setLabels(getLabelsByPostId(connection, post.getId()));
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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            deletePostLabels(connection, id);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePostLabels(Connection connection, Post post) throws SQLException {
        String sql = "INSERT INTO post_label (post_id, label_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Label label : post.getLabels()) {
                ps.setLong(1, post.getId());
                ps.setLong(2, label.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deletePostLabels(Connection connection, Long postId) throws SQLException {
        String sql = "DELETE FROM post_label WHERE post_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.executeUpdate();
        }
    }

    private List<Label> getLabelsByPostId(Connection connection, Long postId) throws SQLException {
        List<Label> labels = new ArrayList<>();
        String sql = "SELECT l.id, l.name FROM label l " +
                "JOIN post_label pl ON l.id = pl.label_id WHERE pl.post_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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