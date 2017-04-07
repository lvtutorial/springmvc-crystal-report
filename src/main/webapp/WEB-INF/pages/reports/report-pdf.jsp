<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%//Crystal Java Reporting Component (JRC) imports.%>
<%@page import="com.crystaldecisions.reports.sdk.*" %>
<%@page import="com.crystaldecisions.sdk.occa.report.lib.*" %>
<%@page import="com.crystaldecisions.sdk.occa.report.data.*" %>
<%@page import="com.crystaldecisions.sdk.occa.report.exportoptions.*" %>
<%//Java imports. %>
<%@page import="java.io.*" %>

	
<%
	
	try {		
		//do not need show in this jsp
		ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)request.getAttribute("objectFile");
		writeToBrowser(byteArrayInputStream, response, "application/pdf");
	   //out.println(getServletContext().getRealPath("/"));

	} catch(Exception ex) {
		out.println(ex);
	}

%>

<%!
   /*
	* Utility method that demonstrates how to write an input stream to the server's local file system.  
	*/
	private void writeToBrowser(ByteArrayInputStream byteArrayInputStream, HttpServletResponse response, String mimetype) throws Exception {
	
		//Create a byte[] the same size as the exported ByteArrayInputStream.
		byte[] buffer = new byte[byteArrayInputStream.available()];
		int bytesRead = 0;
		
		//Set response headers to indicate mime type and inline file.
		response.reset();
		response.setHeader("Content-disposition", "inline;filename=report.pdf");
		response.setContentType(mimetype);
		
		//Stream the byte array to the client.		
		while((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
			response.getOutputStream().write(buffer, 0, bytesRead);	
		}
		
		//Flush and close the output stream.
		response.getOutputStream().flush();
		response.getOutputStream().close();
		
	}
%>	