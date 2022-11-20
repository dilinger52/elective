package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

import static org.elective.logic.CourseManager.deleteCourse;

/**
 * Delete course servlet realizes mechanism deleting course at course redactor page.
 */
@WebServlet("/delete_course")
public class DeleteCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DeleteCourseServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Deleting course...");
        int courseId = Integer.parseInt(req.getParameter("courseId"));

        try {
            deleteCourse(courseId);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/index.jsp");

    }
}
