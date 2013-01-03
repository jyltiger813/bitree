<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="com.linzhi.tree.bo.DistributorTreeDispatcher" %>
     <%@ page import="com.linzhi.tree.constant.TreeConstant" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<% 
System.out.println("test");
%>
test
<%
DistributorTreeDispatcher dispatcher = (DistributorTreeDispatcher)this.getServletContext().getAttribute(TreeConstant.DISTRIBUTORTREE_NAME);
System.out.println("dispatcher:"+dispatcher);//dispatcher
int size = dispatcher.getIdTree().getInnerTree().size();
%>
<%=size %>
</body>
</html>