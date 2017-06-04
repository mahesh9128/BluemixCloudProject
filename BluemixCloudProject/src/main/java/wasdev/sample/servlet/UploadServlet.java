/* Name : Mahesh Manohar
 * Course Num : 6331
 * Lab Number : Assignment 2
 * Section : 002
 * */

package wasdev.sample.servlet;

//Import required java libraries
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cloudant.client.api.Database;

//This class is used for uploading a file and storing in the DB
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {

	private boolean isMultipart;
	String fileName=null;
	String filecontent=null;
	String hashcode=null;

	public void doPost(HttpServletRequest request, 
			HttpServletResponse response)
					throws ServletException, java.io.IOException {

		response.setContentType("text/html");

		System.out.println("Inside upload Servlet");

		long lastTime = 0;

		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		java.io.PrintWriter out = response.getWriter( );
		if( !isMultipart ){
			response.getWriter().print("Not uploaded");
			return;
		}

		// Create a new file upload handler
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		try{ 
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);
			// Process the uploaded file items
			Iterator i = fileItems.iterator();
			
			//last modified timestamp of the file 
			SimpleDateFormat dt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"); 
			dt.setTimeZone(TimeZone.getTimeZone("CST"));

			while ( i.hasNext () ) 
			{
				FileItem fi = (FileItem)i.next();
				//for getting the last modified date of the file
				if (fi.isFormField() && fi.getFieldName().equalsIgnoreCase("lastTime")){
					lastTime = Long.parseLong(fi.getString());
					//convert the long date to readable form in cst 
					System.out.println("time test :"+ lastTime);
					System.out.println("Timestamp test :"+ dt.format(new Date(lastTime)));
				}
				else if (!fi.isFormField())	
				{
					// Get the uploaded file parameters
					fileName = fi.getName();
					System.out.println("fileName : "+fileName );

					File file = new File(fileName);
					fi.write( file ) ;

					//Get file contents
					filecontent = readFile(file);
					hashcode = HashCode.encodeToMd5(file.getAbsolutePath());
				}
			}

			//store the file
			Database db = CloudantClientMgr.getDB();

			//get the version and save the file
			double version =0;
			List<HashMap> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
					.getDocsAs(HashMap.class);
			Map<String, Object> data = new HashMap<String, Object>();
			if (allDocs.size() == 0) {
				data.put("name",fileName);
				data.put("content", filecontent);
				data.put("timestamp", dt.format(new Date(lastTime)));
				data.put("version", version+1);
				data.put("hash",hashcode);
				db.save(data);
				response.getWriter().print("File saved successfully");	
				return;
			}
			else{
				for (HashMap doc : allDocs) {
					HashMap<String, Object> obj = db.find(HashMap.class, doc.get("_id") + "");
					if(fileName.equalsIgnoreCase((String)obj.get("name"))){
						if(hashcode.equals((String)obj.get("hash"))){
							response.getWriter().print("File with same content exists!!");
							return;
						}
						if(version < (double) obj.get("version")){
							version =(double) obj.get("version");
						}
					}
				}
			}
			System.out.println("Version : "+ version);
			data.put("name",fileName);
			data.put("content", filecontent);
			data.put("timestamp", dt.format(new Date(lastTime)));
			data.put("version", version+1);
			data.put("hash",hashcode);
			db.save(data);

			response.getWriter().print("File saved successfully!!");
			return;

		}catch(Exception ex) {
			ex.printStackTrace();
			response.getWriter().print("Please try again!!");
			return;
		}
	}

	public static String readFile(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader (file));
		try{
			String         line = null;
			StringBuilder  stringBuilder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	/*public static void main(String[] args) throws IOException, ParseException{
		String time = "1486863498377";
		long timeLong = Long.parseLong(time);
		SimpleDateFormat dt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"); 
		dt.setTimeZone(TimeZone.getTimeZone("EST"));
		Date date = new Date(timeLong);
		System.out.println("String  :" +dt.format(date));
	}*/
}