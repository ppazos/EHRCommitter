<!doctype html>
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
    
      <label>
        <span class="label">Patient:</span>
        <g:select from="${patients}" required="true" name="patient_uid" optionKey="uid" optionValue="name" noSelection="${['':'Select One...']}" />
      </label><br />
      <br/>
      
      <g:each in="${tags}" status="i" var="tag">
        <g:display tag="${tag}" />
      </g:each>
      
      <div class="buttons">
        <g:link action="index">Cancel</g:link>
        <g:submitButton name="save" value="Save" />
      </div>
      
    </g:form>
    
  </body>
</html>
