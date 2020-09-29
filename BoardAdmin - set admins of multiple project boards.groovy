package com.atlassian.greenhopper.util.BoardAdminUtils
import com.atlassian.greenhopper.service.rapid.view.BoardAdminService
import com.onresolve.scriptrunner.runner.customisers.PluginModuleCompilationCustomiser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.greenhopper.service.rapid.ProjectRapidViewService
import com.onresolve.scriptrunner.runner.customisers.PluginModuleCompilationCustomiser
import  com.atlassian.jira.user.ApplicationUser
import com.atlassian.greenhopper.model.rapid.BoardAdmin
import com.atlassian.greenhopper.model.rapid.RapidView
import com.atlassian.greenhopper.manager.rapidview.RapidViewManager
import com.atlassian.greenhopper.service.rapid.view.RapidViewService
import com.atlassian.greenhopper.manager.rapidview.BoardAdminManagerImpl
com.atlassian.crowd.embedded.api.Group

//import com.atlassian.greenhopper.manager.rapidview.RapidViewManagerImpl

def rapidViewService = PluginModuleCompilationCustomiser.getGreenHopperBean(RapidViewService)
def projectManager = ComponentAccessor.getProjectManager()

//def boardAdminUtils = new BoardAdminUtils()
def groupManager = ComponentAccessor.getGroupManager() 
def BoardAdminManager = PluginModuleCompilationCustomiser.getGreenHopperBean(BoardAdminManagerImpl)

def projectRapidViewService = PluginModuleCompilationCustomiser.getGreenHopperBean(ProjectRapidViewService)
def projects = ['ACP','CAG']
def admins = ['AA0340', '421206']

for (i = 0; i <projects.size(); i++) {
//for (project in projects) {
    def boards = projectRapidViewService.findRapidViewsByProject(
        ComponentAccessor.jiraAuthenticationContext.loggedInUser,
        ComponentAccessor.projectManager.getProjectByCurrentKey(projects[i])
    ).value

    def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    for (board in boards) {
    	def boardAdminService = PluginModuleCompilationCustomiser.getGreenHopperBean(BoardAdminService)
        
        def currentAdmins = boardAdminService.getBoardAdmins(board)
        
        def newAdmin = ComponentAccessor.userManager.getUserByName(admins[i])
        def boardAdmin = BoardAdmin.builder().type(BoardAdmin.Type.USER).key(newAdmin.key).build()
        
        def newAdmins = []
        newAdmins = currentAdmins + [boardAdmin]
        boardAdminService.updateBoardAdmins(board, user,[boardAdmin])
        boardAdminService.updateBoardAdmins(board, user, newAdmins)
    }
    log.warn("Project ${projects[i]}, admin ${admins[i]}")
}

