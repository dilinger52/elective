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
        <div class="headings">
            <h1 class="heading1">
                <fmt:message key="adminStudentPage.head.header" />
            </h1>
            <p class="heading2">
                <fmt:message key="adminStudentPage.head.description" />
            </p>
        </div>
    </div>
    <div class="content">
        <table class="table">
            <tr class="table_header">
                <th class="header_item1">
                    <fmt:message key="adminStudentPage.table.name" />
                </th>
                <th class="header_item">
                    <fmt:message key="adminStudentPage.table.registerCourses" />
                </th>
                <th class="header_item">
                    <fmt:message key="adminStudentPage.table.startedCourses" />
                </th>
                <th class="header_item">
                    <fmt:message key="adminStudentPage.table.finishedCourses" />
                </th>
                <th class="header_item">
                    <fmt:message key="adminStudentPage.table.blocked" />
                </th>
                <th class="header_item1">
                    <fmt:message key="adminStudentPage.table.averageGrade" />
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
                    <td class="student_name">
                        ${registeredCourses[student.id].size()}
                    </td>
                    <td class="student_name">
                        ${startedCourses[student.id].size()}
                    </td>
                    <td class="student_name">
                        ${finishedCourses[student.id].size()}
                    </td>
                    <td class="student_name">
                        ${student.isBlocked()}
                    </td>
                    <td>
                    <form method="post" action="students" class="rating_form">
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
                        <c:if test="${student.isBlocked() == 'true'}" >
                            <input type="submit" class="primary_button" value='<fmt:message key="adminStudentPage.action.unblock" />'/>
                        </c:if>
                        <c:if test="${student.isBlocked() == 'false'}" >
                            <input type="submit" class="primary_button" value='<fmt:message key="adminStudentPage.action.block" />'/>
                        </c:if>
                    </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

<jsp:include page="footer.jsp" />