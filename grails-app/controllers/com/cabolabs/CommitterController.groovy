package com.cabolabs

import grails.util.Holders
import groovyx.net.http.*
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import java.text.SimpleDateFormat

class CommitterController {

   def config = Holders.config
   
   def datetime_format_openEHR = "yyyyMMdd'T'HHmmss,SSSZ" // openEHR format
   def datetime_format_html5   = "yyyy-MM-dd HH:mm:ss" // HTML5 format

   def login(String username, String password, String orgnumber)
   {
      if (params.doit)
      {
         if (!username && !password && !orgnumber)
         {
            flash.message = 'Please specify all the authentication values'
            return
         }
         
         // service login
         // set token on session
         def ehr = new RESTClient(config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path)
         try
         {
            // Sin URLENC da error null pointer exception sin mas datos... no se porque es. PREGUNTAR!
            def res = ehr.post(
               path:'api/v1/login',
               requestContentType: URLENC,
               body: [username: username, password: password, organization: orgnumber]
            )
            
            //println "res: " + res.responseData
            //println "res: " + res.responseData.token
            
            session.token = res.responseData.token
            
            redirect action:'index'
            return
         }
         catch (Exception e)
         {
            println e.message
            //e.printStackTrace(System.out)
            flash.message = "Invalid credentials"
         }
      }
   }
   
   def logout()
   {
      session.token = null
      
      redirect action:'login'
      return
   }
    
   def index()
   {
       def path = Holders.config.ehr.instance_repo
       
       println path
       
       def repo = new File( path )
       
       println repo
       
       def files = []
       repo.eachFileMatch groovy.io.FileType.FILES, ~/.*\.xml/, { file ->
         
         //println "indexAll file: "+ file.name
         files << file.name - ".xml"
       }
       
       println files
       
       [files: files]
   }
    
   def create(String id)
   {
       def filename = id
       def path = config.ehr.instance_repo
       def file = new File(path + filename + ".xml")
       
       def xml = file.text
       def tags = xml.findAll(/\[\[.*:::.*:::.*\]\]/)
       
       [file: file, tags: tags, ehrs: this.ehrs()]
   }
    
   private List ehrs()
   {
      def ehrs = []
      
      log.info( "Consulta al EHR: "+ config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path +'api/v1/ehrs')
      
      def http = new HTTPBuilder(config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path +'api/v1/ehrs')
      
      // Si no hay conexion con el servidor tira excepcion
      try
      {
         // perform a GET request, expecting TEXT response data
         http.request( Method.GET, ContentType.JSON ) { req ->
           
           uri.query = [ format: 'json' ]
         
           headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
           headers.'Authorization' = 'Bearer '+ session.token
           
           req.getParams().setParameter("http.connection.timeout", new Integer(10000));
           req.getParams().setParameter("http.socket.timeout", new Integer(10000));
         
           response.success = { resp, json ->

              //ehrs = json.ehrs
              json.ehrs.each { ehr ->
                ehrs << ehr
              }
           }
         
           // handler for any failure status code:
           response.failure = { resp ->
              
              println "response: " + resp.getAllHeaders() + " curr thrd id: " + Thread.currentThread().getId()
              
              //println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
              throw new Exception("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
           }
         }
      }
      catch (org.apache.http.conn.HttpHostConnectException e) // no hay conectividad
      {
         log.error ( e.message )
      }
      catch (groovyx.net.http.HttpResponseException e) // hay conectividad pero da un error del lado del servidor
      {
         log.error ( e.message )
      }
      
      return ehrs
   }
   
   def save(String filename, String ehr_uid, String COMMITTER_NAME)
   {
      println params
      
      def path = config.ehr.instance_repo
      def file = new File(path + filename + ".xml")
      def xml = file.text
      
      // Para las fechas vienen el date y el time separados
      def params2 = params.collectEntries
      {
        println it.key +' '+ it.value
        
        if (it.value instanceof String[]) // el valor es date, time
        {
           println "DATE"
           //[(it.key): it.value[0]+" "+it.value[1]] // formato HTML5 "yyyy-MM-dd HH:mm:ss"
           
           // Transform yyyy-MM-dd HH:mm:ss to yyyyMMddTHHmmss.sss
           
           def date_text = it.value[0]+" "+it.value[1]
           
           // Avoid empty values
           if (date_text == " ") [(it.key): ""]
           else
           {
              def format = new SimpleDateFormat(this.datetime_format_html5)
              def date = format.parse( date_text )
              
              def format_oehr = new SimpleDateFormat(this.datetime_format_openEHR)
              def str_date_openEHR = format_oehr.format(date)
              
              println "date: " + str_date_openEHR
              
              [(it.key): str_date_openEHR]
           }
        }
        else if (it.key.startsWith("DVCT_")) // data comes from a coded text
        {
           println "CODED"
          
           if (!it.value)
           {
              /*
              <group name="null flavours">
               <concept id="271" rubric="no information"/>
               <concept id="253" rubric="unknown"/>
               <concept id="272" rubric="masked"/>
               <concept id="273" rubric="not applicable"/>
              </group>
              */
              [(it.key - "DVCT_"): $/<null_flavour xsi:type="DV_CODED_TEXT">
                <value>no information</value>
                <defining_code>
                  <terminology_id>
                    <value>openehr</value>
                  </terminology_id>
                  <code_string>271</code_string>
                </defining_code>
              </null_flavour>/$]
           }
           else
           {
              def (text, code, terminology_id) = it.value.split("::")
              /*
              TBD (it.value is text::code::terminology_id)
             
              <value xsi:type="DV_CODED_TEXT">
                <value>Leve</value>
                <defining_code>
                  <terminology_id>
                    <value>local</value>
                  </terminology_id>
                  <code_string>at0047</code_string>
                </defining_code>
              </value>
              */
             
              [(it.key - "DVCT_"): $/<value xsi:type="DV_CODED_TEXT">
                <value>${text}</value>
                <defining_code>
                  <terminology_id>
                    <value>${terminology_id}</value>
                  </terminology_id>
                  <code_string>${code}</code_string>
                </defining_code>
              </value>/$]
           }
        }
        else if (it.key.startsWith("DVORD_")) // data comes from an ordina
        {
          println "ORDINAL"
          def (text, code, terminology_id, ordinal) = it.value.split("::")
          /*
           <value xsi:type="DV_ORDINAL">
              <value>ordinal</value>
              <symbol>
                <value>text</value>
                <defining_code>
                  <terminology_id>
                     <value>terminology_id</value>
                  </terminology_id>
                  <code_string>code</code_string>
                </defining_code>
              </symbol>
            </value>
          */
          
          [(it.key - "DVORD_"): $/<value xsi:type="DV_ORDINAL">
            <value>${ordinal}</value>
            <symbol>
              <value>${text}</value>
              <defining_code>
                <terminology_id>
                  <value>${terminology_id}</value>
                </terminology_id>
                <code_string>${code}</code_string>
              </defining_code>
            </symbol>
          </value>/$]
        }
        else if (it.value == null)
        {
           println "Value null"
           [(it.key): ''] // Null for empty
        }
        else
        {
           println "last case"
           [(it.key): it.value]
        }
      }
      
      println params2
      
      def escaped_k, escaped_v
      params2.each { k, v ->
         //println v.getClass() // String o Nullobject
         
         // If k has parenthesis, the replacement is not done.

         escaped_k = k.replace('?', '\\?') // if k has a ?, it is not replaced
         escaped_k = escaped_k.replace('*', '\\*') // if k has a *, it is not replaced
         escaped_k = escaped_k.replace('&', '&amp;')
         
         if (k != escaped_k)
         {
            println "replacing "+ k +" "+ escaped_k
         }
         
         escaped_v = v.replace('&', '&amp;')
         
         if (v != escaped_v)
         {
            println "replacing "+ v +" "+ escaped_v
         }
         
         xml = xml.replaceAll( /\[\[${escaped_k}:::.*:::.*\]\]/, escaped_v)

         //xml.replaceAll(k, v) // FIXME: lo que hay que sustituir es esto [[PROBLEMA_RESOLUCION:::DATE:::EMPTY]] NO solo el nombre...
      }
      
      //println xml
      
      
      // Write edited file to disk
      def random = new java.util.Random()
      def committed = new File(config.ehr.instance_repo + File.separator +'committed' + File.separator +'version_'+ random.nextInt(10000) +'.xml')
      committed << xml
      
      
      def res = commit(xml, ehr_uid, COMMITTER_NAME)
      
      println res
      
      redirect action:"index" // show commit result
   }
   
   private String commit(String xml, String ehrUid, String committer_name) {
   
      def ehr = new RESTClient(config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path)
      def res
      
      println "ehrUid: $ehrUid"
      
      try
      {
         // remove XML declaration and internal namespaces
         xml = xml.replace('<?xml version="1.0" encoding="UTF-8"?>', '')
         xml = xml.replace('xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"', '')
         xml = xml.replace('xmlns="http://schemas.openehr.org/v1"', '')
         
         // add namespace only on the root
         xml = '<versions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://schemas.openehr.org/v1">'+ xml +'</versions>'
         
         // Sin URLENC da error null pointer exception sin mas datos... no se porque es. PREGUNTAR!
         res = ehr.post(
            path:"api/v1/ehrs/${ehrUid}/compositions",
            requestContentType: XML,
            query:  [
               auditSystemId: 'EMR',
               auditCommitter: committer_name,
               format: 'xml'
            ],
            body: xml,
            headers: ['Authorization': 'Bearer '+ session.token]
         )
         /*
            <result>
              <type>AA</type>
              <message>Las versiones fueron recibidas con éxito, pero algunas advertencias fueron devueltas.</message>
              <code>EHR_SERVER::API::RESPONSE_CODES::1324</code>
              <details>
                <item>El OPT Test Multimedia referenciado desde la composición 424ec1c0-f1a0-4560-af7d-d7a02cac41c9, no está cargada. Por favor cargue el OPT para permitir la indexación de los datos.</item>
              </details>
            </result>
          */
         
         
         println "------"
         if (res.status in 200..299)
         {
            println "Status OK: "+ res.statusLine.statusCode +' '+ res.statusLine.reasonPhrase
         }
         else
         {
            println "Status ERROR: "+ res.statusLine.statusCode +' '+ res.statusLine.reasonPhrase
         }
         
         println "Result Type: "+ res.data.type.text()
         println "Result Message: "+ res.data.message.text()
         println "Result Code: "+ res.data.code.text()
         
         if (!res.data.details.isEmpty())
         {
            println "Result Details:"
            res.data.details.item.each {
               println " - "+ it
            }
         }
         
         // text/xml
         println "Result Content: "+ res.contentType
         println "------"
         println ""
         
         
         // Versions were committed successfully ...
         //println "res: " + res.responseData.message.text()
         
         // EHR_SERVER::API::RESPONSE_CODES::1324
         //println res.data.code.text()
         //println res.data.getClass() // NodeChild
         //println "res2: " + res.responseData.getClass() // NodeChild
         //println "res3: " + res.responseData.name() // result
         
         // 202 Accepted
         //println res.statusLine.statusCode +' '+ res.statusLine.reasonPhrase
         //println res.statusLine.statusCode.getClass() // Integer
         
         // 202
         ///println res.status
         //println res.status.getClass() // Integer
         
         return res.data.message.text() // res.responseData.message
      }
      catch (Exception e)
      {
         println "except commit:" + e.message
         /*
         println e.response.data
         
         e.printStackTrace(System.out)
         
         //if (e?.response.status == 404)
         //{
            // TODO> ver que hacer con los errores
         //}
         
         return e.message
         */
      }
   }
}
