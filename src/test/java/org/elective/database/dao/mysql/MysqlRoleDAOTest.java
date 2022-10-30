package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.RoleDAO;
import org.elective.database.entity.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.elective.database.dao.mysql.MysqlRoleDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MysqlRoleDAOTest {

    RoleDAO roleDAO;
    Map<Integer, Role> roles;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        roleDAO = daoFactory.getRoleDAO();
        roles = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            Role role = new Role("Role" + number);
            role.setId(count);
            roles.put(count, role);
        }
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        Role role = new Role();
        role.setId(count);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt(1)).thenReturn(count);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(roles.put(count, role), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_ROLE, Statement.RETURN_GENERATED_KEYS)).thenReturn(stmt);


        roleDAO.create(con, role);

        Assertions.assertEquals(role, roles.get(count));
    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_ROLE, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());
        try {
            roleDAO.create(con, new Role());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testRead() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(roles.get(1).getId());
        when(rs.getString("name")).thenReturn(roles.get(1).getName());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ROLE_BY_ID)).thenReturn(stmt);


        Role role = roleDAO.read(con, 1);
        Assertions.assertTrue(new ReflectionEquals(roles.get(1)).matches(role));
    }

    @Test
    public void testReadBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ROLE_BY_ID)).thenThrow(new SQLException());
        try {
            roleDAO.read(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        Role newRole = roles.get(1);
        newRole.setName("new name");


        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(roles.put(newRole.getId(), newRole), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_ROLE_BY_ID)).thenReturn(stmt);

        roleDAO.update(con, newRole);
        Assertions.assertTrue(new ReflectionEquals(roles.get(1)).matches(newRole));
    }

    @Test
    public void testUpdateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_ROLE_BY_ID)).thenThrow(new SQLException());
        try {
            roleDAO.update(con, new Role());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testDelete() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(roles.remove(1), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_ROLE_BY_ID)).thenReturn(stmt);

        roleDAO.delete(con, 1);
        Assertions.assertNull(roles.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_ROLE_BY_ID)).thenThrow(new SQLException());
        try {
            roleDAO.delete(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

} 
