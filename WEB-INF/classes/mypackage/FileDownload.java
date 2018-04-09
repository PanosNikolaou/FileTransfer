package mypackage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
//import java.util.Random;


import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

//@WebServlet("/FileDownload")
public class FileDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
    int BUFFER_SIZE= 1024* 10000; //10MB
    //private static final long userWebAppCounter = 0;
    
	// database connection settings
    private String dbURL = "jdbc:mysql://localhost:3306/transfile";
	private String dbUser = "root";
	private String dbPass = "testtest";
	//private char[] keyPass= "123".toCharArray();
	String keyPass="";
    String uploadId=null;
    
    
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		 uploadId = request.getParameter("download");
		 System.out.println(uploadId);
		   
		String packetFilePath="";
		//String packetLink="";
	    FileInputStream fileInputStream=null;
	    Connection conn = null;	// connection to the database
		PreparedStatement statement=null;	
		try {
			// connects to the database
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

			// queries the database
			String sqlSelect = "SELECT * FROM packet WHERE pLink = ?";
			statement = conn.prepareStatement(sqlSelect);
			statement.setString(1, uploadId);

			ResultSet result = statement.executeQuery();
		
			if (result.next()) {
				packetFilePath= result.getString("pLocation");
				//packetLink= result.getString("pLink");
				keyPass=result.getString("pPassKey");
	
				try {
					SecretKey secretKey = null;
				    File keyStoreFile = new File(packetFilePath+"/KeyStore");
				    fileInputStream = new FileInputStream(keyStoreFile);
				    KeyStore keyStore = KeyStore.getInstance("JCEKS"); 
				    keyStore.load(fileInputStream,keyPass.toCharArray());
				    fileInputStream.close();
				    
				    for (Enumeration<String> en = keyStore.aliases(); en.hasMoreElements();){
				        String alias = (String) en.nextElement();
				        // If the key entry password is not the same a the keystore password then change this
				        KeyStore.Entry entry = keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyPass.toCharArray())); 
				        KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) entry;
				        secretKey = skEntry.getSecretKey();
				    }//for
				    
			        Cipher daesCipher = Cipher.getInstance("AES");
					daesCipher.init(Cipher.DECRYPT_MODE,secretKey,daesCipher.getParameters());
			        FileOutputStream fileOutStream=new FileOutputStream(packetFilePath +"\\DECRYPTfile.zip"); //set decrypted output zip
			        fileInputStream=new FileInputStream(packetFilePath +"\\ENCRYPTfile"); //set encrypted input file.
			        CipherOutputStream decrypter= new CipherOutputStream(fileOutStream, daesCipher);
			        
			        byte[] readingBytes = new byte[BUFFER_SIZE]; //10MB buffer <--best tune after test
					int ReadBytesNum;
			        while ((ReadBytesNum=fileInputStream.read(readingBytes))!= -1){
			     	   decrypter.write(readingBytes, 0, ReadBytesNum);
			 		}
			        fileInputStream.close();
			        decrypter.close();
			        fileOutStream.close(); 
				} catch (java.security.cert.CertificateException e) {
				} catch (NoSuchAlgorithmException e) {
				} catch (IOException e) {
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (UnrecoverableEntryException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				}
				
		        ServletOutputStream outStream = response.getOutputStream();
		        ServletContext context  = getServletConfig().getServletContext();
		        String mimetype = context.getMimeType(packetFilePath);
		        // sets response content type
		        if (mimetype == null) {
		            mimetype = "application/octet-stream";
		        }//if can't get mimety
		        response.setContentType(mimetype);
		        File  file=new File(packetFilePath+"/DECRYPTfile.zip");
		        response.setContentLength((int)file.length());
		        String fileName = "FileTransfer.zip"; //download name
		        // set HTTP header
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		        DataInputStream in = new DataInputStream(new FileInputStream(packetFilePath +"\\DECRYPTfile.zip"));
		        IOUtils.copy(in, outStream);
		        in.close();
		        outStream.close();
		        
		        //delete non-encrypted files  
		        File deleteNonEncyptFile=new File(packetFilePath+"/DECRYPTfile.zip");
		        deleteNonEncyptFile.delete();
		        
			}//if select not null 	
		}//try Download
		catch (SQLException ex) {
			ex.printStackTrace();
		}//sql
		catch (IOException ex) {
			ex.printStackTrace();
		}//io
		finally {
		  if(conn!= null) {
			try{
			  conn.close();
			}//try close connection
			catch (SQLException ex) {
				ex.printStackTrace();
			}//catch
		  }//if there is connection			
		}//finally	
  }//GET POST
}//Servlet  Download File