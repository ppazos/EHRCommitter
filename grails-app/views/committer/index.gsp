<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
  </head>
  <body>
    <ul>
      <g:each in="${files}" status="i" var="file">
        <li>
          <g:link action="create" id="${file}">${file}</g:link>
        </li>
      </g:each>
    </ul>
  </body>
</html>
