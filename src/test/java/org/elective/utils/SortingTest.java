package org.elective.utils;

import org.elective.database.entity.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.utils.Sorting.sort;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SortingTest {
    HttpSession session;
    List<Course> courses;
    Map<Integer, Integer> coursesStudents;

    @Before
    public void before() throws Exception {
        courses = new ArrayList<>();
        Course c1 = new Course(1, "Course", "Topic", "Description", 30L);
        c1.setId(1);
        courses.add(c1);
        Course c2 = new Course(1, "Course", "Topic", "Description", 30L);
        c2.setId(2);
        courses.add(c2);
        Course c3 = new Course(1, "Course", "Topic", "Description", 3L);
        c3.setId(3);
        courses.add(c3);
        Course c4 = new Course(1, "Course", "Topic", "Description", 310L);
        c4.setId(4);
        courses.add(c4);
        Course c5 = new Course(1, "ACourse", "Topic", "Description", 30L);
        c5.setId(5);
        courses.add(c5);
        Course c6 = new Course(1, "ZCourse", "Topic", "Description", 30L);
        c6.setId(6);
        courses.add(c6);
        coursesStudents = new HashMap<>();
        int i = 0;
        for (Course c : courses) {
            coursesStudents.put(c.getId(), ++i);
        }
        session = mock(HttpSession.class);
        when(session.getAttribute("coursesStudents")).thenReturn(coursesStudents);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testSortPattern1() throws Exception {

        List<Course> result = sort(session, courses, 1);
        Assertions.assertEquals(courses.get(0), result.get(0));
    }

    @Test
    public void testSortPattern2() throws Exception {

        List<Course> result = sort(session, courses, 2);
        Assertions.assertEquals(courses.get(5), result.get(0));
    }

    @Test
    public void testSortPattern3() throws Exception {
        List<Course> result = sort(session, courses, 3);
        Assertions.assertEquals(result.get(0), courses.get(4));
        result = sort(session, courses, 0);
        Assertions.assertEquals(courses.get(4), result.get(0));
    }

    @Test
    public void testSortPattern4() throws Exception {
        List<Course> result = sort(session, courses, 4);
        Assertions.assertEquals(courses.get(5), result.get(0));
    }

    @Test
    public void testSortPattern5() throws Exception {
        List<Course> result = sort(session, courses, 5);
        Assertions.assertEquals(courses.get(2), result.get(0));
    }

    @Test
    public void testSortPattern6() throws Exception {
        List<Course> result = sort(session, courses, 6);
        Assertions.assertEquals(courses.get(3), result.get(0));
    }


}
