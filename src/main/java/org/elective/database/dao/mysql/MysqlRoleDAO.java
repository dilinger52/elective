package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.RoleDAO;
import org.elective.database.entity.Role;

import java.sql.*;

import static org.elective.database.DBUtils.close;


public class MysqlRoleDAO implements RoleDAO {

    static final String CREATE_ROLE = "INSERT INTO role (id, name) VALUES (DEFAULT, ?)";
    static final String FIND_ROLE_BY_ID = "SELECT * FROM role r WHERE r.id=? ORDER BY r.name";
    static final String UPDATE_ROLE_BY_ID = "UPDATE role SET name=? WHERE id=?";
    static final String DELETE_ROLE_BY_ID = "DELETE FROM role WHERE id=?";

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
            throw new DBException("ABigMistakeWhileAddingRole", e);
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
            throw new DBException("ABigMistakeWhileFindingRole", e);
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
            throw new DBException("ABigMistakeWhileUpdatingRole", e);
        } finally {
            close(stmt);
        }
    }

    @Override
    public void delete(Connection con, int roleId) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DELETE_ROLE_BY_ID);
            int k = 0;
            stmt.setInt(++k, roleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileDeletingRole", e);
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
