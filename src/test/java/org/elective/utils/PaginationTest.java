package org.elective.utils;

import org.elective.database.entity.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elective.utils.Pagination.pageConstructor;

/**
 * Pagination Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>окт. 21, 2022</pre>
 */
public class PaginationTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: pageConstructor(List<Course> courses)
     */
    @Test
    public void testPageConstructor() throws Exception {
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            courses.add(new Course());
        }
        Map<Integer, List<Course>> result = pageConstructor(courses);
        Assertions.assertEquals(14, result.size());
    }


} 
