

<%@ page import="au.org.emii.aatams.AnimalRelease" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'animalRelease.label', default: 'AnimalRelease')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${animalReleaseInstance}">
            <div class="errors">
                <g:renderErrors bean="${animalReleaseInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="project"><g:message code="animalRelease.project.label" default="Project" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'project', 'errors')}">
                                    <g:select name="project.id" from="${au.org.emii.aatams.Project.list()}" optionKey="id" value="${animalReleaseInstance?.project?.id}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="animal"><g:message code="animalRelease.animal.label" default="Animal" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'animal', 'errors')}">
                                    <g:select name="animal.id" from="${au.org.emii.aatams.Animal.list()}" optionKey="id" value="${animalReleaseInstance?.animal?.id}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="captureLocality"><g:message code="animalRelease.captureLocality.label" default="Capture Locality" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'captureLocality', 'errors')}">
                                    <g:textField name="captureLocality" value="${animalReleaseInstance?.captureLocality}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="captureLocation"><g:message code="animalRelease.captureLocation.label" default="Capture Location" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'captureLocation', 'errors')}">
                                    <g:textField name="captureLocation" value="${animalReleaseInstance?.captureLocation}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="captureDateTime"><g:message code="animalRelease.captureDateTime.label" default="Capture Date Time" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'captureDateTime', 'errors')}">
                                    <g:datePicker name="captureDateTime" precision="day" value="${animalReleaseInstance?.captureDateTime}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="releaseLocality"><g:message code="animalRelease.releaseLocality.label" default="Release Locality" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'releaseLocality', 'errors')}">
                                    <g:textField name="releaseLocality" value="${animalReleaseInstance?.releaseLocality}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="releaseLocation"><g:message code="animalRelease.releaseLocation.label" default="Release Location" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'releaseLocation', 'errors')}">
                                    <g:textField name="releaseLocation" value="${animalReleaseInstance?.releaseLocation}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="releaseDateTime"><g:message code="animalRelease.releaseDateTime.label" default="Release Date Time" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'releaseDateTime', 'errors')}">
                                    <g:datePicker name="releaseDateTime" precision="day" value="${animalReleaseInstance?.releaseDateTime}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="comments"><g:message code="animalRelease.comments.label" default="Comments" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'comments', 'errors')}">
                                    <g:textField name="comments" value="${animalReleaseInstance?.comments}" />

                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>