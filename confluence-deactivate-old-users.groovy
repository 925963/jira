import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.GroupManager

import java.time.LocalDateTime
import java.time.ZoneId

// This script finds users that are not logged in for 3 months and
// - Removes them from the 'confluence-users' group
// - Adds them to the (new) group 'Deactivated users'
// - Deactivates the user


def groupManager = ComponentLocator.getComponent(GroupManager)
def loginManager = ComponentLocator.getComponent(LoginManager)
def userAccessor = ComponentLocator.getComponent(UserAccessor)

def inactiveGroup = groupManager.getGroup("Deactivated users") ?: groupManager.createGroup("Deactivated users")

def confluenceUsersGroup = groupManager.getGroup('confluence-users')
def users = groupManager.getMemberNames(confluenceUsersGroup)
users.each { userName ->
    def user = userAccessor.getUserByName(userName)
    if (user) {
        log.info "Inspecting $user.name to determine whether user should be deactivated"
        def loginInfo = loginManager.getLoginInfo(user)

        def lastSuccessfulDate = convertDate(loginInfo?.lastSuccessfulLoginDate)
        def lastFailedDate = convertDate(loginInfo?.lastFailedLoginDate)
        def threeMonthsAgo = LocalDateTime.now().minusMonths(3)

        if (lastFailedDate?.isBefore(threeMonthsAgo) && lastSuccessfulDate?.isBefore(threeMonthsAgo)) {
            log.info "User ${user.name} last successuflly logged in ${lastSuccessfulDate} and last failed to login on ${lastFailedDate}"
            log.info "Removing user ${user.name} from confluence-users"

            groupManager.removeMembership(confluenceUsersGroup, user)

            log.info "Add user ${user.name} to the group: 'Deactivated Users'"
            groupManager.addMembership(inactiveGroup, user)

            log.info "Deactivating user ${user.name}"
            userAccessor.deactivateUser(user)
        }
    }
}

/**
 * Note: In the latest versions of ScriptRunner, you can simply use http://docs.groovy-lang.org/docs/latest/html/groovy-jdk/java/util/Date.html#toLocalDateTime() to convert the Date object to a LocalDateTime, as we have upgraded to Groovy 2.5. This convenience method is here for backward compatibility only.
 * @param date
 * @return
 */
private static LocalDateTime convertDate(Date date) {
    date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
}