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
        <h1 class="log_in_header"><fmt:message key="createTeacher.head.header" /></h1>
        <p class="log_in_text"><fmt:message key="createTeacher.head.description" /></p>

    <form class="log_in_form" action="create_teacher" method="post">
    <jsp:include page="registerForm.jsp" />
    </div>
</body>
</html>
