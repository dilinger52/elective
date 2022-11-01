package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsCourseDAO;
import org.elective.database.entity.StudentsCourse;
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

import static org.elective.database.dao.mysql.MysqlStudentsCourseDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MysqlStudentsCourseDAOTest {

    StudentsCourseDAO courseDAO;
    Map<Integer, StudentsCourse> courses;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        courseDAO = daoFactory.getStudentsCourseDAO();
        courses = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            StudentsCourse course = new StudentsCourse(2 * number, number, new Date(1579L * number));
            courses.put(count, course);
        }
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        StudentsCourse course = new StudentsCourse(5, 3, new Date(1579L));

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.put(count, course), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(ADD_COURSE_TO_STUDENT)).thenReturn(stmt);


        courseDAO.create(con, course);

        Assertions.assertEquals(course, courses.get(count));
    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(ADD_COURSE_TO_STUDENT)).thenThrow(new SQLException());
        try {
            courseDAO.create(con, new StudentsCourse());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testRead() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("student_id")).thenReturn(courses.get(count).getStudentId());
        when(rs.getInt("course_id")).thenReturn(courses.get(count).getCourseId());
        when(rs.getDate("registration_data")).thenReturn(courses.get(count).getRegistrationDate());
        when(rs.getLong("grade")).thenReturn(courses.get(count).getGrade());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSE_BY_COURSE_ID_AND_STUDENT_ID)).thenReturn(stmt);


        StudentsCourse course = courseDAO.read(con, 3, 5);
        Assertions.assertTrue(new ReflectionEquals(courses.get(count)).matches(course));
    }

    @Test
    public void testReadBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSE_BY_COURSE_ID_AND_STUDENT_ID)).thenThrow(new SQLException());
        try {
            courseDAO.read(con, 3, 5);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        StudentsCourse newCourse = courses.get(1);
        newCourse.setRegistrationDate(new Date(5168465L));
        newCourse.setGrade(5);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.put(1, newCourse), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID)).thenReturn(stmt);

        courseDAO.update(con, newCourse);
        Assertions.assertTrue(new ReflectionEquals(courses.get(1)).matches(newCourse));
    }

    @Test
    public void testUpdateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID)).thenThrow(new SQLException());
        try {
            courseDAO.update(con, new StudentsCourse());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testDelete() throws Exception {
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.remove(1), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID)).thenReturn(stmt);


        courseDAO.delete(con, courses.get(2));
        Assertions.assertNull(courses.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID)).thenThrow(new SQLException());
        try {
            courseDAO.delete(con, courses.get(2));
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testFindCoursesByStudentId() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("student_id")).thenReturn(courses.get(1).getStudentId());
        when(rs.getInt("course_id")).thenReturn(courses.get(1).getCourseId());
        when(rs.getDate("registration_data")).thenReturn(courses.get(1).getRegistrationDate());
        when(rs.getLong("grade")).thenReturn(courses.get(1).getGrade());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_STUDENT)).thenReturn(stmt);


        List<StudentsCourse> result = courseDAO.findCoursesByStudentId(con, courses.get(1).getStudentId());
        List<StudentsCourse> expected = courses.values().stream()
                .filter(v -> v.getStudentId() == courses.get(1).getStudentId())
                .collect(Collectors.toList());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testFindCoursesByStudentIdBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_STUDENT)).thenThrow(new SQLException());
        try {
            courseDAO.findCoursesByStudentId(con, courses.get(1).getStudentId());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testFindCoursesByCourseId() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("student_id")).thenReturn(courses.get(1).getStudentId());
        when(rs.getInt("course_id")).thenReturn(courses.get(1).getCourseId());
        when(rs.getDate("registration_data")).thenReturn(courses.get(1).getRegistrationDate());
        when(rs.getLong("grade")).thenReturn(courses.get(1).getGrade());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_COURSE_ID)).thenReturn(stmt);


        List<StudentsCourse> result = courseDAO.findCoursesByCourseId(con, courses.get(1).getCourseId());
        List<StudentsCourse> expected = courses.values().stream().filter(v -> v.getCourseId() == courses.get(1).getCourseId()).collect(Collectors.toList());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testFindCoursesByCourseIdBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_COURSE_ID)).thenThrow(new SQLException());
        try {
            courseDAO.findCoursesByCourseId(con, courses.get(1).getCourseId());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }
} 
