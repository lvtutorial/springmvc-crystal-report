package com.demo.springmvc.controller;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.crystaldecisions.reports.sdk.*;
import com.crystaldecisions.sdk.occa.report.lib.*;
import com.crystaldecisions.sdk.occa.report.data.*;
import com.crystaldecisions.sdk.occa.report.exportoptions.*;

import java.io.*;


/*https://wiki.scn.sap.com/wiki/display/BOBJ/Java+Reporting+Component++SDK+Samples*/

@Controller
public class HelloWorldController {

	@Autowired
	ServletContext context;
		
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String HelloWorld(Model model) {		
		//model.addAttribute("lstUser", lstUser);
		model.addAttribute("message", "Welcome to Spring MVC");
		return "hello";
	}

	@RequestMapping(value = "/report-viewer", method = RequestMethod.GET)
	public String HelloReport(Model model) {		
		//model.addAttribute("lstUser", lstUser);
		//model.addAttribute("message", "Welcome to Crystal Report");
		return "redirect:/reports/report-viewer.jsp";
		//return "reports/report-viewer";
	}
	
	private ResultSet getResultSetFromJDBC(String query, int scrollType) throws SQLException, ClassNotFoundException {

	    
	    String SERVERNAME = "jdbc:oracle:thin:@10.84.1.193";
	    String PORT = "1521";
	    String dbName = "AGTP";
		String username = "FAS";
		String password = "fas123456";
		// db2 versions 7.1 and 8.1
		String jdbcdriver = "oracle.jdbc.driver.OracleDriver";
		//String jdbcurl = "jdbc:db2:sample";
		Class.forName(jdbcdriver);
		
		//Construct connection to the DSN.
		String mysqlURL = SERVERNAME + ":" + PORT + "/" + dbName;
		java.sql.Connection connection = DriverManager.getConnection(mysqlURL, username,password); 
		Statement statement = connection.createStatement(scrollType, ResultSet.CONCUR_READ_ONLY);
		
		//Execute query and return result sets.
		ResultSet rs = statement.executeQuery(query);
		return rs;

	}
	
	@RequestMapping(value = "/report-viewer-no-param", method = RequestMethod.GET)
	public void callReportNoParameter(Model model, HttpServletResponse response, HttpServletRequest request) {		
		
		try {
			ReportClientDocument reportClientDocument = new ReportClientDocument();
			reportClientDocument.open("template_reports\\oracle_demo_1.rpt", 0);
			/*ITable table = reportClientDocument.getDatabaseController().getDatabase().getTables().getTable(0);
			ICommandTable ic= (ICommandTable) table;
			String SQLText=ic.getCommandText();*/
 
			//SQL query that can be used can be obtained by first creating a report directly off the desired datasource, and then
			//in Crystal Report in spring, open the 'Database' > 'Show SQL Query' to see the SQL generated for the report.  
 
		    String query= "select * from room";
 
			//Call simple utility function that obtains Java Result set that will be 
			//pushed into the report.  
			ResultSet resultSet = getResultSetFromJDBC(query, ResultSet.TYPE_SCROLL_INSENSITIVE); 
			
			String tableName = reportClientDocument.getDatabaseController().getDatabase().getTables().getTable(0).getName();
 
			//Push the Java Resultset into the report.  This will then become the datasource of the report when the report itself 
			//is generated.
			reportClientDocument.getDatabaseController().setDataSource(resultSet, tableName , "resultTbl");
 
			//Cache the report source of the ReportClientDocument in session.
			request.getSession().setAttribute("reportSource", reportClientDocument.getReportSource());
			reportClientDocument.close();
 
			//request.getRequestDispatcher("/reports/CrystalReportViewer_setResultSet.jsp").forward(request,response);
			response.sendRedirect("reports/CrystalReportViewer_setResultSet.jsp");
		}
		catch (Exception ex)
		{
			model.addAttribute("error", ex.toString());
		}			
	}	
	
	@RequestMapping(value = "/report-viewer-param", method = RequestMethod.GET)
	public void HelloReportTestParam(Model model, HttpServletResponse response, HttpServletRequest request) {		
		
		try {
			String DBUSERNAME = "FAS";
	        String DBPASSWORD = "fas123456";
	        String SERVERNAME = "10.84.1.193";
			String PORT = "1521";
	        String DATABASE_NAME = "AGTP"; // SID or Instance
	        String URI = "!oracle.jdbc.driver.OracleDriver!jdbc:oracle:thin:{userid}/{password}@" + SERVERNAME + ":" + PORT + "/" + DATABASE_NAME;  //1521/ or :1521
	        String DATABASE_DLL = "crdb_jdbc.dll";
	        
	        ReportClientDocument clientDoc = new ReportClientDocument();
	        clientDoc.open("template_reports\\oracle_demo_2.rpt", 0);
			
	        ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);
	
	        IConnectionInfo connectionInfo = table.getConnectionInfo();
	        PropertyBag propertyBag = connectionInfo.getAttributes();
	        propertyBag.clear();
	        //Overwrite any existing properties with updated values.
	        propertyBag.put("Trusted_Connection", "false");
	        propertyBag.put("Server Name", SERVERNAME); //Optional property.
	        propertyBag.put("Database Name", DATABASE_NAME);
	        propertyBag.put("Server Type", "JDBC (JNDI)");
	        propertyBag.put("URI", URI);
	        propertyBag.put("Use JDBC", "true");
	        propertyBag.put("Database DLL", DATABASE_DLL);
	        connectionInfo.setAttributes(propertyBag);
	
	        //Set database username and password.
	        //NOTE: Even if these the username and password properties don't change when switching databases, the 
	        //database password is *not* saved in the report and must be set at runtime if the database is secured.  
	        connectionInfo.setUserName(DBUSERNAME);
	        connectionInfo.setPassword(DBPASSWORD);
	        connectionInfo.setKind(ConnectionInfoKind.SQL);
	
	        table.setConnectionInfo(connectionInfo);
	        //Update old table in the report with the new table.
	        clientDoc.getDatabaseController().setTableLocation(table, table);
	        
	        ParameterFieldController paramController = clientDoc.getDataDefController().getParameterFieldController();
	        paramController.setCurrentValue("","p_id","14");
			paramController.setCurrentValue("","p_name","welcome");
			//ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)clientDoc.getPrintOutputController().export(ReportExportFormat.PDF);
			//Cache the report source of the ReportClientDocument in session.
			request.getSession().setAttribute("reportSource", clientDoc.getReportSource());
			clientDoc.close();

			//request.getRequestDispatcher("/reports/CrystalReportViewer_setResultSet.jsp").forward(request,response);
			response.sendRedirect("reports/CrystalReportViewer_setResultSet.jsp");
		}
		catch (Exception ex)
		{
			model.addAttribute("error", ex.toString());
		}
	}
	
	@RequestMapping(value = "/report-pdf-jsp", method = RequestMethod.GET)
	public String HelloReport1(Model model) {		
		//model.addAttribute("message", "Welcome to Crystal Report");
		try {
			String DBUSERNAME = "FAS";
	        String DBPASSWORD = "fas123456";
	        String SERVERNAME = "10.84.1.193";
			String PORT = "1521";
	        String DATABASE_NAME = "AGTP"; // SID or Instance
	        String URI = "!oracle.jdbc.driver.OracleDriver!jdbc:oracle:thin:{userid}/{password}@" + SERVERNAME + ":" + PORT + "/" + DATABASE_NAME;  //1521/ or :1521
	        String DATABASE_DLL = "crdb_jdbc.dll";
	        
	        ReportClientDocument clientDoc = new ReportClientDocument();
	        clientDoc.open("template_reports\\oracle_demo_2.rpt", 0);
			
	        ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);
	
	        IConnectionInfo connectionInfo = table.getConnectionInfo();
	        PropertyBag propertyBag = connectionInfo.getAttributes();
	        propertyBag.clear();
	        //Overwrite any existing properties with updated values.
	        propertyBag.put("Trusted_Connection", "false");
	        propertyBag.put("Server Name", SERVERNAME); //Optional property.
	        propertyBag.put("Database Name", DATABASE_NAME);
	        propertyBag.put("Server Type", "JDBC (JNDI)");
	        propertyBag.put("URI", URI);
	        propertyBag.put("Use JDBC", "true");
	        propertyBag.put("Database DLL", DATABASE_DLL);
	        connectionInfo.setAttributes(propertyBag);
	
	        //Set database username and password.
	        //NOTE: Even if these the username and password properties don't change when switching databases, the 
	        //database password is *not* saved in the report and must be set at runtime if the database is secured.  
	        connectionInfo.setUserName(DBUSERNAME);
	        connectionInfo.setPassword(DBPASSWORD);
	        connectionInfo.setKind(ConnectionInfoKind.SQL);
	
	        table.setConnectionInfo(connectionInfo);
	        //Update old table in the report with the new table.
	        clientDoc.getDatabaseController().setTableLocation(table, table);
	        
	        ParameterFieldController paramController = clientDoc.getDataDefController().getParameterFieldController();
	        paramController.setCurrentValue("","p_id","14");
			paramController.setCurrentValue("","p_name","welcome");
			ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)clientDoc.getPrintOutputController().export(ReportExportFormat.PDF);
			clientDoc.close();
			model.addAttribute("objectFile", byteArrayInputStream);
			model.addAttribute("message", "welcome");
		}
		catch (Exception ex)
		{
			model.addAttribute("error", ex.toString());
		}
		return "/reports/report-pdf";
	}
	
	//other way to download file
	public void downloadFile(InputStream inputStream, HttpServletResponse response, String fileType) throws FileNotFoundException, IOException {
        //response.setHeader("Content-Disposition", "attachment; filename=" + "report_1.pdf");
        //response.setHeader("Content-disposition", "inline;filename=" + "report.pdf");
		response.setHeader("Content-disposition", fileType + ";filename=" + "report.pdf");
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();

    }
	
	@RequestMapping(value = "/report-pdf-java", method = RequestMethod.GET)
	public void HelloReportJava(HttpServletResponse response) {		
		
		//model.addAttribute("message", "Welcome to Crystal Report");
		try {
			String DBUSERNAME = "FAS";
	        String DBPASSWORD = "fas123456";
	        String SERVERNAME = "10.84.1.193";
			String PORT = "1521";
	        String DATABASE_NAME = "AGTP"; // SID or Instance
	        String URI = "!oracle.jdbc.driver.OracleDriver!jdbc:oracle:thin:{userid}/{password}@" + SERVERNAME + ":" + PORT + "/" + DATABASE_NAME;  //1521/ or :1521
	        String DATABASE_DLL = "crdb_jdbc.dll";
	        
	        ReportClientDocument clientDoc = new ReportClientDocument();
	        clientDoc.open("template_reports\\oracle_demo_2.rpt", 0);
			
	        ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);
	
	        IConnectionInfo connectionInfo = table.getConnectionInfo();
	        PropertyBag propertyBag = connectionInfo.getAttributes();
	        propertyBag.clear();
	        //Overwrite any existing properties with updated values.
	        propertyBag.put("Trusted_Connection", "false");
	        propertyBag.put("Server Name", SERVERNAME); //Optional property.
	        propertyBag.put("Database Name", DATABASE_NAME);
	        propertyBag.put("Server Type", "JDBC (JNDI)");
	        propertyBag.put("URI", URI);
	        propertyBag.put("Use JDBC", "true");
	        propertyBag.put("Database DLL", DATABASE_DLL);
	        connectionInfo.setAttributes(propertyBag);
	
	        //Set database username and password.
	        //NOTE: Even if these the username and password properties don't change when switching databases, the 
	        //database password is *not* saved in the report and must be set at runtime if the database is secured.  
	        connectionInfo.setUserName(DBUSERNAME);
	        connectionInfo.setPassword(DBPASSWORD);
	        connectionInfo.setKind(ConnectionInfoKind.SQL);
	
	        table.setConnectionInfo(connectionInfo);
	        //Update old table in the report with the new table.
	        clientDoc.getDatabaseController().setTableLocation(table, table);
	        
	        ParameterFieldController paramController = clientDoc.getDataDefController().getParameterFieldController();
	        paramController.setCurrentValue("","p_id","14");
			paramController.setCurrentValue("","p_name","welcome");
			InputStream inputStream = clientDoc.getPrintOutputController().export(ReportExportFormat.PDF);
			downloadFile(inputStream, response, "attachment");
			clientDoc.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/report-pdf-inline", method = RequestMethod.GET)
	public void HelloReportInline(HttpServletResponse response)  {		
		//model.addAttribute("message", "Welcome to Crystal Report");
		try {
			String DBUSERNAME = "FAS";
	        String DBPASSWORD = "fas123456";
	        String SERVERNAME = "10.84.1.193";
			String PORT = "1521";
	        String DATABASE_NAME = "AGTP"; // SID or Instance
	        String URI = "!oracle.jdbc.driver.OracleDriver!jdbc:oracle:thin:{userid}/{password}@" + SERVERNAME + ":" + PORT + "/" + DATABASE_NAME;  //1521/ or :1521
	        String DATABASE_DLL = "crdb_jdbc.dll";
	        
	        ReportClientDocument clientDoc = new ReportClientDocument();
	        clientDoc.open("template_reports\\oracle_demo_2.rpt", 0);
			
	        ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);
	
	        IConnectionInfo connectionInfo = table.getConnectionInfo();
	        PropertyBag propertyBag = connectionInfo.getAttributes();
	        propertyBag.clear();
	        //Overwrite any existing properties with updated values.
	        propertyBag.put("Trusted_Connection", "false");
	        propertyBag.put("Server Name", SERVERNAME); //Optional property.
	        propertyBag.put("Database Name", DATABASE_NAME);
	        propertyBag.put("Server Type", "JDBC (JNDI)");
	        propertyBag.put("URI", URI);
	        propertyBag.put("Use JDBC", "true");
	        propertyBag.put("Database DLL", DATABASE_DLL);
	        connectionInfo.setAttributes(propertyBag);
	
	        //Set database username and password.
	        //NOTE: Even if these the username and password properties don't change when switching databases, the 
	        //database password is *not* saved in the report and must be set at runtime if the database is secured.  
	        connectionInfo.setUserName(DBUSERNAME);
	        connectionInfo.setPassword(DBPASSWORD);
	        connectionInfo.setKind(ConnectionInfoKind.SQL);
	
	        table.setConnectionInfo(connectionInfo);
	        //Update old table in the report with the new table.
	        clientDoc.getDatabaseController().setTableLocation(table, table);
	        
	        ParameterFieldController paramController = clientDoc.getDataDefController().getParameterFieldController();
	        paramController.setCurrentValue("","p_id","14");
			paramController.setCurrentValue("","p_name","welcome");
			InputStream inputStream = clientDoc.getPrintOutputController().export(ReportExportFormat.PDF);
			downloadFile(inputStream, response, "inline");
			clientDoc.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
		
	
}
