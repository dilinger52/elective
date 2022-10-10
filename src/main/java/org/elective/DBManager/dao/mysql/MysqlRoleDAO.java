package org.elective.DBManager.dao.mysql;

import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.RoleDAO;
import org.elective.DBManager.entity.Role;

import java.sql.*;

import static org.elective.DBManager.DBUtils.*;


public class MysqlRoleDAO implements RoleDAO {

    private static final String CREATE_ROLE = "INSERT INTO role (id, name) VALUES (DEFAULT, ?)";
    private static final String FIND_ROLE_BY_ID = "SELECT * FROM role r WHERE r.id=? ORDER BY r.name";
    private static final String UPDATE_ROLE_BY_ID = "UPDATE role SET name=? WHERE id=?";
    private static final String DELETE_ROLE_BY_ID = "DELETE FROM role WHERE id=?";

    @Override
    public void create(Connection con, Role role) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(CREATE_ROLE, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            stmt.setString(++k, role.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                role.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while adding role", e);
        } finally {
            close(stmt);
        }
    }

    @Override
    public Role read(Connection con, int id) throws DBException {
        Role role = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_ROLE_BY_ID)) {
            int k = 0;
            stmt.setInt(++k, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = mapRole(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding role", e);
        }
        return role;
    }



    @Override
    public void update(Connection con, Role role) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_ROLE_BY_ID);
            int k = 0;
            stmt.setString(++k, role.getName());
            stmt.setInt(++k, role.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while updating role", e);
        } finally {
            close(stmt);
        }
    }

    @Override
    public void delete(Connection con, Role role) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DELETE_ROLE_BY_ID);
            int k = 0;
            stmt.setInt(++k, role.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while deleting role", e);
        } finally {
            close(stmt);
        }
    }

    private Role mapRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        return role;
    }
}
