<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<meta charset="UTF-8">
<head>
    <title>Query2</title>
</head>
<body>

<div style="width:100%;height:10px;"></div>
<table style="width:80%;">
    <tr>
        <c:forEach items = "${requestScope.result } " var="item" varStatus="status">
            <c:if test="${status.index % 3 == 0}">
                </tr>
                <tr>
            </c:if>
        <td>${item}</td>
        </c:forEach>
    </tr>
</table>
</body>
</html>