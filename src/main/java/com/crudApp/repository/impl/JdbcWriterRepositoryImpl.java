package com.crudApp.repository.impl;

import com.crudApp.model.Post;
import com.crudApp.model.Writer;
import com.crudApp.repository.WriterRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    private static final String URL = "jdbc:mysql://localhost:3306/prjct_app";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private final JdbcPostRepositoryImpl postRepository = new JdbcPostRepositoryImpl();

    @Override
    public Writer save(Writer writer) {
        String sql = "INSERT INTO writer (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, writer.getFirstName());
            ps.setString(2, writer.getLastName());
            ps.setLong(3, writer.getId());
            ps.executeUpdate();

            deleteWriterPosts(connection, writer.getId());
            saveWriterPosts(connection, writer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public Writer findById(Long id) {
        String sql = "SELECT * FROM writer WHERE id = ?";
        Writer writer = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                writer = new Writer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        getPostsByWriterId(connection, id)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public List<Writer> findAll() {
        List<Writer> writers = new ArrayList<>();
        String sql = "SELECT * FROM writer";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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