package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.entity.User;
import org.elective.logic.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Authorisation servlet checks the user information and associated user with current session.
 */
@WebServlet("/authorisation")
public class AuthorisationServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(AuthorisationServlet.class);
    private static final String PASSWORD = "password";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RequestDispatcher rd = req.getRequestDispatcher("authorisation.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Log in...");
        HttpSession session = req.getSession();
        String email = req.getParameter("email");
        logger.debug("email: {}", email);
        String password = req.getParameter(PASSWORD);

        User user;
        try {
            user = UserManager.findUserByEmail(email);
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        session.removeAttribute("email");
        session.removeAttribute(PASSWORD);

        if (user == null) {
            session.setAttribute("emailmes", "InvalidEmail");
            logger.debug("invalid email");
            resp.sendRedirect(req.getContextPath() + "/authorisation");
            return;
        }

        if (!password.equals(user.getPassword())) {
            session.setAttribute(PASSWORD, "InvalidPassword");
            logger.debug("invalid password");
            resp.sendRedirect(req.getContextPath() + "/authorisation");
            return;
        }

        session.setAttribute("user", user);
        logger.debug("log in completed");
        resp.sendRedirect(req.getContextPath());

    }



}
