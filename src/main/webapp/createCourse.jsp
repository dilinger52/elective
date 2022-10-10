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
        <c:if test="${course != null}">
            <h1 class="log_in_header"><fmt:message key="createCourse.head.header" /></h1>
            <p class="log_in_text"><fmt:message key="createCourse.head.description" /></p>
        </c:if>
        <c:if test="${course == null}">
        <h1 class="log_in_header"><fmt:message key="createCourse.head.altHeader" /></h1>
        <p class="log_in_text"><fmt:message key="createCourse.head.altDescription" /></p>
        </c:if>
    <form class="log_in_form" action="create_course" method="post" id="create">

        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="name"><fmt:message key="createCourse.name.label" /></label>
            <input class="log_in_input" id="name" type="text" name="name" value="${course.name}" placeholder='<fmt:message key="createCourse.name.placeholder" />' required/>
        </div>
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="topic"><fmt:message key="createCourse.topic.label" /></label>
            <input class="log_in_input" id="topic" type="text" name="topic" value="${course.topic}" placeholder='<fmt:message key="createCourse.topic.placeholder" />' required/>
        </div>
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="duration"><fmt:message key="createCourse.duration.label" /></label>
            <input class="log_in_input" id="duration" type="number" name="duration" value="${course.duration}" placeholder='<fmt:message key="createCourse.duration.placeholder" />' required/>
        </div>
        <div class="log_in_input_wrapper">
            <label class="log_in_label" for="description"><fmt:message key="createCourse.description.label" /></label>
            <input class="log_in_input" id="description" type="text" name="description" value="${course.description}" placeholder='<fmt:message key="createCourse.description.placeholder" />' required/>
        </div>
        <select name="teacher" class="dropdown">
            <option class="option" value="0"><fmt:message key="createCourse.teacher.default" /></option>
            <c:forEach var="teacher" items="${teachers}">
                <option class="option" value="${teacher.id}" ${selected[teacher.id]}>${teacher.firstName} ${teacher.lastName}</option>
            </c:forEach>
        </select>
    </form>
        <form action="create_teacher" method="get">
            <input type="submit" value='<fmt:message key="createCourse.action.newTeacher" />' class="primary_button"/>
        </form>

        <c:if test="${course != null}">
            <div class="log_in_form">
            <input class="log_in_button" type="submit" value='<fmt:message key="createCourse.action.confirm" />' form="create"/>
            <input type="hidden" name="courseId" value="${course.id}" form="create"/>
            </div>
            <form method="post" action="delete_course">
                <input type="hidden" name="courseId" value="${course.id}"/>
                <input class="log_in_button" type="submit" value='<fmt:message key="createCourse.action.delete" />'/>
            </form>
        </c:if>
        <div class="log_in_form">
        <c:if test="${course == null}">
            <input class="log_in_button" type="submit" value='<fmt:message key="createCourse.action.create" />' form="create"/>
        </c:if>
        </div>
    <form action="index.jsp" method="get" >
        <input class="navigation_button" type="submit" value='<fmt:message key="back" />'/>
    </form>
    </div>
</body>
</html>
