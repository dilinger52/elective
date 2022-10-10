package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.DAOFactory;
import org.elective.DBManager.dao.UserDAO;
import org.elective.DBManager.entity.User;

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


@WebServlet("/registration")
public class RegisterServlet extends HttpServlet {

    private static final String ELECTIVE_EMAIL = "elective.org@gmail.com";
    private static final Logger logger = LogManager.getLogger(RegisterServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/registration.jsp");
            rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Starting registration...");
        HttpSession session = req.getSession();
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String password = req.getParameter("password");
        String confpass = req.getParameter("confpass");
        String email = req.getParameter("email");

        if (!firstName.matches("[A-Z][a-z]+")) {
            session.setAttribute("firstName", "firstName must starts with uppercase");
            logger.debug("firstName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }
        if (!lastName.matches("[A-Z][a-z]+")) {
            session.setAttribute("lastName", "lastName must starts with uppercase");
            logger.debug("lastName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }
        if (password.length() < 8) {
            session.setAttribute("password", "password length must be at least 8 symbols");
            logger.debug("password length must be at least 8 symbols");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }
        if (!email.matches("[a-z0-9]+@[a-z]+.[a-z]+")) {
            session.setAttribute("email", "email must contain \"@\" and \".\"");
            logger.debug("email must contain \"@\" and \".\"");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }
        if (!confpass.equals(password)) {
            session.setAttribute("confpass", "passwords doesn't the same");
            logger.debug("passwords doesn't the same");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }
        User user;
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.readByEmail(con, email);
            if (user != null) {
                session.setAttribute("email", "user with this email has already registered");
                resp.sendRedirect(req.getContextPath() + "/registration");
                return;
            }
        } catch (Exception | DBException e) {
            logger.error(e);
            e.printStackTrace();
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
            session.setAttribute("email", email);
            resp.sendRedirect(req.getContextPath() + "confirmEmail.jsp");
        } catch (MessagingException e) {
            logger.error(e);
            e.printStackTrace();
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        }

    }
}
