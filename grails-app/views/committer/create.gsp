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
    
    <g:set var="patients" value="${patients.collect{ pat -> 
      pat.name = pat.firstName +" "+ pat.lastName
      pat
    }}" />
    
    <%--
    ${patients}
    --%>
    

    <g:form action="save">
    
      <input type="hidden" name="filename" value="${file.name -".xml"}" />
    
      <div class="form-group">
        <label for="patient_uid">Patient:</label>
        <g:select from="${patients}" class="form-control" required="true" name="patient_uid" optionKey="uid" optionValue="name" noSelection="${['':'Select One...']}" />
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
