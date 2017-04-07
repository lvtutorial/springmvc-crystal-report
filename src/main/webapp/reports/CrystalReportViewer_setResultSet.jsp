<%@ page contentType="text/html; charset=utf-8"

         import="com.crystaldecisions.report.web.viewer.CrystalImageCleaner,
                 com.crystaldecisions.report.web.viewer.CrystalReportViewer,
                 com.crystaldecisions.report.web.viewer.CrPrintMode"
%><%
Object reportSource;
CrystalReportViewer crystalReportViewer;

/*
 * Retrieve ReportSource from HTTP Session and pass to CrystalReportViewer.
 */ 

reportSource = session.getAttribute("reportSource");

crystalReportViewer = new CrystalReportViewer();
crystalReportViewer.setOwnPage(true);
crystalReportViewer.setPrintMode(CrPrintMode.ACTIVEX);
crystalReportViewer.setReportSource(reportSource);
crystalReportViewer.processHttpRequest(request, response, getServletContext(), null);

%><%!

/*
 * Start image clieanup thread on JSP load. 
 * Scan temp folder every 10 minutes to delete viewer-generated images 20 minutes or older.
 */
public void jspInit() {
    CrystalImageCleaner.start(getServletContext(),  10 * (60 * 1000), 20 * (60 * 1000));
} 

/*
 * Stop the image cleanup thread on JSP unload.
 */
public void jspDestroy() {
    CrystalImageCleaner.stop(getServletContext());
} 

%>

