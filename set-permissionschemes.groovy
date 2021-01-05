import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.greenhopper.model.validation.ErrorCollection
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.permission.PermissionSchemeManager
 
 
// Script requirements (don't change these)
def projectManager = ComponentAccessor.getProjectManager()
def permissionManager = ComponentAccessor.permissionManager
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def i

  
//////////////////////////////
// configuration section    //
//////////////////////////////
  
// List of project keys 
def projectKeys = ['BB8',  'JIP']

// Permission scheme to set
String permissionScheme = "AAB - DQIM PS"


//////////////////////////////
// execution                //
//////////////////////////////

for (i = 0; i <projectKeys.size(); i++) {
    def project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKeys[i])
    ComponentAccessor.getPermissionSchemeManager().removeSchemesFromProject(project)
    ComponentAccessor.getPermissionSchemeManager().addSchemeToProject(project, ComponentAccessor.getPermissionSchemeManager().getSchemeObject(permissionScheme))
}