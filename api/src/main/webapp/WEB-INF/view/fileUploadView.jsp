<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>File Upload Result</title>
	</head>
	
	<body>
		
	<h2>Submitted File</h2>
	<table>
	    <tr>
	        <td>OriginalFileName:</td>
	        <td>${file.originalFilename}</td>
	    </tr>
	    <tr>
	        <td>Type:</td>
	        <td>${file.contentType}</td>
	    </tr>
	</table>
	
	</body>
</html>
