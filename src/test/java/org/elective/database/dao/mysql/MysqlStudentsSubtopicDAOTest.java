package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.entity.StudentsSubtopic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.elective.database.dao.mysql.MysqlStudentsSubtopicDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MysqlStudentsSubtopicDAOTest {

    StudentsSubtopicDAO subtopicDAO;
    Map<Integer, StudentsSubtopic> subtopics;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        subtopicDAO = daoFactory.getStudentsSubtopicDAO();
        subtopics = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            StudentsSubtopic subtopic = new StudentsSubtopic(number, number * 2);
            subtopics.put(count, subtopic);
        }
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        StudentsSubtopic subtopic = new StudentsSubtopic(5, 3);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(subtopics.put(count, subtopic), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_SUBTOPIC)).thenReturn(stmt);


        subtopicDAO.create(con, subtopic);

        Assertions.assertEquals(subtopic, subtopics.get(count));
    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_SUBTOPIC)).thenThrow(new SQLException());
        try {
            subtopicDAO.create(con, new StudentsSubtopic());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testRead() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("student_id")).thenReturn(subtopics.get(count).getStudentId());
        when(rs.getInt("subtopic_id")).thenReturn(subtopics.get(count).getSubtopicId());
        when(rs.getString("completion")).thenReturn(subtopics.get(count).getCompletion());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPIC_BY_ID)).thenReturn(stmt);


        StudentsSubtopic subtopic = subtopicDAO.read(con, 3, 5);
        Assertions.assertTrue(new ReflectionEquals(subtopics.get(count)).matches(subtopic));
    }

    @Test
    public void testReadBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPIC_BY_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.read(con, 3, 5);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        StudentsSubtopic newSubtopic = subtopics.get(1);
        newSubtopic.setCompletion("completed");

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(subtopics.put(1, newSubtopic), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_SUBTOPIC_BY_ID)).thenReturn(stmt);

        subtopicDAO.update(con, newSubtopic);
        Assertions.assertTrue(new ReflectionEquals(subtopics.get(1)).matches(newSubtopic));
    }

    @Test
    public void testUpdateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_SUBTOPIC_BY_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.update(con, new StudentsSubtopic());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testDelete() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(subtopics.remove(1), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_SUBTOPIC_BY_ID)).thenReturn(stmt);

        subtopicDAO.delete(con, subtopics.get(2));
        Assertions.assertNull(subtopics.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_SUBTOPIC_BY_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.delete(con, subtopics.get(2));
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

} 
