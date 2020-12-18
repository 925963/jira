import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.UserWithAttributes
import com.atlassian.crowd.embedded.impl.ImmutableUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.security.groups.GroupManager
  
int numOfDays = 150 // Number of days the user was not logged in
Date dateLimit = (new Date())- numOfDays
  
UserUtil userUtil = ComponentAccessor.userUtil
CrowdService crowdService = ComponentAccessor.crowdService
UserService userService = ComponentAccessor.getComponent(UserService)
ApplicationUser updateUser
UserService.UpdateUserValidationResult updateUserValidationResult
GroupManager groupManager = ComponentAccessor.getGroupManager()

// counters to count the number of  users per outcome
long countAll = 0
long countDeactivated = 0
long countExcempted = 0
long countActivelyUsing = 0
long countFailedToDeactivate = 0 
long countNeverLoggedIn = 0
  
userUtil.getUsers().findAll{it.isActive()}.each {
    
    countAll++ // to keep track of all users we've looped through
        
    UserWithAttributes user = crowdService.getUserWithAttributes(it.getName())
         
    if (groupManager.isUserInGroup(it.getName(),"Some user group") || groupManager.isUserInGroup(it.getName(),"Some other user group")) {
        countExcempted++ 
    } else {
        String lastLoginMillis = user.getValue('login.lastLoginMillis')
        if (lastLoginMillis?.isNumber()) {
            Date d = new Date(Long.parseLong(lastLoginMillis))
            if (d.before(dateLimit)) {
                updateUser = ApplicationUsers.from(ImmutableUser.newUser(user).active(false).toUser())
                updateUserValidationResult = userService.validateUpdateUser(updateUser)
                if (updateUserValidationResult.isValid()) {      
                    userService.updateUser(updateUserValidationResult)
                    log.info("Deactivated ${updateUser.name}")
                    countDeactivated++       
                } else {
                    countFailedToDeactivate++
                    log.error "Update of ${user.name} failed: ${updateUserValidationResult.getErrorCollection().getErrors().entrySet().join(',')}"
                }
            } else {
                countActivelyUsing++
            } // EndIF login date is longer then 150 days
        } else { countNeverLoggedIn++} // EndIF  login time is a number
    } // EndIF user is in one of the exempted groups
} // Looped through last user
  
log.warn("${countAll} active users checked in total.\n")
log.warn("${countDeactivated} users deactivated.\n")
log.warn("${countExcempted} users skipped due to excempted Group membership.\n")
log.warn("${countActivelyUsing} users who have logged in during last 150 days.\n")
log.warn("${countFailedToDeactivate} users for which we failed to deactivate.\n")
log.warn("${countNeverLoggedIn} users never logged in.\n")

