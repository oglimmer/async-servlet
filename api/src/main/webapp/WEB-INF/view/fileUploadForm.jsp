<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>File Upload Example</title>
	</head>
	
	<body>

	<form:form method="POST" action="uploadFile" enctype="multipart/form-data" modelAttribute="login">
	    <table>
	        <tr>
	            <td><form:label path="file">Select a file to upload</form:label></td>
	            <td><input type="file" name="file" /></td>
	        </tr>
	        <tr>
	            <td><input type="submit" value="Submit" /></td>
	        </tr>
	    </table>
	
	</form:form>
	
	</body>

</html>
