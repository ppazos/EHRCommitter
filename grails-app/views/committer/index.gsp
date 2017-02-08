<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
  </head>
  <body>
    <div class="btn-group-vertical">
      <g:each in="${files}" status="i" var="file">
        <g:link action="create" id="${file}" class="btn btn-default">
        ${file}
        </g:link>
      </g:each>
    </div>
  </body>
</html>
