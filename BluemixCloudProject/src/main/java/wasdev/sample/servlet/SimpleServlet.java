package wasdev.sample.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;

/* Name : Mahesh Manohar
 * Course Num : 6331
 * Lab Number : Assignment 2
 * Section : 002
 * */


// This is the first servlet call for loading the home page data
@WebServlet("/GetAllDocsServlet")
public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Database db = CloudantClientMgr.getDB();

		//Get all the data from the database
		List<HashMap> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
				.getDocsAs(HashMap.class);
		StringBuilder strb = new StringBuilder();
		
		if (allDocs.size() == 0) {
			response.setContentType("text/html");
			response.getWriter().print("No data!!");	
			return;
		}
		//all the data is retrieved and loaded into html elements
		else{
			strb.append("<table class=\"table\">");
			strb.append("<thead><tr><th>Name</th><th>Version</th><th>Last Modified</th></tr></thead>");
			strb.append("<tbody>");	
			for (HashMap doc : allDocs) {
				HashMap<String, Object> obj = db.find(HashMap.class, doc.get("_id") + "");
				strb.append("<tr><td>"+obj.get("name")+"</td><td>"+obj.get("version")+"</td><td>"+obj.get("timestamp")+"</td></tr>");
			}
			strb.append("</tbody></table>");
		}
		
		System.out.println("To send : "+ strb.toString());
		response.setContentType("text/html");
		response.getWriter().print(strb.toString());	
	}

}
