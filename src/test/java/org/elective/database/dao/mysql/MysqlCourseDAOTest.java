package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.entity.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.elective.database.dao.mysql.MysqlCourseDAO.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MysqlCourseDAOTest {
    CourseDAO courseDAO;
    Map<Integer, Course> courses;
    int count = 0;

    @Before
    public void before() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");
        DAOFactory daoFactory = DAOFactory.getInstance();
        courseDAO = daoFactory.getCourseDAO();
        courses = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            count++;
            int number = (int) (Math.random() * 10);
            Course course = new Course(number, "Course" + number, "Topic" + number, "Description" + number, number * 30L);
            course.setId(count);
            courses.put(count, course);

        }

    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        count++;
        Course course = new Course(1, "Course 1", "topic 1", "Good course", 100);
        course.setId(count);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt(1)).thenReturn(count);

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.put(count, course), 1).hashCode()));

        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_COURSE, Statement.RETURN_GENERATED_KEYS)).thenReturn(stmt);


        courseDAO.create(con, course);

        Assertions.assertEquals(course, courses.get(count));

    }

    @Test
    public void testCreateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(CREATE_COURSE, Statement.RETURN_GENERATED_KEYS)).thenThrow(new SQLException());
        try {
            courseDAO.create(con, new Course());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testRead() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(courses.get(1).getId());
        when(rs.getInt("teacher_id")).thenReturn(courses.get(1).getTeacherId());
        when(rs.getString("name")).thenReturn(courses.get(1).getName());
        when(rs.getString("topic")).thenReturn(courses.get(1).getTopic());
        when(rs.getString("description")).thenReturn(courses.get(1).getDescription());
        when(rs.getLong("duration")).thenReturn(courses.get(1).getDuration());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSE_BY_ID)).thenReturn(stmt);


        Course course = courseDAO.read(con, 1);
        Assertions.assertTrue(new ReflectionEquals(courses.get(1)).matches(course));
    }

    @Test
    public void testReadBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSE_BY_ID)).thenThrow(new SQLException());
        try {
            courseDAO.read(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        Course newCourse = courses.get(1);
        newCourse.setDuration(156);
        newCourse.setDescription("new description");
        newCourse.setName("new name");
        newCourse.setTeacherId(3);
        //Assertions.assertFalse(new ReflectionEquals(courses.get(1)).matches(newCourse));

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.put(newCourse.getId(), newCourse), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_COURSE_BY_ID)).thenReturn(stmt);

        courseDAO.update(con, newCourse);
        Assertions.assertTrue(new ReflectionEquals(courses.get(1)).matches(newCourse));
    }

    @Test
    public void testUpdateBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(UPDATE_COURSE_BY_ID)).thenThrow(new SQLException());
        try {
            courseDAO.update(con, new Course());
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testDelete() throws Exception {

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeUpdate()).thenReturn((Objects.requireNonNullElse(courses.remove(1), 1).hashCode()));
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_COURSE_BY_ID)).thenReturn(stmt);

        courseDAO.delete(con, 1);
        Assertions.assertNull(courses.get(1));
    }

    @Test
    public void testDeleteBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(DELETE_COURSE_BY_ID)).thenThrow(new SQLException());
        try {
            courseDAO.delete(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testFindByName() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(courses.get(1).getId())
                .thenReturn(courses.get(2).getId())
                .thenReturn(courses.get(3).getId())
                .thenReturn(courses.get(4).getId())
                .thenReturn(courses.get(5).getId());
        when(rs.getInt("teacher_id")).thenReturn(courses.get(1).getTeacherId())
                .thenReturn(courses.get(2).getTeacherId())
                .thenReturn(courses.get(3).getTeacherId())
                .thenReturn(courses.get(4).getTeacherId())
                .thenReturn(courses.get(5).getTeacherId());
        when(rs.getString("name")).thenReturn(courses.get(1).getName())
                .thenReturn(courses.get(2).getName())
                .thenReturn(courses.get(3).getName())
                .thenReturn(courses.get(4).getName())
                .thenReturn(courses.get(5).getName());
        when(rs.getString("topic")).thenReturn(courses.get(1).getTopic())
                .thenReturn(courses.get(2).getTopic())
                .thenReturn(courses.get(3).getTopic())
                .thenReturn(courses.get(4).getTopic())
                .thenReturn(courses.get(5).getTopic());
        when(rs.getString("description")).thenReturn(courses.get(1).getDescription())
                .thenReturn(courses.get(2).getDescription())
                .thenReturn(courses.get(3).getDescription())
                .thenReturn(courses.get(4).getDescription())
                .thenReturn(courses.get(5).getDescription());
        when(rs.getLong("duration")).thenReturn(courses.get(1).getDuration())
                .thenReturn(courses.get(2).getDuration())
                .thenReturn(courses.get(3).getDuration())
                .thenReturn(courses.get(4).getDuration())
                .thenReturn(courses.get(5).getDuration());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_NAME)).thenReturn(stmt);


        List<Course> result = courseDAO.findByName(con, "Course");
        List<Course> expected = new ArrayList<>(courses.values());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testFindByNameBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_NAME)).thenThrow(new SQLException());
        try {
            courseDAO.findByName(con, "Course");
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testGetAllCourses() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(courses.get(1).getId())
                .thenReturn(courses.get(2).getId())
                .thenReturn(courses.get(3).getId())
                .thenReturn(courses.get(4).getId())
                .thenReturn(courses.get(5).getId());
        when(rs.getInt("teacher_id")).thenReturn(courses.get(1).getTeacherId())
                .thenReturn(courses.get(2).getTeacherId())
                .thenReturn(courses.get(3).getTeacherId())
                .thenReturn(courses.get(4).getTeacherId())
                .thenReturn(courses.get(5).getTeacherId());
        when(rs.getString("name")).thenReturn(courses.get(1).getName())
                .thenReturn(courses.get(2).getName())
                .thenReturn(courses.get(3).getName())
                .thenReturn(courses.get(4).getName())
                .thenReturn(courses.get(5).getName());
        when(rs.getString("topic")).thenReturn(courses.get(1).getTopic())
                .thenReturn(courses.get(2).getTopic())
                .thenReturn(courses.get(3).getTopic())
                .thenReturn(courses.get(4).getTopic())
                .thenReturn(courses.get(5).getTopic());
        when(rs.getString("description")).thenReturn(courses.get(1).getDescription())
                .thenReturn(courses.get(2).getDescription())
                .thenReturn(courses.get(3).getDescription())
                .thenReturn(courses.get(4).getDescription())
                .thenReturn(courses.get(5).getDescription());
        when(rs.getLong("duration")).thenReturn(courses.get(1).getDuration())
                .thenReturn(courses.get(2).getDuration())
                .thenReturn(courses.get(3).getDuration())
                .thenReturn(courses.get(4).getDuration())
                .thenReturn(courses.get(5).getDuration());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(GET_ALL_COURSES)).thenReturn(stmt);


        List<Course> result = courseDAO.getAllCourses(con);
        List<Course> expected = new ArrayList<>(courses.values());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testGetAllCoursesBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(GET_ALL_COURSES)).thenThrow(new SQLException());
        try {
            courseDAO.getAllCourses(con);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testGetAllTopics() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getString("topic")).thenReturn(courses.get(1).getTopic())
                .thenReturn(courses.get(2).getTopic())
                .thenReturn(courses.get(3).getTopic())
                .thenReturn(courses.get(4).getTopic())
                .thenReturn(courses.get(5).getTopic());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(GET_ALL_TOPICS)).thenReturn(stmt);


        List<String> result = courseDAO.getAllTopics(con);
        List<String> expected = courses.values().stream().map(Course::getTopic).collect(Collectors.toList());
        List<String> l = new ArrayList<>();
        for (String s : expected) {
            if (!l.contains(s)) {
                l.add(s);
            }
        }
        expected = l;
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testGetAllTopicsBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(GET_ALL_TOPICS)).thenThrow(new SQLException());
        try {
            courseDAO.getAllTopics(con);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }

    @Test
    public void testFindCoursesByTeacher() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true)
                .thenReturn(false);
        when(rs.getInt("id")).thenReturn(courses.get(1).getId());
        when(rs.getInt("teacher_id")).thenReturn(courses.get(1).getTeacherId());
        when(rs.getString("name")).thenReturn(courses.get(1).getName());
        when(rs.getString("topic")).thenReturn(courses.get(1).getTopic());
        when(rs.getString("description")).thenReturn(courses.get(1).getDescription());
        when(rs.getLong("duration")).thenReturn(courses.get(1).getDuration());

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_TEACHER)).thenReturn(stmt);


        List<Course> result = courseDAO.findCoursesByTeacher(con, courses.get(1).getTeacherId());
        List<Course> expected = courses.values().stream().filter(v -> v.getTeacherId() == courses.get(1).getTeacherId()).collect(Collectors.toList());
        Assertions.assertTrue(new ReflectionEquals(expected).matches(result));
    }

    @Test
    public void testFindCoursesByTeacherBadCase() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(FIND_COURSES_BY_TEACHER)).thenThrow(new SQLException());
        try {
            courseDAO.findCoursesByTeacher(con, 1);
        } catch (Exception e) {
            Assertions.assertEquals(DBException.class, e.getClass());
        }
    }


} 
