package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Integer, Subtopic> pages = new HashMap<>();
        Course course;
        Map<Integer, String> subtopicCompletion = new HashMap<>();
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            course = courseDAO.read(con, courseId);
            logger.debug("course properties: {}", course);
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, courseId);
            logger.debug("subtopic properties: {}", subtopics);
            int i = 0;
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            for (Subtopic subtopic :
                    subtopics) {
                pages.put(i, subtopic);
                subtopicCompletion.put(i, studentsSubtopicDAO.read(con, subtopic.getId(), student.getId()).getCompletion());
                i++;
            }
        } catch (Exception e) {
            logger.error(e);
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
