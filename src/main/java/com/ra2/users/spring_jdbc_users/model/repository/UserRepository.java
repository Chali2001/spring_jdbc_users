package com.ra2.users.spring_jdbc_users.model.repository;

import com.ra2.users.spring_jdbc_users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Arrays;
import java.sql.Timestamp;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setName(rs.getString("name"));
            u.setDescription(rs.getString("description"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setUltimAcces(rs.getTimestamp("ultimAcces"));
            u.setDataCreated(rs.getTimestamp("dataCreated"));
            u.setDataUpdated(rs.getTimestamp("dataUpdated"));

            return u;
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> res = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return res.isEmpty() ? null : res.get(0);
    }

    public int save(User u) {
        String sql = "INSERT INTO users (name, description, email, password, ultimAcces, dataCreated, dataUpdated) VALUES (?,?,?,?,?,?,?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql,
                u.getName(),
                u.getDescription(),
                u.getEmail(),
                u.getPassword(),
                u.getUltimAcces(),
                now,
                now);
    }

    public int insertUsersBatch() {
        final String sql = "INSERT INTO users (name, description, email, password, dataCreated, dataUpdated) VALUES (?,?,?,?,?,?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());

        List<Object[]> batchArgs = List.of(
                new Object[]{"Ana", "Admin", "ana@test.com", "1234", now, now},
                new Object[]{"Bruno", "Gestor", "bruno@test.com", "1234", now, now},
                new Object[]{"Carla", "Cliente", "carla@test.com", "1234", now, now},
                new Object[]{"David", "User", "david@test.com", "1234", now, now},
                new Object[]{"Eva", "User", "eva@test.com", "1234", now, now},
                new Object[]{"Fabio", "User", "fabio@test.com", "1234", now, now},
                new Object[]{"Gema", "User", "gema@test.com", "1234", now, now},
                new Object[]{"Hugo", "User", "hugo@test.com", "1234", now, now},
                new Object[]{"Irene", "User", "irene@test.com", "1234", now, now},
                new Object[]{"Jordi", "User", "jordi@test.com", "1234", now, now}
        );

        int[] res = jdbcTemplate.batchUpdate(sql, batchArgs);
        return Arrays.stream(res).sum();
    }

    public int update(Long id, User u) {
        String sql = "UPDATE users SET name=?, description=?, email=?, password=?, ultimAcces=?, dataUpdated=? WHERE id=?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql,
                u.getName(),
                u.getDescription(),
                u.getEmail(),
                u.getPassword(),
                u.getUltimAcces(),
                now,
                id);
    }

    public int updateName(Long id, String name) {
        String sql = "UPDATE users SET name=?, dataUpdated=? WHERE id=?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, name, now, id);
    }

    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}