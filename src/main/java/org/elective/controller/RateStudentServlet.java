package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsCourseDAO;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.StudentsSubtopic;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.StudentsCourseManager.findStudentsCourse;
import static org.elective.logic.StudentsCourseManager.updateGrade;
import static org.elective.logic.StudentsSubtopicManager.*;
import static org.elective.logic.SubtopicManager.findSubtopicsByCourse;

/**
 * Rate student servlet that realized functionality to change students grade by teacher on students page.
 */
@WebServlet("/rate")
public class RateStudentServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(RateStudentServlet.class);
    private static final String ERROR_PAGE = "error.jsp";
    private static final String MESSAGE = "message";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int studentId = Integer.parseInt(req.getParameter("student"));

        int grade;
        if (req.getParameter("grade") != null) {
            grade = Integer.parseInt(req.getParameter("grade"));
        } else {
            logger.error("Chosen invalid grade");
            req.setAttribute(MESSAGE, "ChooseValidGrade");
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        Course course = (Course) session.getAttribute("course");

        logger.debug("Updating student(id={}) rating for course: {}", studentId, course);

        List<StudentsSubtopic> studentsSubtopics;
        List<StudentsSubtopic> finishedSubtopics;
        try {
            studentsSubtopics = findAllStudentsSubtopic(studentId, course.getId());
            finishedSubtopics = getFinishedSubtopics(studentId, course.getId());
        } catch (Exception e) {
            req.setAttribute(MESSAGE, e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        if (finishedSubtopics.size() != studentsSubtopics.size()) {
            logger.debug("This student did not complete course yet");
            req.setAttribute(MESSAGE, "ThisStudentDidNotCompleteCourseYet");
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        try {
            updateGrade(grade, findStudentsCourse(course.getId(), studentId));
        } catch (Exception e) {
            req.setAttribute(MESSAGE, e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/view_course");
    }


}
