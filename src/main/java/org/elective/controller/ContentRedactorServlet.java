package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.Subtopic;
import org.elective.logic.CourseManager;
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

import static org.elective.utils.Pagination.getSubtopicPages;

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
        int courseId = getCourseId(req, session);

        Map<Integer, Subtopic> pages;
        Course course;
        try {
            course = CourseManager.findCourseById(courseId);
            pages = getSubtopicPages(courseId);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        session.setAttribute("course", course);
        session.setAttribute("pages", pages);
        setPageKey(session, pages);
        session.setAttribute("path", "courseContentRedactor.jsp");

        RequestDispatcher rd = req.getRequestDispatcher("courseContentRedactor.jsp");

        rd.forward(req, resp);
    }

    private void setPageKey(HttpSession session, Map<Integer, Subtopic> pages) {
            if (session.getAttribute("path").equals("new_subtopic")) {
                session.setAttribute("pageKey", pages.size() - 1);
                return;
            }
            if (session.getAttribute("pageKey") == null) {
                session.setAttribute("pageKey", 0);
            }

    }


    private int getCourseId(HttpServletRequest req, HttpSession session) {
        if (req.getParameter("courseId") == null) {
            return ((Course) session.getAttribute("course")).getId();
        } else {
            return Integer.parseInt(req.getParameter("courseId"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Updating course content...");
        int subtopicId = Integer.parseInt(req.getParameter("subtopicId"));
        String subtopicName = req.getParameter("subtopicName");
        String subtopicContent = req.getParameter("subtopicContent");

        try {
            SubtopicManager.updateSubtopic(subtopicId, subtopicName, subtopicContent);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/content_redactor");

    }
}
