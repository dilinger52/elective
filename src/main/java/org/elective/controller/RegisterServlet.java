package org.elective.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.UserDAO;
import org.elective.database.entity.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;


/**
 * Register servlet that generate registration page. This page has validation of inserted information and send
 * letter to email with link to complete registration.
 */
@WebServlet("/registration")
public class RegisterServlet extends HttpServlet {

    private static final String ELECTIVE_EMAIL = "elective.org@gmail.com";
    private static final Logger logger = LogManager.getLogger(RegisterServlet.class);
    private static final String FIRST_NAME = "firstName";
    private static final String EMAIL = "email";
    private static final String LAST_NAME = "lastName";
    private static final String PASSWORD = "password";
    private static final String CONFPASS = "confpass";
    private static final String REGISTRATION_PAGE = "/registration.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.removeAttribute(EMAIL);
        session.removeAttribute(FIRST_NAME);
        session.removeAttribute(LAST_NAME);
        session.removeAttribute(PASSWORD);
        session.removeAttribute(CONFPASS);
        RequestDispatcher rd = req.getRequestDispatcher("registration.jsp");
        rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Starting registration...");
        HttpSession session = req.getSession();
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String password = req.getParameter(PASSWORD);
        String confpass = req.getParameter(CONFPASS);
        String email = req.getParameter(EMAIL);

        session.removeAttribute(EMAIL);
        session.removeAttribute(FIRST_NAME);
        session.removeAttribute(LAST_NAME);
        session.removeAttribute(PASSWORD);
        session.removeAttribute(CONFPASS);

        if (!firstName.matches("[A-Z][a-z]+")) {
            session.setAttribute(FIRST_NAME, "FirstNameMustStartsWithUppercase");
            logger.debug("firstName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
            return;
        }
        if (!lastName.matches("[A-Z][a-z]+")) {
            session.setAttribute(LAST_NAME, "LastNameMustStartsWithUppercase");
            logger.debug("lastName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
            return;
        }
        if (password.length() < 8) {
            session.setAttribute(PASSWORD, "PasswordLengthMustBeAtLeast8Symbols");
            logger.debug("password length must be at least 8 symbols");
            resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
            return;
        }
        if (!email.matches("[a-z0-9]+@[a-z]+.[a-z]+")) {
            session.setAttribute(EMAIL, "EmailMustContain@And.");
            logger.debug("email must contain \"@\" and \".\"");
            resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
            return;
        }
        if (!confpass.equals(password)) {
            session.setAttribute(CONFPASS, "PasswordsDoesn'tTheSame");
            logger.debug("passwords doesn't the same");
            resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
            return;
        }
        User user;
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.readByEmail(con, email);
            if (user != null) {
                session.setAttribute(EMAIL, "UserWithThiEmailHasAlreadyRegistered");
                resp.sendRedirect(req.getContextPath() + REGISTRATION_PAGE);
                return;
            }
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }


        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session mailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ELECTIVE_EMAIL, "gzevhywxoyxveziw");
            }
        });
        try {
            logger.debug("Preparing message...");
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(ELECTIVE_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Confirm your email!");
            message.setText("To confirm our email click on the link below:\n" +
                    "http://localhost:8080/elective/mailconfirmed?first_name=" + firstName +
                    "&last_name=" + lastName +
                    "&password=" + password +
                    "&email=" + email);
            Transport.send(message);
            logger.debug("message send");
            session.setAttribute(EMAIL, email);
            resp.sendRedirect(req.getContextPath() + "/" + "confirmEmail.jsp");
        } catch (MessagingException e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        }

    }
}
