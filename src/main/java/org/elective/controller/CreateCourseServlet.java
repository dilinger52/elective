package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.UserDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import static org.elective.logic.CourseManager.*;
import static org.elective.logic.UserManager.findAllTeachers;

/**
 * Create course servlet generate curse creating/editing page. From this page administrator can go to create
 * teacher page and delete courses.
 */
@WebServlet("/create_course")
public class CreateCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CreateCourseServlet.class);
    private static final String SELECTED = "selected";
    private static final String MESSAGE = "message";
    private static final String ERROR_PAGE = "error.jsp";
    private static final String COURSE_ID = "courseId";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading course redactor page...");
        HttpSession session = req.getSession();
        session.removeAttribute("course");
        session.removeAttribute(SELECTED);
        session.removeAttribute("selectedTopic");

        List<User> teachers;
        List<String> topics;
        try {
            teachers = findAllTeachers();
            topics = findAllTopics();
        } catch (Exception e) {
            req.setAttribute(MESSAGE, e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        if (req.getParameter(COURSE_ID) != null) {
            int courseId = Integer.parseInt(req.getParameter(COURSE_ID));
            Course course;
            try {
                course = findCourseById(courseId);
            } catch (Exception e) {
                req.setAttribute(MESSAGE, e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
                rd.forward(req, resp);
                return;
            }
            session.setAttribute("course", course);
        }

        session.setAttribute("teachers", teachers);
        session.setAttribute("topics", topics);

        RequestDispatcher rd = req.getRequestDispatcher("createCourse.jsp");
        rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Saving course...");

        String name = req.getParameter("name");
        String topic = getTopic(req.getParameterValues("topic"));
        String description = req.getParameter("description");
        long duration = Long.parseLong(req.getParameter("duration"));
        int teacherId = Integer.parseInt(req.getParameter("teacher"));

        try {
            if (req.getParameter(COURSE_ID) != null) {
                int courseId = Integer.parseInt(req.getParameter(COURSE_ID));
                updateCourse(courseId, name, topic, description, teacherId, duration);
            } else {
                insertCourse(new Course(teacherId, name, topic, description, duration));
            }
        } catch (Exception e) {
            req.setAttribute(MESSAGE, e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/index.jsp");

    }

    private String getTopic(String[] topics) {
        String topic;
        if (topics[1].length() > 0) {
            topic = topics[1];
        } else {
            topic = topics[0];
        }
        return topic;
    }
}
