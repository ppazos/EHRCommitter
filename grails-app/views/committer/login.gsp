<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
    <style>
      label {
        display: block;
      }
      .label {
        width: 33%;
        display: inline-block;
        text-align: right;
        padding-right: 1em;
      }
      .buttons {
        text-align: right;
        padding-right: 1em;
      }
    </style>
  </head>
  <body>
    <h1>Login</h1>
    
    <g:if test="${flash.message}">
	   <div class="message" style="display: block">${flash.message}</div>
	 </g:if>

    <g:form action="login">
      <label>
        <span class="label">username</span>
        <input type="text" required="true" name="username" />
      </label><br />
      <label>
        <span class="label">password</span>
        <input type="password" required="true" name="password" />
      </label><br />
      <label>
        <span class="label">organization</span>
        <input type="text" required="true" name="orgnumber" />
      </label><br />
      
      <div class="buttons">
        <g:submitButton name="doit" value="Login" />
      </div>
    </g:form>
    
  </body>
</html>
