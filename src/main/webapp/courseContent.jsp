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
        <form action="personal_courses" method="get" >
            <input class="navigation_button" type="submit" value='<fmt:message key="back" />'/>
        </form>
        <div class="headings">
            <h1 class="heading1">
                ${pageKey + 1}.${sessionScope.pages[pageKey].subtopicName}
            </h1>
            <div class="subtopic_info">
                <p class="text">
                    <fmt:message key="courseContent.subtopic" />: ${pageKey + 1} from ${sessionScope.pages.size()}
                </p>
                <p class="text">
                    <fmt:message key="courseContent.status" />: ${subtopicCompletion[pageKey]}
                </p>
            </div>
        </div>
    </div>
    <hr class="separator"/>
    <div class="content">
        <div class="content_sidebar">
            <div class="sidebar_item">
                <div class="item_header">
                    <fmt:message key="courseContent.topic" />:
                </div>
                <div class="item_value">
                    ${course.topic}
                </div>
            </div>
            <div class="sidebar_item">
                <div class="item_header">
                    <fmt:message key="courseContent.teacher" />:
                </div>
                <div class="item_value">
                    <img src="images/Users.svg" class="course_pic">
                    ${coursesTeacher[course.id]}
                </div>
            </div>
            <div class="sidebar_item">
                <div class="item_header">
                    <fmt:message key="courseContent.duration" />:
                </div>
                <div class="item_value">
                    <img src="images/Clock.svg" class="course_pic">
                    ${course.duration}
                </div>
            </div>
        </div>
        <div class="main_information">
            <p class="subtopic_content">
                ${sessionScope.pages[pageKey].subtopicContent}
            </p>
            <form method="post" action="pagination" class="navigation_panel">

                    <button class="navigation_button" name="new_page_key" value="${sessionScope.pageKey - 1}">
                        <fmt:message key="pagination.previous" />
                    </button>

                <div class="page_number_wrapper">
                    <c:forEach var="page" items="${sessionScope.pages}">
                        <button class="navigation_button" name="new_page_key" value="${page.key}">
                            ${page.key + 1}
                        </button>
                    </c:forEach>
                </div>

                    <button class="navigation_button" name="new_page_key" value="${sessionScope.pageKey + 1}">
                        <fmt:message key="pagination.next" />
                    </button>

            </form>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />