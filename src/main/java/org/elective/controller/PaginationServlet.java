package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Map;

import static org.elective.logic.StudentsSubtopicManager.changeCompletion;
import static org.elective.utils.Pagination.getNewPageKey;


/**
 * Pagination servlet changes page key.
 */
@WebServlet("/pagination")
public class PaginationServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(PaginationServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Change page...");

        HttpSession session = req.getSession();
        Map<Integer, Subtopic> pages = (Map<Integer, Subtopic>) session.getAttribute("pages");
        int newPageKey = getNewPageKey(req, pages);
        String path = (String) session.getAttribute("path");
        Course course = (Course) session.getAttribute("course");

        if (path.equals("courseContent.jsp")) {
            Map<Integer, String> subtopicCompletion;
            int key = (int) session.getAttribute("pageKey");
            User student = (User) session.getAttribute("user");

            try {
                subtopicCompletion = changeCompletion(key, student, pages, course);
            } catch (Exception e) {
                req.setAttribute("message", e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
                rd.forward(req, resp);
                return;
            }

            session.setAttribute("subtopicCompletion", subtopicCompletion);
        }

        logger.debug("new page key: {}", newPageKey);

        session.setAttribute("pageKey", newPageKey);
        session.setAttribute("path", path);

        resp.sendRedirect(req.getContextPath() + "/" + path);
    }


}
