<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Query1</title>
    <link rel="stylesheet" href="css/bootstrap.css" type="text/css" />
</head>
<body>

    <table border="1" width="600" frame="hsides" rules="groups">
        <caption>Ultimate Table</caption>
        <colgroup span="1" width="200"></colgroup>
        <colgroup span="3" width="400"></colgroup>

        <thead>
        <tr>
            <td>Location-ID</td>
            <td>Start-Time</td>
            <td>End-Time</td>
        </tr>
        </thead>
        <tbody>
        <c:forEach items = "${requestScope.result}" var = "entry">
        <c:forEach items="${entry.value}" var="item" varStatus="status">
        <tr>
            <td>${entry.key}</td>
            <td>${item[0]}</td>
            <td>${item[1]}</td>
        </tr>
        </c:forEach>
        </c:forEach>
        </tbody>
    </table>

</body>
</html>