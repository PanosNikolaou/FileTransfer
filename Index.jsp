<%@ page language="java" 
		 contentType="text/html; charset=UTF-8"
    	 pageEncoding="UTF-8"
    	 import="com.mysql.jdbc.*"
		 import="java.sql.ResultSet"
 %>
<jsp:useBean id="dbConn" scope="request" class="mypackage.DBFunctions"/>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>FileTransfer</title>
<link rel="stylesheet" type="text/css" media="all" href="myStyle.css" />
</head>
<script src="js/mscript.js">
</script>
        
<body onload="sban()">

 	            <div class="upload_form_cont">
                <form id="upload_form" method="post">
                    <div>
                        <div><input type="file" name="transfer_file" id="transfer_file" onchange="fileSelected();" />
                        </div>
                    </div>
                    <div>
                        <input type="button" value="Μεταφόρτωση Αρχείου" onclick="startUploading()" />
                    </div>
                    <div id="progress_info">
                        <div id="progress"></div>
                        <div id="progress_percent">&nbsp;</div>
                        <div class="clear_both"></div>
                        <div id="upload_response"></div>
                    </div>
                </form>
            </div>

  <p id="message">
  </p>
 <script  src="./js/script.js"  charset="UTF-8"></script> 
</body>
  
</html>