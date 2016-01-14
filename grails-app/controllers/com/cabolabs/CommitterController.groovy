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
               path:'rest/login',
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
    
   def index() {
    
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
    
   def create(String id) {
    
       def filename = id
       def path = config.ehr.instance_repo
       def file = new File(path + filename + ".xml")
       
       def xml = file.text
       def tags = xml.findAll(/\[\[.*:::.*:::.*\]\]/)
       
       [file: file, tags: tags, patients: this.patients()]
   }
    
   private List patients()
   {
      def patientList = []
      
      log.info( "Consulta al EHR: "+ config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path +'rest/patientList')
      
      def http = new HTTPBuilder(config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path +'rest/patientList')
      
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

              patientList = json.patients
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
      
      return patientList
   }
   
   def save(String filename, String patient_uid, String COMMITTER_NAME) {
   
      println params
      
      def path = config.ehr.instance_repo
      def file = new File(path + filename + ".xml")
      def xml = file.text
      
      // Para las fechas vienen el date y el time separados
      def params2 = params.collectEntries
      {
        if (it.value instanceof String[]) // el valor es date, time
        {
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
        else if (it.value == null)
        {
           [(it.key): ''] // Null for empty
        }
        else [(it.key): it.value]
      }
      
      println params2
      
      params2.each { k, v ->
         //println v.getClass() // String o Nullobject
         
         xml = xml.replaceAll( /\[\[${k}:::.*:::.*\]\]/, v)
         
         //xml.replaceAll(k, v) // FIXME: lo que hay que sustituir es esto [[PROBLEMA_RESOLUCION:::DATE:::EMPTY]] NO solo el nombre...
      }
      
      println xml
      
      
      // Write edited file to disk
      def random = new java.util.Random()
      def committed = new File(config.ehr.instance_repo + File.separator +'committed' + File.separator +'version_'+ random.nextInt(10000) +'.xml')
      committed << xml
      
      
      def res = commit(xml, patient_uid, COMMITTER_NAME)
      
      println res
      
      redirect action:"index" // show commit result
   }
   
   private String commit(String xml, String patient_uid, String committer_name) {
   
      def ehr = new RESTClient(config.server.protocol + config.server.ip +':'+ config.server.port + config.server.path)
      
      def res
      def ehrUid
      try
      {
         res = ehr.get( path:'rest/ehrForSubject',
                        query:[subjectUid:patient_uid, format:'json'],
                        headers: ['Authorization': 'Bearer '+ session.token] )
         
         ehrUid = res.data.uid
      }
      catch (Exception e)
      {
         //  No such property: response for class: org.apache.http.conn.HttpHostConnectException
         // TODO: preguntar porque en los ejemplos del sitio web usan exception.response pero la except no tiene ese atributo
         /*
         if (e?.response.status == 404)
         {
            // TODO> ver que hacer con los errores
         }
         */
         
         println "except 1: "+ e.message
         return e.message
      }

      println "ehrUid: $ehrUid"
      
      try
      {
         /*
         def rest_params = [
           versions: [xml], // commit one version
           ehrUid: ehrUid,
           auditSystemId: 'EMR',
           auditCommitter: committer_name
         ]
         */
         
         // remove XML declaration
         xml = xml.replace('<?xml version="1.0" encoding="UTF-8"?>', '')
         xml = '<versions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://schemas.openehr.org/v1">'+ xml +'</versions>'
         
         // Sin URLENC da error null pointer exception sin mas datos... no se porque es. PREGUNTAR!
         res = ehr.post(
            path:'rest/commit',
            requestContentType: XML,
            query:  [
               ehrUid: ehrUid,
               auditSystemId: 'EMR',
               auditCommitter: committer_name
            ],
            body: xml,
            headers: ['Authorization': 'Bearer '+ session.token]
         )
         /*
          * result {
               type {
                  code('AR')                         // application reject
                  codeSystem('HL7::TABLES::TABLE_8') // http://amisha.pragmaticdata.com/~gunther/oldhtml/tables.html
               }
               message('El parametro versions es obligatorio')
               code('ISIS_EHR_SERVER::COMMIT::ERRORS::401') // sys::service::concept::code
            }
          */
         println "res: " + res.responseData.message
         //println "res2: " + res.responseData.getClass() // nodeChild
         //println "res3: " + res.responseData.name() // result
         
         if (res.responseData.type.code.text() != "AA")
         {
            throw new Exception("Server rejected the commit")
         }
         
         return res.responseData.message
      }
      catch (Exception e)
      {
         // FIXME: log a disco
         println "except 2:" + e.message
         
         e.printStackTrace(System.out)
         
         println "3"
         
         return e.message
      }
   }
}
