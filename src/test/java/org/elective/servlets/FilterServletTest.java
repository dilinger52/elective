package org.elective.servlets;

import org.elective.database.entity.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class FilterServletTest {
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
        List<Course> result = FilterServlet.filterTopic(topics, courses);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testFilterTopicBadCase() throws Exception {
        List<Course> result = FilterServlet.filterTopic(null, courses);
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void testFilterTeachers() throws Exception {
        String[] teachers = new String[]{"1"};
        List<Course> result = FilterServlet.filterTeachers(teachers, courses);
        Assertions.assertEquals(4, result.size());
    }

    @Test
    public void testFilterTeachersBadCase() throws Exception {
        List<Course> result = FilterServlet.filterTeachers(null, courses);
        Assertions.assertEquals(6, result.size());
    }


} 
