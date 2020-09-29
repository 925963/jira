import com.onresolve.scriptrunner.canned.jira.admin.CopyProject
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.greenhopper.model.validation.ErrorCollection
import com.atlassian.greenhopper.service.rapid.view.RapidViewService
import com.atlassian.greenhopper.web.rapid.view.RapidViewHelper
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.AssigneeTypes
 
// Script requirements (don't change these)
def projectManager = ComponentAccessor.getProjectManager()
def i
def params = [:]
def copyProject = new CopyProject()
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
@WithPlugin("com.pyxis.greenhopper.jira")
@JiraAgileBean
RapidViewService rapidViewService
@JiraAgileBean
RapidViewHelper rapidViewHelper
 
//////////////////////////////
// configuration section    //
//////////////////////////////
 
// List of project names, project keys and project leads (usernames)
def projectNames = ['CB XO - 32B. SIRA',  'CB XO - 32C. Risk Appetite Statement',  'CB XO - 33A. Duty of Care',  'CB XO - 33B. Duty of Care: PARP/POGP',  'CB XO - 33C. Duty of Care: GKF',  'CB XO - 33D. Duty of Care: Complaint Management',  'CB XO - 33E. Duty of Care: TB Pricing Policy',  'CB XO - 33F. Duty of Care: Single point of truth for information and instructions to serve clients',  'CB XO - 35. Brexit',  'CB XO - 36A. Implement Subsidiary Risk Charter ABF',  'CB XO - 38. Suspense accounts',  'CB XO - 39A. MI in Control: Umbrella',  'CB XO - 39C. MI in Control: Non Financial Risk',  'CB XO - 39D. MI in Control: Project Portfolio Management',  'CB XO - 40. BAU audit & IMAT bevindingen',  'CB XO - 44. Mandate new P&C',  'CB XO - 49. DAC6 Readiness',  'CB XO - 51. Business Process Management']
def projectKeys = ['SIRA',  'RAS',  'DOC',  'PARPPOGP',  'GKF',  'COMPMAN',  'PRIPOL',  'SPOT',  'BREXIT',  'ISRCABF',  'SUSACC',  'MIICUM',  'MIICNFR',  'MIICPPM',  'AUDIMAT',  'MANPC',  'DACZES',  'BPM']
def projectLeads = ['C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330', 'C70330']
 
// The source project, which will be used as the 'template'
def parentProjectName = "CPO"
 
// The source Board, that will be copied to the new projects
def parentBoardName = "CB XO - Project Template Epic Board"
 
// Target Board name prefix and suffix. The project key will go in between (Prefix PROJECTKEY Suffix)
def boardPrefix = "CB XO "
def boardSuffix = " Epic Board"
 
//////////////////////////////
// Project creation script  //
//////////////////////////////
 
// Check if the lists contain the same number of values
if(projectNames.size() == projectKeys.size() && projectLeads.size() == projectKeys.size()) {
     
    for (i = 0; i <projectNames.size(); i++) {
        // Confirm the which iteration you're starting on
        log.warn("Starting on " + i + ". Project: " + projectNames[i])
 
        def newProject = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKeys[i]) // try to fetch a project with the new project key
         
        // the new project key should not exist yet. So we must only proceed if newProject is empty
        if (newProject == "" || newProject == null) {
            // The new project key doesn't exist, so we can proceed...
            // Set the parameters for copying the project
            params = [
                    (CopyProject.FIELD_SOURCE_PROJECT) : parentProjectName,
                    (CopyProject.FIELD_TARGET_PROJECT) : projectKeys[i],
                    (CopyProject.FIELD_TARGET_PROJECT_NAME) : projectNames[i],
                    (CopyProject.FIELD_COPY_VERSIONS) : false,
                    (CopyProject.FIELD_COPY_COMPONENTS) : false,
                    (CopyProject.FIELD_COPY_ISSUES) : false,
                    (CopyProject.FIELD_COPY_DASH_AND_FILTERS) : false,
                    (CopyProject.FIELD_CLONE_BOARD_NAME) : parentBoardName,
                    (CopyProject.FIELD_TARGET_BOARD_NAME) : boardPrefix + projectKeys[i] + boardSuffix
            ]
 
            // Copy the project (this will show in red, but it works nevertheless), including the board copy
            copyProject.doScript(params)
 
            // Now set the project lead
            def originalproject = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKeys[i])
            def name = projectNames[i]
            def description = " "
            def leadkey = projectLeads[i]
            def url = "http://jira.aws.abnamro.org"
             
            // Temporarily commented out becuase the Lead was not set correctly.
            // ComponentAccessor.getProjectManager().updateProject(originalproject,name,description,leadkey,url,AssigneeTypes.UNASSIGNED)
 
            // Confirm that you finished on the project iteration
            log.warn("Successfully created project " + i)
 
        } else {
             
            // Oh, no! The project already exists!
            log.warn("The new projectkey for number " + i + "with name: " + projectKeys[i] + " already exits. Skipping this project...")
        } // END of IF-ELSE to check if project[i] exists
         
    } // END of looping through project list   
     
} else {
     
    log.warn("The lists do not contain the same number of values. Aborting project creation... Bye!")
    return
} // END of script