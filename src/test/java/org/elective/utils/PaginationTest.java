package org.elective.utils;

import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.utils.Pagination.getNewPageKey;
import static org.elective.utils.Pagination.pageConstructor;
import static org.mockito.Mockito.*;

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

    @Test
    public void testGetNewPageKey() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("new_page_key"))
                .thenReturn("-1")
                .thenReturn("3")
                .thenReturn("7");

        Map<Integer, Subtopic> subtopicMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            subtopicMap.put(i, new Subtopic());
        }

        Assertions.assertEquals(0 , getNewPageKey(req, subtopicMap));
        Assertions.assertEquals(3 , getNewPageKey(req, subtopicMap));
        Assertions.assertEquals(4 , getNewPageKey(req, subtopicMap));
    }

    @Test
    public void testGetSubtopicPages() throws Exception {
        DAOFactory.setDAOFactoryFCN("org.elective.database.dao.mysql.MysqlDAOFactory");

        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("subtopic_id")).thenReturn(4);
        when(rs.getInt("course_id")).thenReturn(1);
        when(rs.getString("subtopic_name")).thenReturn("name");
        when(rs.getString("subtopic_content")).thenReturn("content");

        PreparedStatement stmt = mock(PreparedStatement.class);
        when(stmt.executeQuery()).thenReturn(rs);

        Connection con = mock(Connection.class);
        when(con.prepareStatement("SELECT * FROM course_subtopics s WHERE s.course_id=? ORDER BY s.subtopic_id"))
                .thenReturn(stmt);

        Field instance = DBUtils.class.getDeclaredField("instance");
        instance.setAccessible(true);
        DBUtils dbUtils = mock(DBUtils.class);
        when(dbUtils.getConnection()).thenReturn(con);
        instance.set(dbUtils, dbUtils);

        Map<Integer, Subtopic> result = Pagination.getSubtopicPages(1);
        Assertions.assertEquals(1, result.size());
    }
} 
