package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;
import org.elective.database.entity.User;
import org.elective.logic.CourseManager;
import org.elective.logic.StudentsSubtopicManager;
import org.elective.logic.SubtopicManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.logic.StudentsSubtopicManager.getSubtopicCompletion;
import static org.elective.utils.Pagination.getSubtopicPages;

/**
 * Course content servlet generates student page on which he can view and study course content.
 */
@WebServlet("/course_content")
public class CourseContentServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CourseContentServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading course content...");
        HttpSession session = req.getSession();
        User student = (User) session.getAttribute("user");
        int courseId = Integer.parseInt(req.getParameter("courseId"));

        Map<Integer, Subtopic> pages;
        Course course;
        Map<Integer, String> subtopicCompletion;
        try {
            course = CourseManager.findCourseById(courseId);
            pages = getSubtopicPages(courseId);
            subtopicCompletion = getSubtopicCompletion(student, courseId);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        session.setAttribute("course", course);
        session.setAttribute("pages", pages);
        session.setAttribute("subtopicCompletion", subtopicCompletion);
        session.setAttribute("pageKey", 0);
        session.setAttribute("path", "courseContent.jsp");

        RequestDispatcher rd = req.getRequestDispatcher("courseContent.jsp");
        rd.forward(req, resp);

    }



}
