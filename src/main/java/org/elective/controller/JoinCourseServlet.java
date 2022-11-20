package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsCourseDAO;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.StudentsSubtopic;
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
import java.util.Date;
import java.util.List;

import static org.elective.database.DBUtils.close;
import static org.elective.database.DBUtils.rollback;
import static org.elective.logic.StudentsCourseManager.addCourseToStudent;

/**
 * Join course servlet assigned student to course and all subtopics of this course.
 */
@WebServlet("/join_course")
public class JoinCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(JoinCourseServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Joining course...");

        HttpSession session = req.getSession();
        User student = (User) session.getAttribute("user");
        int courseId = Integer.parseInt(req.getParameter("course"));

        try {
            addCourseToStudent(student, courseId);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        }

        resp.sendRedirect(req.getContextPath() + "/personal_courses");

    }
}
