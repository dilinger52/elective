package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.CourseDAO;
import org.elective.DBManager.dao.DAOFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/delete_course")
public class DeleteCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DeleteCourseServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Deleting course...");
        int courseId = Integer.parseInt(req.getParameter("courseId"));
        try(Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            courseDAO.delete(con, courseId);
            logger.debug("Successfully deleting");
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
            resp.sendRedirect(req.getContextPath() + "index.jsp");

    }
}
