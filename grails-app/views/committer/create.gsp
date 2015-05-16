<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <title></title>
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
    
      Patient: <g:select from="${patients}" name="patient_uid" optionKey="uid" optionValue="name" noSelection="${['null':'Select One...']}" />
    
      <br/><br/>
      
      <g:each in="${tags}" status="i" var="tag">
        <g:display tag="${tag}" />
      </g:each>
      
      <g:submitButton name="save" value="Save" />
      
    </g:form>
    
  </body>
</html>
