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
        <form action="view_course" method="get" >
            <input class="navigation_button" type="submit" value='<fmt:message key="back" />'/>
        </form>
        <div class="headings">
            <h1 class="heading1">
                ${course.name}
            </h1>
        </div>
        <div class="subtopic_count">
            <p class="redactor_subtopic">
                <fmt:message key="courseContent.subtopic" /> ${pageKey + 1}
            </p>
            <div class="topic_buttons">
                <form method="post" action="delete_subtopic">
                    <input type="hidden" name="subtopicId" value="${sessionScope.pages[pageKey].id}"/>
                    <input class="primary_button" type="submit" value='<fmt:message key="courseContentRedactor.delete" />'/>
                </form>
                <input class="primary_button" type="submit" value='<fmt:message key="courseContentRedactor.save" />' form="redactor">
            </div>
        </div>
        <div class="redactor_content">
            <hr class="separator">
            <c:if test="${pages.size() >= 1}">
                <div class="redactor_row">
                    <p class="redactor_row_header">
                        <fmt:message key="courseContentRedactor.subtopicTitle" />
                    </p>
                    <input class="redactor_input_title" form="redactor" name="subtopicName" value="${sessionScope.pages[pageKey].subtopicName}"/>
                </div>
                <hr class="separator">
                <div class="redactor_row">
                    <p class="redactor_row_header">
                        <fmt:message key="courseContentRedactor.subtopicContent" />
                    </p>
                    <form action="content_redactor" method="post" id="redactor">
                        <input type="hidden" name="subtopicId" value="${sessionScope.pages[pageKey].id}"/>
                        <textarea class="redactor_input_content" name="subtopicContent">${sessionScope.pages[pageKey].subtopicContent}</textarea>
                    </form>
                </div>
            </c:if>
                 <form method="post" action="pagination" class="navigation_panel redactor">
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
                     <c:if test="${sessionScope.pageKey < sessionScope.pages.size() - 1}">
                         <button class="navigation_button" name="new_page_key" value="${sessionScope.pageKey + 1}">
                             <fmt:message key="pagination.next" />
                         </button>
                     </c:if>
                     <c:if test="${sessionScope.pageKey >= sessionScope.pages.size() - 1}">
                         <input form="newsub" class="navigation_button" type="submit" value='<fmt:message key="courseContentRedactor.createNew" />'/>
                     </c:if>
                 </form>
            <form method="post" action="new_subtopic" id="newsub">
                <input type="hidden" name="courseId" value="${course.id}">
            </form>
             </div>
    </div>
</div>
    <jsp:include page="footer.jsp" />