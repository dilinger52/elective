package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;

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
 * Content redactor servlet generate page of redacting subtopics for teachers. Also, from this page teacher
 * can create and delete subtopics.
 */
@WebServlet("/content_redactor")
public class ContentRedactorServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ContentRedactorServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading redactor page...");
        HttpSession session = req.getSession();
        int courseId;
        if (req.getParameter("courseId") == null) {
            courseId = ((Course) session.getAttribute("course")).getId();
        } else {
            courseId = Integer.parseInt(req.getParameter("courseId"));
        }
        Map<Integer, Subtopic> pages = new HashMap<>();
        Course course;
        try {
            DAOFactory daoFactory = DAOFactory.getInstance();
            Connection con = DBUtils.getInstance().getConnection();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            course = courseDAO.read(con, courseId);
            logger.debug("course properties: {}", course);
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, courseId);
            logger.debug("subtopics properties: {}", subtopics);
            for (int i = 0; i < subtopics.size(); i++) {
                pages.put(i, subtopics.get(i));
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
        if (session.getAttribute("path").equals("new_subtopic")) {
            session.setAttribute("pageKey", pages.size() - 1);
        } else {
            session.setAttribute("pageKey", 0);
        }
        session.setAttribute("path", "courseContentRedactor.jsp");
        RequestDispatcher rd = req.getRequestDispatcher("courseContentRedactor.jsp");

        rd.forward(req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Updating course content...");
        int subtopicId = Integer.parseInt(req.getParameter("subtopicId"));
        String subtopicName = req.getParameter("subtopicName");
        String subtopicContent = req.getParameter("subtopicContent");
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            Subtopic subtopic = subtopicDAO.read(con, subtopicId);
            subtopic.setSubtopicName(subtopicName);
            subtopic.setSubtopicContent(subtopicContent);
            subtopicDAO.update(con, subtopic);
            logger.debug("subtopic properties: {}", subtopic);
        } catch (Exception e) {
            logger.debug(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/content_redactor");

    }
}
