
class AuthFilters {

   def filters = {
      
      loginCheck(controller: '*', action: '*') {
         before = {
            if (!session.token && !actionName.equals('login'))
            {
               redirect(controller: 'committer', action: 'login')
               return false
            }
         }
      }
   }
}
