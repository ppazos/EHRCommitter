<!DOCTYPE html>
<html>
  <head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <!-- Bootstrap Core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    
    <style type='text/css' media='screen'>
     body {
       padding-top: 15px;
     }
	  .panel-body {
	    padding-bottom: 0;
	  }
	  .form-group {
	    text-align: left;
	  }
	  label {
	    color: #000;
	  }
    </style>
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="col-md-4 col-md-offset-4">
          <div class="login-panel panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Please Sign In</h3>
            </div>
            <div class="panel-body">
			     <g:if test="${flash.message}">
				    <div class="message" style="display: block">${flash.message}</div>
				  </g:if>
			     <g:form action="login">
				    <fieldset>
				      <div class="form-group">
				        <label for="username">username</label>
				        <input type="text" required="true" name="username" class='form-control' />
				      </div>
				      <div class="form-group">
				        <label for="password">password</label>
				        <input type="password" required="true" name="password" class='form-control' />
				      </div>
				      <div class="form-group">
				        <label for="orgnumber">organization</label>
				        <input type="text" required="true" name="orgnumber" class='form-control' />
				      </div>
				    </fieldset>
				    <fieldset>
				      <div class="form-group">
				          <g:submitButton name="doit" value="Login" />
				      </div>
				    </fieldset>
				  </g:form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
