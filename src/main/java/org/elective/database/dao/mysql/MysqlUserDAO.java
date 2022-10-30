package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.UserDAO;
import org.elective.database.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.elective.database.DBUtils.close;

public class MysqlUserDAO implements UserDAO {

    static final String CREATE_USER = "INSERT INTO user (id, role_id, first_name, last_name, password, email, blocked) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
    static final String FIND_USER_BY_NAME = "SELECT * FROM user u WHERE u.last_name=? ORDER BY u.first_name";
    static final String FIND_USER_BY_ID = "SELECT * FROM user u WHERE u.id=? ORDER BY u.first_name";
    static final String UPDATE_USER_BY_ID = "UPDATE user SET role_id=?, first_name=?, last_name=?, password=?, email=?, blocked=? WHERE id=?";
    static final String DELETE_USER_BY_ID = "DELETE FROM user WHERE id=?";
    static final String FIND_ALL_TEACHERS = "SELECT * FROM user u WHERE u.role_id=2 ORDER BY u.first_name";
    static final String FIND_USER_BY_EMAIL = "SELECT * FROM user u WHERE u.email=? ORDER BY u.first_name";
    static final String FIND_ALL_STUDENTS = "SELECT * FROM user u WHERE u.role_id=3 ORDER BY u.first_name";
    public static final String MISTAKE_WHILE_FINDING_USER = "ABigMistakeWhileFindingUser";

    public void create(Connection con, User user) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            stmt.setInt(++k, user.getRoleId());
            stmt.setString(++k, user.getFirstName());
            stmt.setString(++k, user.getLastName());
            stmt.setString(++k, user.getPassword());
            stmt.setString(++k, user.getEmail());
            stmt.setString(++k, user.isBlocked());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileAddingUser", e);
        } finally {
            close(stmt);
        }
    }

    public User read(Connection con, String name) throws DBException {
        User user = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_USER_BY_NAME)) {
            int k = 0;
            stmt.setString(++k, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException(MISTAKE_WHILE_FINDING_USER, e);
        }
        return user;
    }

    public User read(Connection con, int id) throws DBException {
        User user = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_USER_BY_ID)) {
            int k = 0;
            stmt.setInt(++k, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException(MISTAKE_WHILE_FINDING_USER, e);
        }
        return user;
    }

    public User readByEmail(Connection con, String email) throws DBException {
        User user = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_USER_BY_EMAIL)) {
            int k = 0;
            stmt.setString(++k, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException(MISTAKE_WHILE_FINDING_USER, e);
        }
        return user;
    }


    public void update(Connection con, User user) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_USER_BY_ID);
            int k = 0;
            stmt.setInt(++k, user.getRoleId());
            stmt.setString(++k, user.getFirstName());
            stmt.setString(++k, user.getLastName());
            stmt.setString(++k, user.getPassword());
            stmt.setString(++k, user.getEmail());
            stmt.setString(++k, user.isBlocked());
            stmt.setInt(++k, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileUpdatingUser", e);
        } finally {
            close(stmt);
        }
    }

    @Override
    public void delete(Connection con, User user) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DELETE_USER_BY_ID);
            int k = 0;
            stmt.setInt(++k, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileDeletingUser", e);
        } finally {
            close(stmt);
        }
    }

    public List<User> getAllStudents(Connection con) throws DBException {
        List<User> students = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(FIND_ALL_STUDENTS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User u = mapUser(rs);
                    students.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileGettingStudents", e);
        }
        return students;
    }

    public List<User> getAllTeachers(Connection con) throws DBException {
        List<User> teachers = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(FIND_ALL_TEACHERS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User u = mapUser(rs);
                    teachers.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileGettingTeachers", e);
        }
        return teachers;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setPassword(rs.getString("password"));
        u.setRoleId(rs.getInt("role_id"));
        u.setEmail(rs.getString("email"));
        u.setBlocked(rs.getString("blocked").equals("true"));
        return u;
    }
}
