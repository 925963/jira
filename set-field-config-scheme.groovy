import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.greenhopper.model.validation.ErrorCollection
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager


// Script requirements (don't change these)
def projectManager = ComponentAccessor.getProjectManager()
def FieldLayoutManager fieldLayoutManager = ComponentAccessor.getComponent(FieldLayoutManager)
def i


//////////////////////////
// configuration        //
//                      //
// default = 15811      //
// playground = 17502   //
// infra = 12901        //
//////////////////////////

def fieldConfigScheme = 12901 // INFRA Config scheme

def projectKeys = ['BB8',  'JIP']

///////////////////////
// execution         //
///////////////////////

for (i = 0; i <projectKeys.size(); i++) {
    def project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKeys[i])
    fieldLayoutManager.addSchemeAssociation(project, fieldConfigScheme)
}