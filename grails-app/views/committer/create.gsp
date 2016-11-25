<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
    <style>
      form {
        text-align: left;
      }
      .buttons {
        text-align: right;
        padding-right: 1em;
      }
    </style>
  </head>
  <body>
    <h1>${file.name}</h1>
    

    <g:form action="save">
    
      <input type="hidden" name="filename" value="${file.name -".xml"}" />
    
      <div class="form-group">
        <label for="ehr_uid">EHR:</label>
        <g:select from="${ehrs}" class="form-control" required="true" name="ehr_uid" optionKey="uid" optionValue="uid" noSelection="${['':'Select One...']}" />
      </div>
      
      <g:each in="${tags}" status="i" var="tag">
        <div class="form-group">
          <g:display tag="${tag}" />
        </div>
      </g:each>
      
      <div class="buttons">
        <g:link action="index">Cancel</g:link>
        <g:submitButton name="save" value="Save" />
      </div>
      
    </g:form>
    
  </body>
</html>
