package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.StudentsCourseManager.findCoursesByStudent;
import static org.elective.logic.StudentsSubtopicManager.findStudentsSubtopic;
import static org.elective.logic.SubtopicManager.findSubtopicsByCourse;
import static org.elective.logic.UserManager.blockStudent;
import static org.elective.logic.UserManager.findAllStudents;

/**
 * Students page servlet generates administrator page on which he can view all students, their progress
 * and block students.
 */
@WebServlet("/students")
public class StudentsPageServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(StudentsPageServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading students page...");

        HttpSession session = req.getSession();

        List<User> students;
        Map<Integer, List<StudentsCourse>> registeredCourses = new HashMap<>();
        Map<Integer, List<StudentsCourse>> startedCourses = new HashMap<>();
        Map<Integer, List<StudentsCourse>> finishedCourses = new HashMap<>();
        Map<Integer, Integer> studentsGrade = new HashMap<>();

        try {
            students = findAllStudents();
            for (User student : students) {
                List<StudentsCourse> studentsCourse = findCoursesByStudent(student.getId());
                List<StudentsCourse> rc = new ArrayList<>();
                List<StudentsCourse> sc = new ArrayList<>();
                List<StudentsCourse> fc = new ArrayList<>();
                for (StudentsCourse course : studentsCourse) {
                    List<Subtopic> subtopics = findSubtopicsByCourse(course.getCourseId());

                    List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();
                    for (Subtopic subtopic : subtopics) {
                        studentsSubtopics.add(findStudentsSubtopic(subtopic.getId(), student.getId()));
                    }

                    List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                            .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                            .collect(Collectors.toList());

                    if (finishedSubtopics.size() == studentsSubtopics.size()) {
                        fc.add(course);
                    } else if (finishedSubtopics.isEmpty()) {
                        rc.add(course);
                    } else {
                        sc.add(course);
                    }
                }
                registeredCourses.put(student.getId(), rc);
                startedCourses.put(student.getId(), sc);
                finishedCourses.put(student.getId(), fc);

                int averageGrade = (int) (fc.stream().mapToInt(c -> (int) c.getGrade()).average()).orElse(-1);
                studentsGrade.put(student.getId(), averageGrade);
            }

        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        session.setAttribute("students", students);
        session.setAttribute("registeredCourses", registeredCourses);
        session.setAttribute("startedCourses", startedCourses);
        session.setAttribute("finishedCourses", finishedCourses);
        session.setAttribute("studentsGrade", studentsGrade);
        RequestDispatcher rd = req.getRequestDispatcher("adminStudentsPage.jsp");

        rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int studentId = Integer.parseInt(req.getParameter("student"));
        logger.debug("Blocking student id={}", studentId);

        try {
            blockStudent(studentId);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/students");
    }
}
