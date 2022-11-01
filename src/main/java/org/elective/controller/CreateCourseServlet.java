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
    private static final String COURSE_MESSAGE = "course: {}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading course redactor page...");
        HttpSession session = req.getSession();
        session.removeAttribute("course");
        session.removeAttribute(SELECTED);
        session.removeAttribute("selectedTopic");
        List<User> teachers;
        List<String> topics;
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            teachers = userDAO.getAllTeachers(con);
            topics = courseDAO.getAllTopics(con);
            logger.debug("teachers: {}; topics: {}", teachers, topics);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute(MESSAGE, e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }
        if (req.getParameter(COURSE_ID) != null) {
            int courseId = Integer.parseInt(req.getParameter(COURSE_ID));
            Course course;
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                course = courseDAO.read(con, courseId);
                logger.debug(COURSE_MESSAGE, course);
            } catch (Exception e) {
                logger.error(e);
                e.printStackTrace();
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
        String[] topics = req.getParameterValues("topic");
        String topic;
        if (topics[1].length() > 0) {
            topic = topics[1];
        } else {
            topic = topics[0];
        }
        String description = req.getParameter("description");
        long duration = Long.parseLong(req.getParameter("duration"));
        int teacherId = Integer.parseInt(req.getParameter("teacher"));
        if (req.getParameter(COURSE_ID) != null) {
            int courseId = Integer.parseInt(req.getParameter(COURSE_ID));
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                Course course = courseDAO.read(con, courseId);
                course.setName(name);
                course.setTopic(topic);
                course.setDescription(description);
                course.setTeacherId(teacherId);
                course.setDuration(duration);
                courseDAO.update(con, course);
                logger.debug(COURSE_MESSAGE, course);
            } catch (Exception e) {
                logger.error(e);
                req.setAttribute(MESSAGE, e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
                rd.forward(req, resp);
                return;
            }
        } else {
            Course course = new Course(teacherId, name, topic, description, duration);
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                courseDAO.create(con, course);
                logger.debug(COURSE_MESSAGE, course);
            } catch (Exception e) {
                logger.error(e);
                req.setAttribute(MESSAGE, e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
                rd.forward(req, resp);
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp");

    }
}
