
<%@ page import="au.org.emii.aatams.ReceiverDeployment" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'receiverDeployment.label', default: 'ReceiverDeployment')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.receiver.label" default="Receiver" /></td>
                            
                            <td valign="top" class="value"><g:link controller="receiver" action="show" id="${receiverDeploymentInstance?.receiver?.id}">${receiverDeploymentInstance?.receiver?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.station.label" default="Station" /></td>
                            
                            <td valign="top" class="value"><g:link controller="installationStation" action="show" id="${receiverDeploymentInstance?.station?.id}">${receiverDeploymentInstance?.station?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.deploymentNumber.label" default="Deployment Number" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "deploymentNumber")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.deploymentDate.label" default="Deployment Date" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${receiverDeploymentInstance?.deploymentDate}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.recoveryDate.label" default="Recovery Date" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${receiverDeploymentInstance?.recoveryDate}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.acousticReleaseID.label" default="Acoustic Release ID" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "acousticReleaseID")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.mooringType.label" default="Mooring Type" /></td>
                            
                            <td valign="top" class="value"><g:link controller="mooringType" action="show" id="${receiverDeploymentInstance?.mooringType?.id}">${receiverDeploymentInstance?.mooringType?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.bottomDepthM.label" default="Bottom Depth M" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "bottomDepthM")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.depthBelowSurfaceM.label" default="Depth Below Surface M" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "depthBelowSurfaceM")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.receiverOrientation.label" default="Receiver Orientation" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "receiverOrientation")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.location.label" default="Location" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "location")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="receiverDeployment.comments.label" default="Comments" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: receiverDeploymentInstance, field: "comments")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${receiverDeploymentInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>