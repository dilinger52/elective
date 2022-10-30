package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.SubtopicDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * Delete subtopic servlet realizes mechanism deleting subtopic at content redactor page.
 */
@WebServlet("/delete_subtopic")
public class DeleteSubtopicServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DeleteSubtopicServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Deleting subtopic...");
        int subtopicId = Integer.parseInt(req.getParameter("subtopicId"));
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            subtopicDAO.delete(con, subtopicId);
            logger.debug("Successfully deleting");
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/content_redactor");

    }
}
