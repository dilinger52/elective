<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<html>
<head>
    <link rel="stylesheet" href="main.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<body>
<div class="log_in_wrapper">
    <img class="logo_symbol" src="images/LogoSymbol.svg">
    <h1 class="log_in_header"><fmt:message key="login.header" /></h1>
    <p class="log_in_text"><fmt:message key="login.description" /></p>
    <form class="log_in_form" action="authorisation" method="post">
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="email">
                <fmt:message key="login.email.label" />
            </label>
            <input class="log_in_input" type="email" id="email" name="email" placeholder='<fmt:message key="login.email.placeholder" />' required/>
            <c:if test="${not empty email}">
                <p class="log_in_input_alert">${email}</p>
            </c:if>
        </div>
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="password">
                <fmt:message key="login.password.label" />
            </label>
            <input class="log_in_input" type="password"  name="password" id="password" placeholder='<fmt:message key="login.password.placeholder" />' required/>
            <c:if test="${not empty password}">
                <p class="log_in_input_alert">${password}</p>
            </c:if>
        </div>
        <input class="log_in_button" type="submit" value="Log in"/>
        <div class="filter">
            <input class="checkbox" type="checkbox" onclick="showPassword()"/>
            <p class="log_in_text"><fmt:message key="login.checkbox" /></p>
        </div>
    </form>
    <p class="log_in_text">
        <fmt:message key="login.signup.label" /> <a class="log_in_ref" href="/elective/registration"><fmt:message key="login.signup.submit" /></a>
    </p>
</div>

<script>
    function showPassword() {
    var x = document.getElementById("password");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}
</script>
</body>
</html>
