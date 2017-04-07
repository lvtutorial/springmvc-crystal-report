<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hello World!</title>
</head>
<body>
	<h1>${message}</h1>	
	<h1>${error}</h1>
	<h3><a href="/springmvc/report-viewer">report viewer</a></h3>
	<h3><a href="/springmvc/report-viewer-test">report viewer test</a></h3>
	<h3><a href="/springmvc/report-viewer-test-param">report viewer test param</a></h3>
	<h3><a href="/springmvc/report-pdf">report pdf with code in jsp but have exception</a></h3>
	<h3><a href="/springmvc/report-pdf-java">download report pdf with code java</a></h3>
	<h3><a href="/springmvc/report-pdf-inline">inline report pdf with code java</a></h3>
	
</body>
</html>