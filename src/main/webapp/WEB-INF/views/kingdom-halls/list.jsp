<%--
    Author     : oliver.elder.esq
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <c:set var="pageTitle" value="Kingdom Halls" />
    <%@ include file="/WEB-INF/views/common/header.jsp" %>
    <body>
        <%@ include file="/WEB-INF/views/common/titlebar.jsp" %>
        <div class="content-container">
            <h1>Kingdom Halls</h1>
            <table id="kingdom-hall-list">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Town</th>
                        <th>Post Code</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${kingdomHalls}" var="kingdomHall">
                        <tr>
                            <td>${kingdomHall.kingdomHall}</td>
                            <td>${kingdomHall.town}</td>
                            <td>${kingdomHall.postcode}</td>
                            <td><a href="<c:url value="/kingdom-halls/${kingdomHall.kingdomHall}" />">View</a>&nbsp;
                                <a href="<c:url value="/kingdom-halls/${kingdomHall.kingdomHall}/edit" />">Edit</a>&nbsp;
                                <a href="delete">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <th><input type="text" name="search_name" value="Search names" class="search_init" /></th>
                        <th><input type="text" name="search_town" value="Search towns" class="search_init" /></th>
                        <th><input type="text" name="search_postcode" value="Search post code" class="search_init" /></th>
                        <th></th>
                    </tr>
                </tfoot>
            </table>
        </div>
        <%@ include file="/WEB-INF/views/common/footer.jsp" %>
        <script type="text/javascript" charset="utf8" src="<c:url value='/javascript/kingdom-halls.js' />" ></script>
    </body>
</html>