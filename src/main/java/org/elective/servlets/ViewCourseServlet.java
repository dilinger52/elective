package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;

/**
 * View course servlet generates teacher page on which he can view all students registered on current course,
 * their progress and change grade of students that completed that course. Also, from this page teacher can go
 * to content redactor page.
 */
@WebServlet("/view_course")
public class ViewCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ViewCourseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading course page...");
        HttpSession session = req.getSession();
        int courseId;
        if (req.getParameter("courseId") == null) {
            courseId = ((Course) session.getAttribute("course")).getId();
        } else {
            courseId = Integer.parseInt(req.getParameter("courseId"));
        }
        List<User> students = new ArrayList<>();
        List<StudentsCourse> coursesStudents;
        Course course;
        Map<Integer, Map<String, String>> studentsGrade = new HashMap<>();
        Map<Integer, Integer> studentsCoursesNum = new HashMap<>();
        Map<Integer, Integer> finishedCoursesNum = new HashMap<>();

        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            UserDAO userDAO = daoFactory.getUserDAO();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            course = courseDAO.read(con, courseId);
            coursesStudents = studentsCourseDAO.findCoursesByCourseId(con, courseId);
            for (StudentsCourse coursesStudent : coursesStudents) {
                students.add(userDAO.read(con, coursesStudent.getStudentId()));
                List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getId());
                List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();
                for (Subtopic subtopic : subtopics) {
                    studentsSubtopics.add(studentsSubtopicDAO.read(con, subtopic.getId(), coursesStudent.getStudentId()));
                }
                List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                        .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                        .collect(Collectors.toList());
                finishedCoursesNum.put(coursesStudent.getStudentId(), finishedSubtopics.size());
                studentsCoursesNum.put(coursesStudent.getStudentId(), studentsSubtopics.size());
                Map<String, String> checked = new HashMap<>();
                for (int i = 1; i <= 5; i++) {
                    if (coursesStudent.getGrade() == i) {
                        checked.put("i" + i, "checked");
                    } else {
                        checked.put("i" + i, "");
                    }

                }

                studentsGrade.put(coursesStudent.getStudentId(), checked);
                logger.debug("Loading successfully");
            }
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        session.setAttribute("course", course);
        session.setAttribute("students", students);
        session.setAttribute("finishedCoursesNum", finishedCoursesNum);
        session.setAttribute("studentsCoursesNum", studentsCoursesNum);
        session.setAttribute("studentsGrade", studentsGrade);
        RequestDispatcher rd = req.getRequestDispatcher("teacherCoursePage.jsp");
        rd.forward(req, resp);

    }
}
