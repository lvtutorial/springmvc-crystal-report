<%@ page contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
<%@ taglib
	uri="/crystal-tags-reportviewer.tld" prefix="crviewer"%>
<html>
<body>

<!-- Create an opening viewer tag to specify the viewer name and the type of report source. -->
<!-- When using the Java Reporting Component, the reportSourceType will be "reportingComponent" -->
<crviewer:viewer viewerName="report-viewer" reportSourceType="reportingComponent" reportSourceVar="demo_1" isOwnPage="true">

<!-- Create the required report tag to specify the report to display
and the session variable to cache the report source. -->
<crviewer:report reportName="/template_reports/CrystalReport1.rpt"/>
</crviewer:viewer>
</body>
</html>

