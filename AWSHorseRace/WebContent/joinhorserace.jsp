<%@page import="com.sullhouse.gambol.PlayerDatabaseAccess"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Join a horse race!</title>
</head>
<body>

<%
	String horseRaceId = request.getParameter("horseRaceId");
%>

<form action="/joinhorserace" method="post">

Email: <input type="text" name="email"><br>
<input type="hidden" name="horseRaceId" value="<%=horseRaceId%>">
<input type="hidden" name="createPlayer" value="false">
<input type="submit">
</form>
</body>
</html>