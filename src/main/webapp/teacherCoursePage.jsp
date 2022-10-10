<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<jsp:include page="header.jsp" />
<div class="page">
    <div class="heading_container">
        <form action="index.jsp" method="get" >
            <input class="navigation_button" type="submit" value='<fmt:message key="back" />'/>
        </form>
        <div class="headings">
            <h1 class="heading1">
                ${sessionScope.course.name}
            </h1>
            <p class="heading2">
                ${sessionScope.course.description}
            </p>
            <div class="course_detail_wrapper">
                <div class="course_detail additional">
                    <div class="course_tag">
                        ${sessionScope.course.topic}
                    </div>
                    <div class="teacher">
                        <img src="images/Teacher.svg" class="course_pic">
                        <fmt:message key="catalog.you" />
                    </div>
                    <div class="teacher">
                        <img src="images/Users.svg" class="course_pic">
                        ${students.size()}
                    </div>
                    <div class="teacher">
                        <img src="images/Clock.svg" class="course_pic">
                        ${course.duration}
                    </div>
                </div>
                <form method="get" action="content_redactor">
                    <input type="hidden" name="courseId" value="${course.id}">
                    <input class="primary_button" type="submit" value='<fmt:message key="teacherCoursePage.action.edit" />'>
                </form>
            </div>
        </div>

    </div>
    <hr class="separator">

    <div class="content">
        <div class="content_sidebar">
            <div class="sidebar_item">
                <div class="item_value">
                    <fmt:message key="teacherCoursePage.sidebar" />
                </div>
            </div>
        </div>
        <table class="table">
            <tr class="table_header">
                <th class="header_item1">
                    <fmt:message key="teacherCoursePage.table.name" />
                </th>
                <th class="header_item">
                    <fmt:message key="teacherCoursePage.table.progress" />
                </th>
                <th class="header_item1">
                    <fmt:message key="teacherCoursePage.table.status" />
                </th>
            </tr>
            <c:forEach var="student" items="${sessionScope.students}">
                <tr class="table_row">
                    <td>
                        <div class="student_info">
                            <div class="student_name">
                                ${student.firstName} ${student.lastName}
                            </div>
                            <div class="student_email">
                                ${student.email}
                            </div>
                        </div>
                    </td>
                    <td class="student_progress_wrapper">
                        <progress class="student_progress" value="${sessionScope.finishedCoursesNum[student.id]}" max="${sessionScope.studentsCoursesNum[student.id]}"></progress>
                        ${sessionScope.finishedCoursesNum[student.id]} / ${sessionScope.studentsCoursesNum[student.id]}
                    </td>
                    <td>
                        <form action="rate" method="post" class="rating_form">
                            <input type="hidden" name="student" value="${student.id}" />
                            <div class="rating">
                                <input id="radio1${student.id}" type="radio" name="grade" value="5" class="star" ${studentsGrade[student.id].i5}/>
                                <label for="radio1${student.id}">&#9733;</label>
                                <input id="radio2${student.id}" type="radio" name="grade" value="4" class="star" ${studentsGrade[student.id].i4}/>
                                <label for="radio2${student.id}">&#9733;</label>
                                <input id="radio3${student.id}" type="radio" name="grade" value="3" class="star" ${studentsGrade[student.id].i3}/>
                                <label for="radio3${student.id}">&#9733;</label>
                                <input id="radio4${student.id}" type="radio" name="grade" value="2" class="star" ${studentsGrade[student.id].i2}/>
                                <label for="radio4${student.id}">&#9733;</label>
                                <input id="radio5${student.id}" type="radio" name="grade" value="1" class="star" ${studentsGrade[student.id].i1}/>
                                <label for="radio5${student.id}">&#9733;</label>
                            </div>
                            <input type="submit" class="primary_button" value='<fmt:message key="teacherCoursePage.action.rate" />'/>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

<jsp:include page="footer.jsp" />