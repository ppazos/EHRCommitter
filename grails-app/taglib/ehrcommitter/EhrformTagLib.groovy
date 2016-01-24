package ehrcommitter

import java.util.UUID
import java.text.SimpleDateFormat

class EhrformTagLib {
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    
    // Do not generate inputs for these names
    def filter = ["PATIENT_UID"]
    
    def display = { attrs, body ->
    
       //println "attrs "+ attrs
    
       // [[name::type]]
       // name::type
       // name, type
       def (name, type, defaultValue) = attrs.tag[2..-3].split(':::')
       
       if (filter.contains(name)) return
       
       def method = "display"+type
       out << this."$method"(name, defaultValue)
    }
    
    def displayINTEGER(String name, String defaultValue) {
    
       if (defaultValue == "RANDOM")
       {
          defaultValue = intGenerator(1000, 0) // 0..1000
       }
       else if (defaultValue == "EMPTY")
       {
          defaultValue = 1
       }
       else if (defaultValue.startsWith('RANGE_')) // RANGE_100-150
       {
          def range = (defaultValue - "RANGE_").split('-').collect{ Integer.parseInt(it) }
          defaultValue = intGenerator(range[1] - range[0], range[0]) // range[0]..range[1]
       }
    
       """<label>${name}</label>
       <input type="number" name="${name}" value="${defaultValue}" class="form-control" />
       """
    }
    
    def displaySTRING(String name, String defaultValue) {
    
       if (defaultValue == "RANDOM")
       {
          defaultValue = generator( (('A'..'Z')+('a'..'z')+' ').join(), 30 )
       }
       else if (defaultValue == "EMPTY")
       {
          defaultValue = ""
       }
    
       $/<label>${name}</label>
       <textarea name="${name}" class="form-control">${defaultValue}</textarea>
       /$
    }
    
    def displayDATE(String name, String defaultValue) {
    
       if (defaultValue == "NOW")
       {
          def now = new Date()
          //def date_format = "yyyyMMdd" // openEHR format
          def date_format = "yyyy-MM-dd" // HTML5 format 
          def formatter = new SimpleDateFormat( date_format )
          defaultValue = formatter.format( now )
       }
       else
          defaultValue = ''
          
       """<label>${name}</label>
       <input type="date" name="${name}" value="${defaultValue}" class="form-control" />
       """
    }
    
    def displayDATETIME(String name, String defaultValue) {
    
       def defaultValueDate, defaultValueTime
    
       if (defaultValue == "NOW")
       {
          def now = new Date()
          //def datetime_format = "yyyyMMdd'T'HHmmss,SSSZ" // openEHR format
          def datetime_format = "yyyy-MM-dd HH:mm:ss" // HTML5 format
          def formatter = new SimpleDateFormat( datetime_format )
          def datetime = formatter.format( now )
          (defaultValueDate, defaultValueTime) = datetime.split(' ')
       }
       else
       {
          defaultValueDate = ''
          defaultValueTime = ''
       }
    
       """<label>${name}</label>
       <input type="date" name="${name}" value="${defaultValueDate}" class="form-control" />
       <input type="time" name="${name}" value="${defaultValueTime}" class="form-control" />
       """
    }
    
    def displayUUID(String name, String defaultValue) {
    
       if (defaultValue == "ANY")
          defaultValue = UUID.randomUUID().toString()
       
       """<label>${name}</label>
       <input type="text" name="${name}" value="${defaultValue}" class="form-control" />
       """
    }
    
    // Generates an OBJECT_VERSION_ID object_id ‘::’ creating_system_id ‘::’ version_tree_id
    //[[VERSION:::UUID:::ANY]]::[[APP:::STRING:::EMR]]::1
    def displayVERSION_ID(String name, String defaultValue) {
    
       if (defaultValue == "ANY")
          defaultValue = UUID.randomUUID().toString() +"::EMR::1"
       
       """<label>${name}</label>
       <input type="text" name="${name}" value="${defaultValue}" class="form-control" />
       """
    }
    
    def displayDURATION(String name, String defaultValue) {
       
       if (defaultValue == "ANY")
          defaultValue = "PT30M"
       
       $/<label>${name}</label>
       <input type="text" class="form-control" name="${name}" value="${defaultValue}" pattern="P(?=\w*\d)(?:\d+Y|Y)?(?:\d+M|M)?(?:\d+W|W)?(?:\d+D|D)?(?:T(?:\d+H|H)?(?:\d+M|M)?(?:\d+(?:\­.\d{1,2})?S|S)?)?" />
       /$
    }
    
    // generator( (('A'..'Z')+('0'..'9')).join(), 9 )
    def generator = { String alphabet, int n ->
       new Random().with {
         (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join()
       }
    }
    
    def intGenerator = { n, base ->
       new Random( System.currentTimeMillis() ).nextInt( n+1 ) + base
    }
}
