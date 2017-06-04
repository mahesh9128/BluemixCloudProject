/* Name : Mahesh Manohar
 * Course Num : 6331
 * Lab Number : Assignment 2
 * Section : 002
 * */

package wasdev.sample.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Database db = CloudantClientMgr.getDB();
		try{
			InputStream inputstr = request.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputstr, writer, "UTF-8");
			String input = writer.toString();
			JSONParser parserdet = new JSONParser();
			Object objdet = parserdet.parse(input);
			JSONObject jsonObj = (JSONObject) objdet;

			response.setContentType("text/html");
			
			//Get all the data from the database
			List<HashMap> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
					.getDocsAs(HashMap.class);
			boolean flagexists = true;
			
			//check whether the file exists
			if (allDocs.size() == 0) {
				response.getWriter().print("File doesnt exists!!");	
				return;
			}
			//Delete the file 
			else{
				for (HashMap doc : allDocs) {
					HashMap<String, Object> obj = db.find(HashMap.class, doc.get("_id") + "");
					if(((String)jsonObj.get("name")).equals(obj.get("name")) && ((String)jsonObj.get("version")).equalsIgnoreCase((String.valueOf((double)obj.get("version"))))){
						db.remove(obj);
						flagexists = false;
					}
				}
			}
			if(!flagexists){
				response.getWriter().print("File deleted successfully!!");	
				return;
			}else {
				response.getWriter().print("File doesnt exists!!");	
				return;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
