<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<div class="footer">
    <hr class="separator"/>
    <div class="footer_content">
        <p class="text">
            <fmt:message key="footer.copyright" />
        </p>
        <button class="navigation_button" id="scroll" onclick="topFunction()"><fmt:message key="footer.action" /><img src="images/ArrowUp.svg" class="navigation_ico"/></button>
    </div>
</div>
</body>
</html>

<script>
    let mybutton = document.getElementById("scroll");
    function topFunction() {
  document.body.scrollTop = 0; // For Safari
  document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
}
</script>