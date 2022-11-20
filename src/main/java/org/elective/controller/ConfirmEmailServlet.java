package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.elective.logic.UserManager.insertUser;

/**
 * Confirm email servlet finish registration of student account. It launches from link in email letter.
 */
@WebServlet("/mailconfirmed")
public class ConfirmEmailServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ConfirmEmailServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Confirming email...");
        HttpSession session = req.getSession();
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        User user = new User(firstName, lastName, password, 3, email);

        logger.debug("User properties: {}", user);

        try {
            insertUser(user);
        } catch (DBException e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        session.setAttribute("user", user);

        resp.sendRedirect(req.getContextPath());
    }
}
