package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;
import org.elective.logic.CourseManager;
import org.elective.utils.Pagination;
import org.elective.utils.Sorting;

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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.CourseManager.findCourseById;
import static org.elective.logic.CourseManager.getAvailableCourses;
import static org.elective.logic.StudentsCourseManager.findCoursesByStudent;
import static org.elective.utils.Filter.*;
import static org.elective.utils.Pagination.pageConstructor;

/**
 * Filter servlet realized filtering courses in different pages.
 */
@WebServlet("/filter")
public class FilterServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(FilterServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Starting filtering...");
        String[] topics = req.getParameterValues("topic");
        String[] teachers = req.getParameterValues("teacher");
        String[] completions = req.getParameterValues("completion");
        String sortingPattern = req.getParameter("sorting_pattern");
        String pattern = req.getParameter("pattern");
        HttpSession session = req.getSession();
        String path = (String) session.getAttribute("path");
        User user = (User) session.getAttribute("user");

        List<Course> courses;
        try {
            if ("personalCourses.jsp".equals(path)) {
                courses = CourseManager.findCoursesByStudent(user.getId());
            } else if (user.getRoleId() == 2) {
                courses = CourseManager.findCoursesByTeacher(user.getId());
            } else {
                courses = CourseManager.findAllCourses();
                if (user.getRoleId() == 3) {
                    courses = getAvailableCourses(user, courses);
                }
            }
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        courses = filterName(pattern, courses);
        courses = filterTopic(topics, courses);
        courses = filterTeachers(teachers, courses);
        try {
            courses = filterCompletions(completions, courses, user);
        } catch (DBException e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        courses = Sorting.sort(session, courses, sortingPattern);

        logger.debug("courses after filter: {}", courses);

        session.setAttribute("pages", pageConstructor(courses));
        session.setAttribute("pageKey", 0);
        RequestDispatcher rd = req.getRequestDispatcher(path);
        session.setAttribute("path", path);
        rd.forward(req, resp);

    }


}
