

<%@ page import="au.org.emii.aatams.Detection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'detection.label', default: 'Detection')}" />
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
            <g:hasErrors bean="${detectionInstance}">
            <div class="errors">
                <g:renderErrors bean="${detectionInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="timestamp"><g:message code="detection.timestamp.label" default="Timestamp" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: detectionInstance, field: 'timestamp', 'errors')}">
                                    <g:datePicker name="timestamp" precision="day" value="${detectionInstance?.timestamp}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="receiver"><g:message code="detection.receiver.label" default="Receiver" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: detectionInstance, field: 'receiver', 'errors')}">
                                    <g:select name="receiver.id" from="${au.org.emii.aatams.Receiver.list()}" optionKey="id" value="${detectionInstance?.receiver?.id}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="transmitterName"><g:message code="detection.transmitterName.label" default="Transmitter Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: detectionInstance, field: 'transmitterName', 'errors')}">
                                    <g:textField name="transmitterName" value="${detectionInstance?.transmitterName}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="stationName"><g:message code="detection.stationName.label" default="Station Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: detectionInstance, field: 'stationName', 'errors')}">
                                    <g:textField name="stationName" value="${detectionInstance?.stationName}" />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="location"><g:message code="detection.location.label" default="Location" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: detectionInstance, field: 'location', 'errors')}">
                                    <g:textField name="location" value="${detectionInstance?.location}" />

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