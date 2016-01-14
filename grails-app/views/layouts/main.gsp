<!DOCTYPE html>
<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
		
		<!-- 
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		-->
		 
		<script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
	   <!-- Bootstrap Core CSS -->
	   <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
	   <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	   
		<g:layoutHead/>
		
		<style>
		  body {
		    padding: 15px;
		  }
		  .container {
		    text-align: center;
		  }
		  label {
		    color: #000;
		  }
		</style>
	</head>
	<body>
	   <nav class="navbar navbar-default">
		  <div class="container-fluid">
		    <div class="navbar-header">
		      <a class="navbar-brand" href="#">
		        <img alt="CaboLabs" height="24" src="http://cabolabs.com//CaboLabs New Logo Horizontal 300dpi 421.png">
		      </a>
		    </div>
		    <ul class="nav navbar-top-links navbar-right">
            <li>
              <g:link action="logout"><i class="fa fa-sign-out fa-fw"></i> Logout</g:link>
            </li>
          </ul>
		  </div>
		</nav>
		<div class="container">
		   <g:layoutBody/>
		</div>
	</body>
</html>
