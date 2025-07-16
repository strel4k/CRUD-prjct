package com.crudApp.repository.impl;

import com.crudApp.model.Post;
import com.crudApp.model.PostStatus;
import com.crudApp.model.Writer;
import com.crudApp.repository.WriterRepository;
import com.crudApp.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    private final JdbcPostRepositoryImpl postRepository = new JdbcPostRepositoryImpl();

    @Override
    public Writer save(Writer writer) {
        String sql = "INSERT INTO writer (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = JdbcUtil.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, writer.getFirstName());
            ps.setString(2, writer.getLastName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                writer.setId(rs.getLong(1));
            }

            saveWriterPosts(connection, writer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public Writer update(Writer writer) {
        String sql = "UPDATE writer SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, writer.getFirstName());
                ps.setString(2, writer.getLastName());
                ps.setLong(3, writer.getId());
                ps.executeUpdate();
            }

            deleteWriterPosts(connection, writer.getId());
            saveWriterPosts(connection, writer);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public Writer findById(Long id) {
        String sql = """
            SELECT w.id as writer_id,
                   w.first_name,
                   w.last_name,
                   p.id as post_id,
                   p.content,
                   p.created,
                   p.updated,
                   p.status
            FROM writer w
            LEFT JOIN writer_post wp ON w.id = wp.writer_id
            LEFT JOIN post p ON wp.post_id = p.id
            WHERE w.id = ?
            """;

        Writer writer = null;
        try (Connection connection = JdbcUtil.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            List<Post> posts = new ArrayList<>();

            while (rs.next()) {
                if (writer == null) {
                    writer = new Writer(
                            rs.getLong("writer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            new ArrayList<>()
                    );
                }

                Long postId = rs.getLong("post_id");
                if (postId != 0) {
                    Post post = new Post(
                            postId,
                            rs.getString("content"),
                            rs.getTimestamp("created").toLocalDateTime(),
                            rs.getTimestamp("updated").toLocalDateTime(),
                            new ArrayList<>(),
                            PostStatus.valueOf(rs.getString("status"))
                    );
                    posts.add(post);
                }
            }

            if (writer != null) {
                writer.setPosts(posts);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return writer;
    }

    @Override
    public List<Writer> findAll() {
        List<Writer> writers = new ArrayList<>();
        String sql = "SELECT * FROM writer";

        try (Connection connection = JdbcUtil.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Writer writer = new Writer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        getPostsByWriterId(connection, rs.getLong("id"))
                );
                writers.add(writer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writers;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM writer WHERE id = ?";
        try (Connection connection = JdbcUtil.getInstance().getConnection()) {

            deleteWriterPosts(connection, id);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveWriterPosts(Connection connection, Writer writer) throws SQLException {
        String sql = "INSERT INTO writer_post (writer_id, post_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Post post : writer.getPosts()) {
                ps.setLong(1, writer.getId());
                ps.setLong(2, post.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteWriterPosts(Connection connection, Long writerId) throws SQLException {
        String sql = "DELETE FROM writer_post WHERE writer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, writerId);
            ps.executeUpdate();
        }
    }

    private List<Post> getPostsByWriterId(Connection connection, Long writerId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.* FROM post p " +
                "JOIN writer_post wp ON p.id = wp.post_id WHERE wp.writer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, writerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Post post = postRepository.findById(rs.getLong("id"));
                posts.add(post);
            }
        }
        return posts;
    }
}