<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
  </head>
  <body>
    <div class="btn-group-vertical">
      <g:each in="${files}" status="i" var="file">
         <button type="button" class="btn btn-default">
          <g:link action="create" id="${file}">${file}</g:link>
        </button>
      </g:each>
    </div>
  </body>
</html>
