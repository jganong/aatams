
<%@ page import="au.org.emii.aatams.SensorDetection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'sensorDetection.label', default: 'SensorDetection')}" />
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
                            <td valign="top" class="name"><g:message code="sensorDetection.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: sensorDetectionInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.timestamp.label" default="Timestamp" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${sensorDetectionInstance?.timestamp}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.receiver.label" default="Receiver" /></td>
                            
                            <td valign="top" class="value"><g:link controller="receiver" action="show" id="${sensorDetectionInstance?.receiver?.id}">${sensorDetectionInstance?.receiver?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.transmitterName.label" default="Transmitter Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: sensorDetectionInstance, field: "transmitterName")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.stationName.label" default="Station Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: sensorDetectionInstance, field: "stationName")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.location.label" default="Location" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: sensorDetectionInstance, field: "location")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.sensor.label" default="Sensor" /></td>
                            
                            <td valign="top" class="value"><g:link controller="sensor" action="show" id="${sensorDetectionInstance?.sensor?.id}">${sensorDetectionInstance?.sensor?.encodeAsHTML()}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.uncalibratedValue.label" default="Uncalibrated Value" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: sensorDetectionInstance, field: "uncalibratedValue")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="sensorDetection.tags.label" default="Tags" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${sensorDetectionInstance.tags}" var="t">
                                    <li><g:link controller="tag" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${sensorDetectionInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
