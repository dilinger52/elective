package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;
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

/**
 * Personal courses servlet generates page that show all courses on which current student has registered.
 * On this page student can sort, filter and search courses by name.
 */
@WebServlet("/personal_courses")
public class PersonalCoursesServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(PersonalCoursesServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading personal courses...");
        List<String> topics;
        List<User> teachers;
        Map<Integer, String> coursesTeacher = new HashMap<>();
        Map<Integer, Date> coursesRegistrationDate = new HashMap<>();
        Map<Integer, Integer> studentsCoursesNum = new HashMap<>();
        Map<Integer, Integer> finishedCoursesNum = new HashMap<>();
        Map<Integer, Long> coursesGrade = new HashMap<>();

        List<StudentsCourse> studentsCourses;
        List<Course> courses = new ArrayList<>();
        HttpSession session = req.getSession();
        User student = (User) session.getAttribute("user");
        logger.debug("Loading personal courses for student: {}", student);
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            UserDAO userDAO = daoFactory.getUserDAO();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            topics = courseDAO.getAllTopics(con);
            teachers = userDAO.getAllTeachers(con);
            studentsCourses = studentsCourseDAO.findCoursesByStudentId(con, student.getId());
            for (StudentsCourse course : studentsCourses) {
                Course c = courseDAO.read(con, course.getCourseId());
                courses.add(c);
                User teacher = userDAO.read(con, c.getTeacherId());
                coursesTeacher.put(c.getId(), teacher.getFirstName() + " " +
                        teacher.getLastName());
                coursesRegistrationDate.put(c.getId(), course.getRegistrationDate());
                List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getCourseId());
                List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();
                for (Subtopic subtopic : subtopics) {
                    studentsSubtopics.add(studentsSubtopicDAO.read(con, subtopic.getId(), student.getId()));
                }
                List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                        .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                        .collect(Collectors.toList());
                finishedCoursesNum.put(c.getId(), finishedSubtopics.size());
                studentsCoursesNum.put(c.getId(), studentsSubtopics.size());
                coursesGrade.put(c.getId(), course.getGrade());
            }
            logger.debug("courses: {}", courses);
            Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);

            session.setAttribute("topics", topics);
            session.setAttribute("teachers", teachers);
            session.setAttribute("courses", courses);
            session.setAttribute("coursesTeacher", coursesTeacher);
            session.setAttribute("coursesRegistrationDate", coursesRegistrationDate);
            session.setAttribute("finishedCoursesNum", finishedCoursesNum);
            session.setAttribute("studentsCoursesNum", studentsCoursesNum);
            session.setAttribute("coursesGrade", coursesGrade);
            session.setAttribute("pages", pages);
            session.setAttribute("pageKey", 0);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        session.setAttribute("path", "personalCourses.jsp");
        RequestDispatcher rd = req.getRequestDispatcher("personalCourses.jsp");
        rd.forward(req, resp);
    }
}
