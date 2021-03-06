<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add Tagging to Animal Release</title>
    <g:javascript src="tagLookup.js" />
  </head>
  <body>
        <!--
             Dialog presented when adding surgery to animal release.
        -->
        <div id="dialog-form-add-surgery" title="Add Tagging to Animal Release">
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="animalReleaseId"><g:message code="animalRelease.label" default="Animal Release" /></label>
                                    <g:hiddenField name="animalReleaseId" value="${animalReleaseInstance?.id}" />

                                </td>
                                <td valign="top" class="value ${hasErrors(bean: animalReleaseInstance, field: 'id', 'errors')}">
                                  <label id="project">${animalReleaseInstance}</label>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="surgeryTimestamp"><g:message code="surgery.timestamp.label" default="Timestamp" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: surgeryInstance, field: 'timestamp', 'errors')}">
                                    <joda:dateTimePicker name="surgeryTimestamp" class="remember"
                                                         value="${surgeryInstance?.timestamp}"
                                                         useZone="true"/>

                                </td>
                            </tr>
                            
                            <tr>
                                <td/>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label>Tag Details</label>
                                </td>
                                <td valign="top" class="value">
                                    <label>Enter an existing serial number, or enter new tag details:</label>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="serialNumber"><g:message code="tag.serialNumber.label" default="Serial Number" /></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:textField name="serialNumber" placeholder="autocomplete - start typing" size="60" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for=tagCodeMapId><g:message code="surgery.tag.codeMap.label" default="Code Map" /></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:select name="tagCodeMapId" from="${au.org.emii.aatams.CodeMap.list()}" optionKey="id" />
                                </td>
                            </tr>
                            
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="pingCode"><g:message code="tag.pingCode.label" default="Ping Code ID" /></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:textField name="pingCode" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="modelId"><g:message code="tag.model.label" default="Model" /></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:select name="modelId" from="${au.org.emii.aatams.TagDeviceModel.list()}" optionKey="id" />
                                </td>
                            </tr>
                        
                            <tr>
                                <td/>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="type"><g:message code="surgery.type.label" default="Placement" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: surgeryInstance, field: 'type', 'errors')}">
                                    <g:select name="surgeryTypeId" class="remember" from="${au.org.emii.aatams.SurgeryType.list()}" optionKey="id" value="${surgeryInstance?.type?.id}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label class="compulsory" for="treatmentType"><g:message code="surgery.treatmentType.label" default="Treatment" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: surgeryInstance, field: 'treatmentType', 'errors')}">
                                    <g:select name="treatmentTypeId" class="remember" from="${au.org.emii.aatams.SurgeryTreatmentType.list()}" optionKey="id" value="${surgeryInstance?.treatmentType?.id}"  />

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="comments"><g:message code="surgery.comments.label" default="Comments" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: surgeryInstance, field: 'comments', 'errors')}">
                                    <g:textArea name="surgeryComments" value="${surgeryInstance?.comments}" />

                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
            </g:form>
        </div>
  </body>
</html>
