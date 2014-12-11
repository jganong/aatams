<%@ page import="au.org.emii.aatams.Sensor" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'sensor.label', default: 'Sensor')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <shiro:hasPermission permission="projectWriteAny">
              <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /> (or Sensor)</g:link></span>
            </shiro:hasPermission>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${sensorInstance}">
            <div class="errors">
                <g:renderErrors bean="${sensorInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${sensorInstance?.id}" />
                <g:hiddenField name="version" value="${sensorInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label class="compulsory" for="transmitterType"><g:message code="sensor.transmitterType.label" default="Transmitter Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'transmitterType', 'errors')}">
                                    <g:select name="transmitterType.id" from="${au.org.emii.aatams.TransmitterType.list()}" optionKey="id" value="${sensorInstance?.transmitterType?.id}"  />

                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label class="compulsory" for="pingCode"><g:message code="sensor.pingCode.label" default="Ping Code" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'pingCode', 'errors')}">
                                    <g:textField name="pingCode" value="${sensorInstance?.pingCode}" />

                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="slope"><g:message code="sensor.slope.label" default="Slope" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'slope', 'errors')}">
                                    <g:textField name="slope" value="${fieldValue(bean: sensorInstance, field: 'slope')}" />

                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="intercept"><g:message code="sensor.intercept.label" default="Intercept" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'intercept', 'errors')}">
                                    <g:textField name="intercept" value="${fieldValue(bean: sensorInstance, field: 'intercept')}" />

                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="unit"><g:message code="sensor.unit.label" default="Unit" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'unit', 'errors')}">
                                    <g:textField name="unit" value="${sensorInstance?.unit}" />

                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label class="compulsory" for="status"><g:message code="sensor.status.label" default="Status" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: sensorInstance, field: 'status', 'errors')}">
                                    <g:select name="status.id" from="${au.org.emii.aatams.DeviceStatus.list()}" optionKey="id" value="${sensorInstance?.status?.id}"  />

                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <g:hiddenField name="projectId" value="${sensorInstance?.project?.id}" />
                    <shiro:hasPermission permission="project:${sensorInstance?.project?.id}:write">
                      <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                      <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                    </shiro:hasPermission>
                </div>
            </g:form>
        </div>
    </body>
</html>
