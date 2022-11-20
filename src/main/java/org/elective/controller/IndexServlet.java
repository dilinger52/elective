package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.User;
import org.elective.logic.StudentsCourseManager;
import org.elective.utils.Pagination;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.logic.CourseManager.*;
import static org.elective.logic.StudentsCourseManager.findCoursesByCourseId;
import static org.elective.logic.UserManager.findAllTeachers;
import static org.elective.logic.UserManager.findUserById;

/**
 * Index servlet generate catalog page that show all courses to user. Courses can be sorted, filtered and
 * searched by name. Students can register to course and don't see courses that they are already registered.
 * Teachers can go to students page. Administrator can edit or create courses.
 */
@WebServlet("/index.jsp")
public class IndexServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(IndexServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading catalog...");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        List<String> topics;
        List<User> teachers;
        List<Course> courses;
        Map<Integer, Integer> coursesStudents;
        Map<Integer, String> coursesTeacher;
        try {
            topics = findAllTopics();
            teachers = findAllTeachers();

            if (user.getRoleId() == 2) {
                courses = findCoursesByTeacher(user.getId());
            } else {
                courses = findAllCourses();
                if (user.getRoleId() == 3) {
                    courses = getAvailableCourses(user, courses);
                }
            }

            coursesStudents = getCoursesStudents(courses);
            coursesTeacher = getCoursesTeacher(courses);

        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);

        session.setAttribute("topics", topics);
        session.setAttribute("teachers", teachers);
        session.setAttribute("pages", pages);
        session.setAttribute("coursesStudents", coursesStudents);
        session.setAttribute("coursesTeacher", coursesTeacher);
        session.setAttribute("pageKey", 0);
        session.setAttribute("path", "catalog.jsp");
        RequestDispatcher rd = req.getRequestDispatcher("catalog.jsp");
        rd.forward(req, resp);

    }






}
