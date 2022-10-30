package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Subtopic;
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

import static org.elective.database.dao.mysql.MysqlSubtopicDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MysqlSubtopicDAOTest {

    SubtopicDAO subtopicDAO;
    Map<Integer, Subtopic> subtopics;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        subtopicDAO = daoFactory.getSubtopicDAO();
        subtopics = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            Subtopic subtopic = new Subtopic(1, "Subtopic" + number);
            subtopic.setId(number);
            subtopics.put(count, subtopic);
        }
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        Subtopic subtopic = new Subtopic(2, "new subtopic");

        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(count);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(subtopics.put(count, subtopic), 1).hashCode()));
        when(stmt.getGeneratedKeys()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_SUBTOPIC, Statement.RETURN_GENERATED_KEYS)).thenReturn(stmt);


        subtopicDAO.create(con, subtopic);

        Assertions.assertEquals(subtopic, subtopics.get(count));
    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_SUBTOPIC, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());
        try {
            subtopicDAO.create(con, new Subtopic());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testRead() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("subtopic_id")).thenReturn(subtopics.get(count).getId());
        when(rs.getInt("course_id")).thenReturn(subtopics.get(count).getCourseId());
        when(rs.getString("subtopic_name")).thenReturn(subtopics.get(count).getSubtopicName());
        when(rs.getString("subtopic_content")).thenReturn(subtopics.get(count).getSubtopicContent());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPIC_BY_ID)).thenReturn(stmt);


        Subtopic subtopic = subtopicDAO.read(con, count);
        Assertions.assertTrue(new ReflectionEquals(subtopics.get(count)).matches(subtopic));
    }

    @Test
    public void testReadBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPIC_BY_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.read(con, count);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        Subtopic newSubtopic = subtopics.get(1);
        newSubtopic.setSubtopicName("new name");
        newSubtopic.setSubtopicContent("new content");

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
            subtopicDAO.update(con, new Subtopic());
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

        subtopicDAO.delete(con, 1);
        Assertions.assertNull(subtopics.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_SUBTOPIC_BY_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.delete(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testFindSubtopicsByCourse() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("subtopic_id")).thenReturn(subtopics.get(1).getId())
                .thenReturn(subtopics.get(2).getId())
                .thenReturn(subtopics.get(3).getId())
                .thenReturn(subtopics.get(4).getId())
                .thenReturn(subtopics.get(5).getId());
        when(rs.getInt("course_id")).thenReturn(subtopics.get(1).getCourseId())
                .thenReturn(subtopics.get(2).getCourseId())
                .thenReturn(subtopics.get(3).getCourseId())
                .thenReturn(subtopics.get(4).getCourseId())
                .thenReturn(subtopics.get(5).getCourseId());
        when(rs.getString("subtopic_name")).thenReturn(subtopics.get(1).getSubtopicName())
                .thenReturn(subtopics.get(2).getSubtopicName())
                .thenReturn(subtopics.get(3).getSubtopicName())
                .thenReturn(subtopics.get(4).getSubtopicName())
                .thenReturn(subtopics.get(5).getSubtopicName());
        when(rs.getString("subtopic_content")).thenReturn(subtopics.get(1).getSubtopicContent())
                .thenReturn(subtopics.get(2).getSubtopicContent())
                .thenReturn(subtopics.get(3).getSubtopicContent())
                .thenReturn(subtopics.get(4).getSubtopicContent())
                .thenReturn(subtopics.get(5).getSubtopicContent());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPICS_BY_COURSE_ID)).thenReturn(stmt);


        List<Subtopic> result = subtopicDAO.findSubtopicsByCourse(con, 1);
        List<Subtopic> expected = subtopics.values().stream().filter(v -> v.getCourseId() == 1).collect(Collectors.toList());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testFindSubtopicsByCourseBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_SUBTOPICS_BY_COURSE_ID)).thenThrow(new SQLException());
        try {
            subtopicDAO.findSubtopicsByCourse(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }
} 
