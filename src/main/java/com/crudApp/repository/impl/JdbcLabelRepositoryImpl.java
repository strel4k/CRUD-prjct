package com.crudApp.repository.impl;

import com.crudApp.model.Label;
import com.crudApp.repository.LabelRepository;
import com.crudApp.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcLabelRepositoryImpl implements LabelRepository {

    @Override
    public Label save(Label label) {
        String sql = "INSERT INTO label (name) VALUES (?)";
        try {
            PreparedStatement ps = JdbcUtil.getInstance()
                    .getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, label.getName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                label.setId(rs.getLong(1));
            }

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return label;
    }


    @Override
    public Label update(Label label) {
        String sql = "UPDATE label SET name = ? WHERE id = ?";
        try {
            PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql);
            ps.setString(1, label.getName());
            ps.setLong(2, label.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return label;
    }

    @Override
    public Label findById(Long id) {
        Label label = null;
        String sql = "SELECT * FROM label WHERE id = ?";
        try {
            PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                label = new Label(rs.getLong("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return label;
    }

    @Override
    public List<Label> findAll() {
        List<Label> labels = new ArrayList<>();
        String sql = "SELECT * FROM label";
        try {
            Statement statement = JdbcUtil.getInstance().getPreparedStatement(sql);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                labels.add(new Label(rs.getLong("id"), rs.getString("name")));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labels;
    }


    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM label WHERE id = ?";
        try {
            PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Label> findAllLabelsByIdIn(List<Long> ids) {
        List<Label> labels = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return labels;
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT * FROM label WHERE id IN (" + placeholders + ")";

        try {
            PreparedStatement ps = JdbcUtil.getInstance().getPreparedStatement(sql);

            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                labels.add(new Label(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return labels;
    }
}