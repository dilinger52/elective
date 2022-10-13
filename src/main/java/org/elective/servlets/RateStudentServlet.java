package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.DAOFactory;
import org.elective.DBManager.dao.StudentsCourseDAO;
import org.elective.DBManager.dao.StudentsSubtopicDAO;
import org.elective.DBManager.dao.SubtopicDAO;
import org.elective.DBManager.entity.Course;
import org.elective.DBManager.entity.StudentsCourse;
import org.elective.DBManager.entity.StudentsSubtopic;
import org.elective.DBManager.entity.Subtopic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elective.DBManager.entity.StudentsSubtopic.completion.COMPLETED;

@WebServlet("/rate")
public class RateStudentServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(RateStudentServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int studentId = Integer.parseInt(req.getParameter("student"));
        int grade;
        if (req.getParameter("grade") != null) {
            grade = Integer.parseInt(req.getParameter("grade"));
        } else {
            logger.error("Choose valid grade");
            req.setAttribute("message", "ChooseValidGrade");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        HttpSession session = req.getSession();
        Course course = (Course) session.getAttribute("course");
        logger.debug("Updating student(id={}) rating for course: {}", studentId, course);
        StudentsCourse studentsCourse;
        List<StudentsSubtopic> studentsSubtopics;
        try(Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            studentsCourse = studentsCourseDAO.read(con, course.getId(), studentId);
            List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getId());
            studentsSubtopics = new ArrayList<>();
            for (Subtopic subtopic : subtopics) {
                studentsSubtopics.add(studentsSubtopicDAO.read(con, subtopic.getId(), studentId));
            }

        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                .collect(Collectors.toList());
        if (finishedSubtopics.size() == studentsSubtopics.size()) {
            studentsCourse.setGrade(grade);
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
                studentsCourseDAO.update(con, studentsCourse);
                logger.debug("setting new garde: {}", grade);
            } catch (Exception e) {
                logger.error(e);
                req.setAttribute("message", e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
                rd.forward(req, resp);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/view_course");

        } else {
            logger.debug("This student did not complete course yet");
            req.setAttribute("message", "ThisStudentDidNotCompleteCourseYet");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        }

    }
}
