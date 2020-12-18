import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.confluence.security.SpacePermissionManager
import com.atlassian.confluence.security.SpacePermission
import com.atlassian.user.GroupManager
import com.atlassian.confluence.core.ContentPermissionManager
import com.atlassian.confluence.internal.security.SpacePermissionContext

def spaceManager = ComponentLocator.getComponent(SpaceManager)
def spacePermissionManager = ComponentLocator.getComponent(SpacePermissionManager)
def groupManager = ComponentLocator.getComponent(GroupManager)

// get all space keys
def spaceList = spaceManager.allSpaces.findAll { it.isGlobal() }.collect { it.key }
def i // loop counter
def spacesUpdated = 0// count spaces where we added the group
def spacesSkipped = 0 // count spaces where the group was already added

log.warn("The total number of spaces is: ${spaceList.size()}")
// log.warn(spaceList)

for (i = 0; i < spaceList.size(); i++) {
    def targetSpace = spaceManager.getSpace(spaceList[i])
    def targetGroup = groupManager.getGroup("ciso-access-control")

    //Ensure the space doesn't have the group already
    if (!spacePermissionManager.getGroupsWithPermissions(targetSpace).contains(targetGroup)) { 
        //Add the group to the space with, with view permissions
        def spacePermission = SpacePermission.createGroupSpacePermission(SpacePermission.CREATEEDIT_PAGE_PERMISSION, targetSpace, targetGroup.getName())
        spacePermissionManager.savePermission(spacePermission)
        spacePermission = SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION , targetSpace, targetGroup.getName())
        spacePermissionManager.savePermission(spacePermission)
        spacesUpdated++
        log.warn("Added group ${targetGroup} to space: ${targetSpace.name}")
    } else { 
        spacesSkipped++
        log.warn("The space ${targetSpace.name} already contains the group ${targetGroup}")
    }
}
log.warn("Succesfully added the permission to ${spacesUpdated} spaces")
log.warn("A number of ${spacesSkipped} spaces had the group already")

