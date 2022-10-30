package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.entity.Course;
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
import java.util.List;
import java.util.Map;

/**
 * Search servlet realizes mechanism of search string and searching courses by name.
 */
@WebServlet("/search_by_name")
public class SearchServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(SearchServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        String path = (String) session.getAttribute("path");
        String pattern = req.getParameter("pattern");
        logger.debug("Searching by pattern: {}", pattern);
        List<Course> courses;
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            courses = courseDAO.findByName(con, pattern);
            logger.debug("Searched courses: {}", courses);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);
        session.setAttribute("pages", pages);
        session.setAttribute("pageKey", 0);
        RequestDispatcher rd = req.getRequestDispatcher(path);
        session.setAttribute("path", path);
        rd.forward(req, resp);

    }
}
