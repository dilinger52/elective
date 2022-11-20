package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;
import org.elective.logic.CourseManager;
import org.elective.utils.Pagination;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.CourseManager.*;
import static org.elective.logic.StudentsCourseManager.*;
import static org.elective.logic.StudentsSubtopicManager.findStudentsSubtopic;
import static org.elective.logic.StudentsSubtopicManager.getFinishedSubtopicsNum;
import static org.elective.logic.SubtopicManager.findSubtopicsByCourse;
import static org.elective.logic.SubtopicManager.getStudentsSubtopicsNum;
import static org.elective.logic.UserManager.findAllTeachers;
import static org.elective.logic.UserManager.findUserById;

/**
 * Personal courses servlet generates page that show all courses on which current student has registered.
 * On this page student can sort, filter and search courses by name.
 */
@WebServlet("/personal_courses")
public class PersonalCoursesServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(PersonalCoursesServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User student = (User) session.getAttribute("user");

        logger.debug("Loading personal courses for student: {}", student);

        List<String> topics;
        List<User> teachers;
        List<Course> courses;
        Map<Integer, String> coursesTeacher;
        Map<Integer, Date> coursesRegistrationDate;
        Map<Integer, Integer> studentsSubtopicNum;
        Map<Integer, Integer> finishedSubtopicsNum;
        Map<Integer, Long> coursesGrade;
        try {
            topics = findAllTopics();
            teachers = findAllTeachers();
            courses = CourseManager.findCoursesByStudent(student.getId());
            coursesTeacher = getCoursesTeacher(courses);
            coursesRegistrationDate = getCoursesRegistrationDate(courses, student);
            studentsSubtopicNum = getStudentsSubtopicsNum(courses);
            finishedSubtopicsNum = getFinishedSubtopicsNum(courses, student);
            coursesGrade = getCoursesGrade(courses, student);

            logger.debug("courses: {}", courses);

        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);

        session.setAttribute("topics", topics);
        session.setAttribute("teachers", teachers);
        session.setAttribute("courses", courses);
        session.setAttribute("coursesTeacher", coursesTeacher);
        session.setAttribute("coursesRegistrationDate", coursesRegistrationDate);
        session.setAttribute("finishedSubtopicsNum", finishedSubtopicsNum);
        session.setAttribute("studentsSubtopicNum", studentsSubtopicNum);
        session.setAttribute("coursesGrade", coursesGrade);
        session.setAttribute("pages", pages);
        session.setAttribute("pageKey", 0);
        session.setAttribute("path", "personalCourses.jsp");
        RequestDispatcher rd = req.getRequestDispatcher("personalCourses.jsp");
        rd.forward(req, resp);
    }

}
