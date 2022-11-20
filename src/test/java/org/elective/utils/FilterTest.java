package org.elective.utils;

import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.entity.Course;
import org.elective.database.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterTest {
    List<Course> courses;

    @Before
    public void before() throws Exception {
        courses = new ArrayList<>();
        Course c1 = new Course(1, "Course", "Topic1", "Description", 30L);
        c1.setId(1);
        courses.add(c1);
        Course c2 = new Course(1, "Course", "Topic1", "Description", 30L);
        c2.setId(2);
        courses.add(c2);
        Course c3 = new Course(1, "Course", "Topic2", "Description", 3L);
        c3.setId(3);
        courses.add(c3);
        Course c4 = new Course(1, "Course", "Topic2", "Description", 310L);
        c4.setId(4);
        courses.add(c4);
        Course c5 = new Course(2, "ACourse", "Topic3", "Description", 30L);
        c5.setId(5);
        courses.add(c5);
        Course c6 = new Course(2, "ZCourse", "Topic3", "Description", 30L);
        c6.setId(6);
        courses.add(c6);

    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testFilterTopic() throws Exception {
        String[] topics = new String[]{"Topic1"};
        List<Course> result = Filter.filterTopic(topics, courses);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testFilterTopicBadCase() throws Exception {
        List<Course> result = Filter.filterTopic(null, courses);
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void testFilterTeachers() throws Exception {
        String[] teachers = new String[]{"1"};
        List<Course> result = Filter.filterTeachers(teachers, courses);
        Assertions.assertEquals(4, result.size());
    }

    @Test
    public void testFilterTeachersBadCase() throws Exception {
        List<Course> result = Filter.filterTeachers(null, courses);
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void testFilterName() throws Exception {
        List<Course> result = Filter.filterName("Course", courses);
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void testFilterCompletions() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");

        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("subtopic_id")).thenReturn(4);
        when(rs.getInt("course_id")).thenReturn(1);
        when(rs.getString("subtopic_name")).thenReturn("name");
        when(rs.getString("subtopic_content")).thenReturn("content");

        ResultSet rs2 = mock(ResultSet.class);
        when(rs2.next()).thenReturn(true).thenReturn(false);
        when(rs2.getInt("subtopic_id")).thenReturn(4);
        when(rs2.getInt("student_id")).thenReturn(1);
        when(rs2.getString("completion")).thenReturn("completed");

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        PreparedStatement stmt2 = mock(PreparedStatement.class);
        when(stmt2.executeQuery()).thenReturn(rs2);

        Connection con = mock(Connection.class);
        when(con.prepareStatement("SELECT * FROM course_subtopics s WHERE s.course_id=? ORDER BY s.subtopic_id"))
                .thenReturn(stmt);
        when(con.prepareStatement("SELECT * FROM students_subtopic s WHERE s.subtopic_id=? AND s.student_id=? ORDER BY s.subtopic_id"))
                .thenReturn(stmt2);

        Field instance = DBUtils.class.getDeclaredField("instance");
        instance.setAccessible(true);
        DBUtils dbUtils = mock(DBUtils.class);
        when(dbUtils.getConnection()).thenReturn(con);
        instance.set(dbUtils, dbUtils);

        String[] completions = new String[]{"2"};
        User user = new User();
        user.setId(1);
        List<Course> result = Filter.filterCompletions(completions, courses, user);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testFilterCompletionsBadCase() throws Exception {
        List<Course> result = Filter.filterCompletions(null, courses, new User());
        Assertions.assertEquals(courses.size(), result.size());
    }
} 
