<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
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
<div class="header" id="header">
    <div class="header_content">
        <a class="logo" href="index.jsp">
                <img src="images/logo.svg" />
        </a>
        <div class="navigation">
            <div class="links">
                <a class="link" href="index.jsp"><fmt:message key="header.links.all_courses" /></a>
                <c:if test="${sessionScope.user.roleId==3}" >
                    <a class="link" href="/elective/personal_courses"><fmt:message key="header.links.my_courses" /></a>
                </c:if>
                <c:if test="${sessionScope.user.roleId==1}" >
                    <a class="link" href="/elective/students"><fmt:message key="header.links.students" /></a>
                </c:if>
                <form>
                    <select class="dropdown_local" id="language" name="language" onchange="submit()">
                        <option value="ru" ${language == 'ru' ? 'selected' : ''}>ru</option>
                        <option value="en" ${language == 'en' ? 'selected' : ''}>en</option>
                    </select>
                    <input type="hidden" name="pattern" value="${param.pattern}">
                    <input type="hidden" name="courseId" value="${not empty param.courseId ? param.courseId : course.id}">
                    <c:forEach var="t" items="${paramValues.topic}">
                        <input type="hidden" name="topic" value="${t}">
                    </c:forEach>
                    <c:forEach var="t" items="${paramValues.teacher}">
                        <input type="hidden" name="teacher" value="${t}">
                    </c:forEach>
                    <c:forEach var="c" items="${paramValues.completion}">
                        <input type="hidden" name="completion" value="${c}">
                    </c:forEach>
                </form>

            </div>
            <div class="actions">
                <div class="student_info">
                    <div class="student_name">
                        ${user.firstName} ${user.lastName}
                    </div>
                    <div class="student_email">
                        ${user.email}
                    </div>
                </div>
                <form action="log_out" method="post">
                    <input type="submit" value="<fmt:message key="header.button.submit" />" class="primary_button"/>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    window.onscroll = function(){headerFunction()};

    var header = document.getElementById("header");
    var sticky = header.offsetTop;

    function headerFunction() {
    if (window.pageYOffset > sticky) {
       header.classList.add("sticky");
    } else {
        header.classList.remove("sticky");
    }
}
</script>