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
	String email = request.getParameter("email");
	String bet = request.getParameter("bet");
	String horse = request.getParameter("horse");
	String code = request.getParameter("code");
%>

<form action="/playerbet" method="post">

Your name: <input type="text" name="playerName"><br>
<input type="hidden" name="email" value="<%=email%>">
<input type="hidden" name="code" value="<%=code%>">
<input type="hidden" name="horseRaceId" value="<%=horseRaceId%>">
<input type="hidden" name="bet" value="<%=bet%>">
<input type="hidden" name="horse" value="<%=horse%>">
<input type="submit">
</form>
</body>
</html>