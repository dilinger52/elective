package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.elective.logic.UserManager.findUserByEmail;
import static org.elective.logic.UserManager.insertUser;

/**
 * Create teacher servlet generate page of creating teacher account by administrator. Contains validations for
 * all inserted information.
 */
@WebServlet("/create_teacher")
public class CreateTeacherServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CreateTeacherServlet.class);
    private static final String PASSWORD = "password";
    private static final String CONFPASS = "confpass";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String CREATE_TEACHER_PAGE = "/create_teacher";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("createTeacher.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Creating teacher...");

        HttpSession session = req.getSession();
        String firstName = req.getParameter(FIRST_NAME);
        String lastName = req.getParameter(LAST_NAME);
        String password = req.getParameter(PASSWORD);
        String confpass = req.getParameter(CONFPASS);
        String email = req.getParameter(EMAIL);

        session.removeAttribute(EMAIL);
        session.removeAttribute(FIRST_NAME);
        session.removeAttribute(LAST_NAME);
        session.removeAttribute(PASSWORD);
        session.removeAttribute(CONFPASS);

        if (!firstName.matches("[A-ZА-Я][a-zа-я]+")) {
            session.setAttribute("firstName", "FirstNameMustStartsWithUppercase");
            logger.debug("firstName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
            return;
        }

        if (!lastName.matches("[A-ZА-Я][a-zа-я]+")) {
            session.setAttribute("lastName", "LastNameMustStartsWithUppercase");
            logger.debug("lastName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
            return;
        }

        if (password.length() < 8) {
            session.setAttribute(PASSWORD, "PasswordLengthMustBeAtLeast8Symbols");
            logger.debug("password length must be at least 8 symbols");
            resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
            return;
        }

        if (!email.matches("[a-z0-9]+@[a-z]+.[a-z]+")) {
            session.setAttribute(EMAIL, "EmailMustContain@And.");
            logger.debug("email must contain \"@\" and \".\"");
            resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
            return;
        }

        if (!confpass.equals(password)) {
            session.setAttribute(CONFPASS, "PasswordsDoesn'tTheSame");
            logger.debug("passwords doesn't the same");
            resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
            return;
        }

        try {
            User user = findUserByEmail(email);
            if (user != null) {
                session.setAttribute(EMAIL, "UserWithThisEmailHasAlreadyRegistered");
                resp.sendRedirect(req.getContextPath() + CREATE_TEACHER_PAGE);
                return;
            }

            insertUser(new User(firstName, lastName, password, 2, email));
        } catch (Exception e) {
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/create_course");
    }
}
