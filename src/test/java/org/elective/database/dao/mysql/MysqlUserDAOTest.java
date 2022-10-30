package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.UserDAO;
import org.elective.database.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elective.database.dao.mysql.MysqlUserDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MysqlUserDAOTest {

    UserDAO userDAO;
    Map<Integer, User> users;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        userDAO = daoFactory.getUserDAO();
        users = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            User user = new User("User" + number, number + "user", "passexampl", 3, "example" + number + "@email.org");
            user.setId(count);
            users.put(count, user);

        }
        for (int i = 0; i < 2; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            User user = new User("User" + number, number + "user", "passexampl", 2, "example" + number + "@email.org");
            user.setId(count);
            users.put(count, user);

        }

    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        User user = new User("Pasha", "Moiseev", "passexampl", 1, "example@email.org");
        user.setId(count);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt(1)).thenReturn(count);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(users.put(count, user), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS)).thenReturn(stmt);


        userDAO.create(con, user);

        Assertions.assertEquals(user, users.get(count));
    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());
        try {
            userDAO.create(con, new User());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testReadForName() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("id")).thenReturn(users.get(1).getId());
        when(rs.getString("first_name")).thenReturn(users.get(1).getFirstName());
        when(rs.getString("last_name")).thenReturn(users.get(1).getLastName());
        when(rs.getString("password")).thenReturn(users.get(1).getPassword());
        when(rs.getInt("role_id")).thenReturn(users.get(1).getRoleId());
        when(rs.getString("email")).thenReturn(users.get(1).getEmail());
        when(rs.getString("blocked")).thenReturn(users.get(1).isBlocked());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_NAME)).thenReturn(stmt);

        User user = userDAO.read(con, users.get(1).getLastName());
        Assertions.assertTrue(new ReflectionEquals(users.get(1)).matches(user));
    }

    @Test
    public void testReadForNameBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_NAME)).thenThrow(new SQLException());
        try {
            userDAO.read(con, users.get(1).getLastName());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testReadForId() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("id")).thenReturn(users.get(1).getId());
        when(rs.getString("first_name")).thenReturn(users.get(1).getFirstName());
        when(rs.getString("last_name")).thenReturn(users.get(1).getLastName());
        when(rs.getString("password")).thenReturn(users.get(1).getPassword());
        when(rs.getInt("role_id")).thenReturn(users.get(1).getRoleId());
        when(rs.getString("email")).thenReturn(users.get(1).getEmail());
        when(rs.getString("blocked")).thenReturn(users.get(1).isBlocked());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_ID)).thenReturn(stmt);

        User user = userDAO.read(con, users.get(1).getId());
        Assertions.assertTrue(new ReflectionEquals(users.get(1)).matches(user));
    }

    @Test
    public void testReadForIdBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_ID)).thenThrow(new SQLException());
        try {
            userDAO.read(con, users.get(1).getId());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testReadByEmail() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("id")).thenReturn(users.get(1).getId());
        when(rs.getString("first_name")).thenReturn(users.get(1).getFirstName());
        when(rs.getString("last_name")).thenReturn(users.get(1).getLastName());
        when(rs.getString("password")).thenReturn(users.get(1).getPassword());
        when(rs.getInt("role_id")).thenReturn(users.get(1).getRoleId());
        when(rs.getString("email")).thenReturn(users.get(1).getEmail());
        when(rs.getString("blocked")).thenReturn(users.get(1).isBlocked());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_EMAIL)).thenReturn(stmt);

        User user = userDAO.readByEmail(con, users.get(1).getEmail());
        Assertions.assertTrue(new ReflectionEquals(users.get(1)).matches(user));
    }

    @Test
    public void testReadByEmailBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_USER_BY_EMAIL)).thenThrow(new SQLException());
        try {
            userDAO.readByEmail(con, users.get(1).getEmail());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        User user = users.get(1);
        user.setFirstName("First");
        user.setLastName("Second");
        user.setPassword("newpass");
        user.setEmail("default@email.com");

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(users.put(user.getId(), user), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_USER_BY_ID)).thenReturn(stmt);

        userDAO.update(con, user);
        Assertions.assertTrue(new ReflectionEquals(users.get(1)).matches(user));
    }

    @Test
    public void testUpdateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_USER_BY_ID)).thenThrow(new SQLException());
        try {
            userDAO.update(con, new User());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testDelete() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(users.remove(1), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_USER_BY_ID)).thenReturn(stmt);

        userDAO.delete(con, users.get(2));
        Assertions.assertNull(users.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_USER_BY_ID)).thenThrow(new SQLException());
        try {
            userDAO.delete(con, users.get(2));
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testGetAllStudents() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(users.get(1).getId())
                .thenReturn(users.get(2).getId())
                .thenReturn(users.get(3).getId());
        when(rs.getString("first_name")).thenReturn(users.get(1).getFirstName())
                .thenReturn(users.get(2).getFirstName())
                .thenReturn(users.get(3).getFirstName());
        when(rs.getString("last_name")).thenReturn(users.get(1).getLastName())
                .thenReturn(users.get(2).getLastName())
                .thenReturn(users.get(3).getLastName());
        when(rs.getString("password")).thenReturn(users.get(1).getPassword())
                .thenReturn(users.get(2).getPassword())
                .thenReturn(users.get(3).getPassword());
        when(rs.getInt("role_id")).thenReturn(users.get(1).getRoleId())
                .thenReturn(users.get(2).getRoleId())
                .thenReturn(users.get(3).getRoleId());
        when(rs.getString("email")).thenReturn(users.get(1).getEmail())
                .thenReturn(users.get(2).getEmail())
                .thenReturn(users.get(3).getEmail());
        when(rs.getString("blocked")).thenReturn(users.get(1).isBlocked())
                .thenReturn(users.get(2).isBlocked())
                .thenReturn(users.get(3).isBlocked());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ALL_STUDENTS)).thenReturn(stmt);


        List<User> result = userDAO.getAllStudents(con);
        List<User> expected = users.values().stream().filter(v -> v.getRoleId() == 3).collect(Collectors.toList());
        System.out.println(result);
        System.out.println(expected);
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testGetAllStudentsBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ALL_STUDENTS)).thenThrow(new SQLException());
        try {
            userDAO.getAllStudents(con);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testGetAllTeachers() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(users.get(4).getId())
                .thenReturn(users.get(5).getId());
        when(rs.getString("first_name")).thenReturn(users.get(4).getFirstName())
                .thenReturn(users.get(5).getFirstName());
        when(rs.getString("last_name")).thenReturn(users.get(4).getLastName())
                .thenReturn(users.get(5).getLastName());
        when(rs.getString("password")).thenReturn(users.get(4).getPassword())
                .thenReturn(users.get(5).getPassword());
        when(rs.getInt("role_id")).thenReturn(users.get(4).getRoleId())
                .thenReturn(users.get(5).getRoleId());
        when(rs.getString("email")).thenReturn(users.get(4).getEmail())
                .thenReturn(users.get(5).getEmail());
        when(rs.getString("blocked")).thenReturn(users.get(4).isBlocked())
                .thenReturn(users.get(5).isBlocked());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ALL_TEACHERS)).thenReturn(stmt);


        List<User> result = userDAO.getAllTeachers(con);
        List<User> expected = users.values().stream().filter(v -> v.getRoleId() == 2).collect(Collectors.toList());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testGetAllTeachersBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_ALL_TEACHERS)).thenThrow(new SQLException());
        try {
            userDAO.getAllTeachers(con);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

} 
