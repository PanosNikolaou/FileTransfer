package mypackage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

//import sun.net.www.URLConnection;

import java.security.KeyStore.SecretKeyEntry;

//import com.sun.xml.internal.ws.util.ByteArrayBuffer;

/**
 * Servlet implementation class UploadFileServlet
 */
//@WebServlet("/UploadFileServlet")
@SuppressWarnings("unused")
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CHAR_LIST ="abcdefghijklmnopqrstuvwxyz-"
										  +"ABCDEFGHIJKLMNOPQRSTUVWXYZ_"
										  +"-1234567890";    
    int BUFFER_SIZE= 1024* 10000; //10MB

    private int  maxFileSize = 5000000 * 1024; // 5GB
    private int maxBufferSize =100*1024; //100KB
    //  private File theFile;
    private int postContentlength=0;  
    private String storePath="C:/storedData/";   // file path where the packets is stored in server.
    String serverPort="";//":8080";
    String serverLink=""; 
    int VisitorCounter=0;
    int UploadCounter=0;
    
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFileServlet() {
        super();
    }//Constructor

    
    // GET a random character
    private int getRandomNumber() {
    	int randomInt=0;
    	Random randomGenerator= new Random();
    	randomInt = randomGenerator.nextInt(CHAR_LIST.length());
    	if (randomInt-1== -1) { return randomInt; }//if
    	else{ return randomInt-1; }//else
    }//getRandomNumber
    
   	
    // construct the random string
    public String randomlink(int RANDOM_STRING_LENGTH) {
    	StringBuffer randStr = new StringBuffer();        
    	for(int i=0; i<RANDOM_STRING_LENGTH; i++){
    		int number = getRandomNumber();
    		char ch = CHAR_LIST.charAt(number);
    		randStr.append(ch);
    	}//for length
      return randStr.toString();
    }//Random Link
 	
	
	// database connection settings
	private String dbURL = "jdbc:mysql://localhost:3306/transfile";
	private String dbUser = "root";
	private String dbPass = "testtest";
	private String message;
    private String folderName,folderPath;
    String keyPass="123";
    
 // S E R V L E T     HTTP
 public void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, java.io.IOException {  
	 
	 serverLink ="http://localhost:8080//FileTransfer/FileDownload?download=";
				
	 String userZipPass="";
	 int downloadTimes=1;
	 Connection conn = null;	// connection to the database
	 request.setCharacterEncoding("UTF-8"); //encoding for non-Latin characters.
	 boolean isMultipart = ServletFileUpload.isMultipartContent(request); //Check if content is   multipart/form-data
	 postContentlength=request.getContentLength(); //get post length in Byte.
	 response.setContentType("text/html"); //set response type
	 PrintWriter out =response.getWriter( ); //get writer to print response
 
	 // CREATE DISK DIR   
	 File newDirection=null;
	 do{
     folderName= randomlink(40);         //get a randowm unique folder name
     folderPath=storePath+ folderName;   //construct path
	 newDirection = new File(folderPath);
	 if (!newDirection.exists()){		// if dicrectory not exist
	    if(!newDirection.mkdirs()){ 	//create all files to create the dir
	      System.out.println("An error is ocured while create direction.");
	    }//if can't make direction
	    break;
	  }//if not exist
	 }while(newDirection.exists());
	
	 // SET FILE FACTORY	  
	 DiskFileItemFactory factory = new DiskFileItemFactory();
	 // maximum size that will be stored in memory until temp file created.
	 factory.setSizeThreshold(maxBufferSize);
	 // directory to save temp files
	 factory.setRepository(new File(folderPath+"/")); 
	 // Create a new file upload handler
	 ServletFileUpload upload = new ServletFileUpload(factory);
   
	 SecretKey secretKey = null;             
	 try{     	
		 //	INITIATE ENCRYPTION
		 KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		 keyGen.init(128);
		 secretKey = keyGen.generateKey(); //generate real time key.
		 Cipher aesCipher = Cipher.getInstance("AES");  // init encrypter for AES
		 aesCipher.init(Cipher.ENCRYPT_MODE,secretKey,aesCipher.getParameters());

		 KeyStore ks = KeyStore.getInstance("JCEKS"); // create store for keys
		 ks.load(null, null);
		 KeyStore.SecretKeyEntry skse = new SecretKeyEntry(secretKey);	// create entry (with the key) for keystore
		 keyPass=randomlink(5);//get 5 char key
		 ks.setEntry("secretKeyAlias",skse,new KeyStore.PasswordProtection(keyPass.toCharArray())); //insert entry to store
		 //store File
		 FileOutputStream fos = null;
		 try {
			 fos = new FileOutputStream(folderPath+"/KeyStore");
			 ks.store(fos,keyPass.toCharArray());
		 } finally {
			 if (fos != null) {
				 fos.close();
			 }//if not null
		 }//finally

		 FileOutputStream fileOutStream;    
		 FileInputStream fileInStream;
    
		 List<FileItem> fileItems = upload.parseRequest(request); //get files list from request
		 Iterator<FileItem> i = fileItems.iterator();

		 out.println("<html>");
		 out.println("<head>");
		 out.println("</head>");
		 out.println("<body>");
		 ZipFile zipFile = new ZipFile(folderPath +"/myfile.zip");
		 ZipParameters parameters = new ZipParameters();
		 parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
		 parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
		 parameters.setEncryptFiles(false);
		 parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

		 // GET FILES
		 while (i.hasNext() ){ 
			 FileItem fileItemList= (FileItem)i.next();  //get a file item from iterator for process
			 if (!fileItemList.isFormField () ){   //if item is file 
				 String fileName = fileItemList.getName();
				 parameters.setFileNameInZip(fileName); //set names for every file in zip
				 parameters.setSourceExternalStream(true); //get input from memory, not from disk.    	   
				 InputStream tempInputStream= fileItemList.getInputStream();
				 zipFile.addStream(tempInputStream, parameters);
				 tempInputStream.close();
			 }//if item is file
    	else{  //if item has data from the posted form, get data.
    	if( fileItemList.getFieldName().equalsIgnoreCase("packetPass") ){ 	 
    		userZipPass= fileItemList.getString();
    		if( userZipPass=="" || userZipPass==null ){ 
    		   parameters.setEncryptFiles(false);	
    		}//if no pass inserted
    		else{ 
    			parameters.setEncryptFiles(true); //enable zip encryption
    			parameters.setPassword(userZipPass);
    		}//else set zip packet
      	  }//if packetPass 
    
    	}//else isFieldForm
      }//while items
  
     // DELETE TEMP files after zip
     File f=new File(folderPath); //set work directory
     for(File deleteFile: f.listFiles()){        //check all files in that folder   
       if(deleteFile.getName().endsWith(".tmp")){ //if .tmp file 
    	  deleteFile.delete();  	  //delete 
       }//if tmp
     }//for all file in the folder

      // ENCRYPTION 
      fileInStream= new FileInputStream(folderPath +"/myfile.zip");   //read non-encrypted zip
      fileOutStream=new FileOutputStream(folderPath +"/ENCRYPTfile"); //write an encrypted file
      CipherInputStream encrypter = new CipherInputStream(fileInStream, aesCipher); 
      byte[] readingBytes = new byte[BUFFER_SIZE]; //10MB buffer <--best tune after test
      int ReadBytesNum;
      while ((ReadBytesNum= encrypter.read(readingBytes))!= -1) {
 		fileOutStream.write(readingBytes,0,ReadBytesNum); //write the encrypted byte in stream.
      }//while
      fileInStream.close();
      encrypter.close();
      fileOutStream.close();
    
      //delete non-encrypted files        (comment this lines for test)
      File deleteNonEncyptFile=new File(folderPath+"/myfile.zip");
      deleteNonEncyptFile.delete();
      
      out.println("The link to download the file is: <br>");
      out.print("\n"+serverLink+folderName);
      out.println("</body>");
      out.println("</html>");  
     
    }//try
    catch(Exception exeption) {
       System.out.println(exeption);
       PrintWriter exeptOut= response.getWriter(); 
       exeptOut.println(exeption.getMessage());
    }//catch
 
	Calendar calendar = Calendar.getInstance();
	java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());            			    	
	try {
		// connects to the database
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		conn = DriverManager.getConnection(dbURL, dbUser, dbPass); //?
		
		// constructs SQL statement
		String sqlInsertPacket = "INSERT INTO packet (pLink,pLocation,pDate,pDownTimes,pPassKey) values (?,?,?,?,?)";
		PreparedStatement statement = conn.prepareStatement(sqlInsertPacket);
		statement.setString(1,folderName);
		statement.setString(2,folderPath);
		statement.setTimestamp(3,ourJavaTimestampObject);
		statement.setInt(4,downloadTimes);
		statement.setString(5,keyPass);

		int row = statement.executeUpdate();
		if(row>0){
			message = "File uploaded and saved into database";
		}//if a row returned
		else{
			message = "File did not uploaded and saved into database!!!";
		}//else no row
		System.out.println("\n"+message+"\n"); 
	}
	catch (SQLException ex){
		message = "ERROR: " + ex.getMessage();
		ex.printStackTrace();
	}//catch
	finally{
		if (conn != null) {
		  try {
			conn.close();
		  }//try close connection 
		  catch(SQLException ex) {
			 ex.printStackTrace();
		  }//catch finally
	 	}//if connection
	}//finally 
 }//doPOST
    
    
    
    //    D O     G E T   
      public void doGet( HttpServletRequest request, HttpServletResponse response)
           throws ServletException, java.io.IOException {
          String value = request.getParameter("url");
          int length   = 0;
        if(value!=null){
          ServletOutputStream outStream = response.getOutputStream();
          ServletContext context  = getServletConfig().getServletContext();
          String mimetype = context.getMimeType(storePath);
          response.setContentType(mimetype);
   
/*          File urlFile = new File(storePath+value+"/My_Download_Link.url");
          response.setContentLength((int)urlFile.length());
          String fileName = urlFile.getName();  
          // sets HTTP header
          response.setHeader("Content-Disposition","attachment; filename=\""+fileName+"\"");   
          byte[] byteBuffer = new byte[1024];
          DataInputStream in = new DataInputStream(new FileInputStream(urlFile));
          // reads the file's bytes and writes them to the response stream
          while ((in != null) && ((length = in.read(byteBuffer)) != -1)){
              outStream.write(byteBuffer,0,length);
          }//while
          
          in.close();
          outStream.close();
*/
        }//if null
      }//doGET

  }// Upload Servlet