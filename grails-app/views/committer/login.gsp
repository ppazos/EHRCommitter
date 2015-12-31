<!DOCTYPE html>
<html>
  <head>
    <title></title>
    
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <!-- Bootstrap Core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    
    <style type='text/css' media='screen'>
  #login {
    margin: 15px 0px;
    padding: 0px;
    text-align: center;
  }
  #login .inner {
    width: 340px;
    padding-bottom: 6px;
    margin: 60px auto;
    text-align: left;
    border: 1px solid #aab;
    background-color: #f0f0fa;
    -moz-box-shadow: 2px 2px 2px #eee;
    -webkit-box-shadow: 2px 2px 2px #eee;
    -khtml-box-shadow: 2px 2px 2px #eee;
    box-shadow: 2px 2px 2px #eee;
  }
  #login .inner .fheader {
    padding: 18px 26px 14px 26px;
    background-color: #f7f7ff;
    margin: 0px 0 14px 0;
    color: #2e3741;
    font-size: 18px;
    font-weight: bold;
  }
  #login .inner .cssform p {
    clear: left;
    margin: 0;
    padding: 4px 0 3px 0;
    padding-left: 105px;
    margin-bottom: 20px;
    height: 1%;
  }
  #login .inner .cssform input[type='text'] {
    width: 120px;
  }
  #login .inner .cssform label {
    font-weight: bold;
    float: left;
    text-align: right;
    margin-left: -105px;
    width: 110px;
    padding-top: 3px;
    padding-right: 10px;
  }
  #login #remember_me_holder {
    padding-left: 120px;
  }
  #login #submit {
    margin-left: 15px;
  }
  #login #remember_me_holder label {
    float: none;
    margin-left: 0;
    text-align: left;
    width: 200px
  }
  #login .inner .login_message {
    padding: 6px 25px 20px 25px;
    color: #c33;
  }
  #login .inner .text_ {
    width: 120px;
  }
  #login .inner .chk {
    height: 12px;
  }
  .panel-body {
    padding-bottom: 0;
  }
  .form-group {
    text-align: left;
  }
  label {
    color: #000;
    
    /*
    width: 100px;
    text-align: right;
    */
  }
    </style>
  </head>
  <body>
    <div class="container" id="login">
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
