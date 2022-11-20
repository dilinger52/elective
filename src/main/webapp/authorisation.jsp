<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<!DOCTYPE html>
<html lang="${language}">
<head>
    <title>Good courses</title>
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
            <c:if test="${not empty emailmes}">
                <div class="log_in_input_alert"><fmt:message key="${emailmes}" /></div>
            </c:if>
        </div>
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="password">
                <fmt:message key="login.password.label" />
            </label>
            <input class="log_in_input" type="password"  name="password" id="password" placeholder='<fmt:message key="login.password.placeholder" />' required/>
            <c:if test="${not empty password}">
                <div class="log_in_input_alert"><fmt:message key="${password}" /></div>
            </c:if>
        </div>
        <input class="log_in_button" type="submit" value='<fmt:message key="login.submit" />'/>
        <div class="filter">
            <input class="checkbox" type="checkbox" onclick="showPassword()"/>
            <div class="log_in_text2"><fmt:message key="login.checkbox" /></div>
        </div>
    </form>
    <p class="log_in_text">
        <fmt:message key="login.signup.label" /> <a class="log_in_ref" href="/elective/registration"><fmt:message key="login.signup.submit" /></a>
    </p>
</div>
<div class="footer">
    <hr class="separator"/>
    <form class="local_dropdown">
        <select class="dropdown_local" id="language" name="language" onchange="submit()">
            <option value="en" ${language == 'en' ? 'selected' : ''}>English</option>
            <option value="ru" ${language == 'ru' ? 'selected' : ''}>Russian</option>
        </select>
    </form>
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
