package au.org.emii.aatams

import au.org.emii.aatams.detection.*
import au.org.emii.aatams.filter.QueryService
import au.org.emii.aatams.report.*
import au.org.emii.aatams.test.AbstractFiltersUnitTestCase
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory

import grails.test.*

import org.codehaus.groovy.grails.plugins.web.filters.FilterConfig

import org.apache.shiro.SecurityUtils

class EmbargoFiltersTests extends AbstractFiltersUnitTestCase 
{
    def embargoService
    def permissionUtilsService
    
    Project project1
    Project project2
    
    AnimalReleaseController releaseController
    DetectionController detectionController
    SensorController sensorController
    TagController tagController

    def releaseList
    
    AnimalRelease releaseNonEmbargoed
    AnimalRelease releaseEmbargoedReadableProject
    AnimalRelease releaseEmbargoedNonReadableProject
    AnimalRelease releasePastEmbargoed
    
    Tag tagNonEmbargoed
    Tag tagEmbargoedReadableProject
    Tag tagEmbargoedNonReadableProject
    Tag tagPastEmbargoed

    Sensor sensorNonEmbargoed
    Sensor sensorEmbargoedReadableProject
    Sensor sensorEmbargoedNonReadableProject
    Sensor sensorPastEmbargoed
	Sensor sensorPingerNonEmbargoed
	Sensor sensorPingerEmbargoedReadableProject
	Sensor sensorPingerEmbargoedNonReadableProject
	Sensor sensorPingerPastEmbargoed

    ValidDetection detectionNonEmbargoed
    ValidDetection detectionEmbargoedReadableProject
    ValidDetection detectionEmbargoedNonReadableProject
    ValidDetection detectionPastEmbargoed
    
	def queryService = queryService
	def reportInfoService
	
    protected void setUp() 
    {
        super.setUp()

        mockLogging(EmbargoService, true)
        embargoService = new EmbargoService()
        
        mockLogging(PermissionUtilsService, true)
        permissionUtilsService = new PermissionUtilsService()

        filters.embargoService = embargoService
        embargoService.permissionUtilsService = permissionUtilsService
        
        project1 = new Project(name: "project 1")
        project2 = new Project(name: "project 2")
        def projectList = [project1, project2]
        mockDomain(Project, projectList)
        projectList.each{ it.save()}
        
        Person user = new Person(username: 'jbloggs')
        
        // Need this for "findByUsername()" etc.
        mockDomain(Person, [user])
        user.save()
        
        // Check permissions are behaving correctly.
        assertTrue(SecurityUtils.subject.isPermitted(permissionUtilsService.buildProjectReadPermission(project1.id)))
        assertFalse(SecurityUtils.subject.isPermitted(permissionUtilsService.buildProjectReadPermission(project2.id)))

        mockConfig("grails.gorm.default.list.max = 10")

		mockLogging(QueryService)
		queryService = new QueryService()
		mockLogging(ReportInfoService)
		reportInfoService = new ReportInfoService()
		
        mockController(AnimalReleaseController)
        mockLogging(AnimalReleaseController)
        releaseController = new AnimalReleaseController()
        releaseController.metaClass.getGrailsApplication = { -> [config: org.codehaus.groovy.grails.commons.ConfigurationHolder.config]}
		releaseController.reportInfoService = reportInfoService
		releaseController.queryService = queryService
		releaseController.queryService.embargoService = new EmbargoService()
		releaseController.queryService.embargoService.permissionUtilsService = permissionUtilsService
		
        mockController(DetectionController)
        mockLogging(DetectionController)
        detectionController = new DetectionController()
        detectionController.metaClass.getGrailsApplication = { -> [config: org.codehaus.groovy.grails.commons.ConfigurationHolder.config]}
		detectionController.reportInfoService = reportInfoService
		detectionController.queryService = queryService
		detectionController.queryService.embargoService = new EmbargoService()
		detectionController.queryService.embargoService.permissionUtilsService = permissionUtilsService

        mockController(SensorController)
        mockLogging(SensorController)
        sensorController = new SensorController()
        sensorController.metaClass.getGrailsApplication = { -> [config: org.codehaus.groovy.grails.commons.ConfigurationHolder.config]}
		sensorController.reportInfoService = reportInfoService
		sensorController.queryService = queryService
		sensorController.queryService.embargoService = new EmbargoService()
		sensorController.queryService.embargoService.permissionUtilsService = permissionUtilsService
		
        mockController(TagController)
        mockLogging(TagController)
        tagController = new TagController()
        tagController.metaClass.getGrailsApplication = { -> [config: org.codehaus.groovy.grails.commons.ConfigurationHolder.config]}
		tagController.reportInfoService = reportInfoService
		tagController.queryService = queryService
		tagController.queryService.embargoService = new EmbargoService()
		tagController.queryService.embargoService.permissionUtilsService = permissionUtilsService
		
        mockLogging(Tag)
        mockLogging(Sensor)
        mockLogging(AnimalRelease)
        mockLogging(Surgery)
        mockLogging(ValidDetection)
        mockLogging(DetectionSurgery)
        
        // Set up some data.
		CodeMap codeMap = new CodeMap(codeMap:"A69-1303")
        tagNonEmbargoed = new Tag(project:project1, codeMap:codeMap)
        tagEmbargoedReadableProject = new Tag(project:project1, codeMap:codeMap)
        tagEmbargoedNonReadableProject = new Tag(project:project2, codeMap:codeMap)
        tagPastEmbargoed = new Tag(project:project2, codeMap:codeMap)

        sensorNonEmbargoed = new Sensor(tag:tagNonEmbargoed, pingCode:1111)
        sensorEmbargoedReadableProject = new Sensor(tag:tagEmbargoedReadableProject, pingCode:2222)
        sensorEmbargoedNonReadableProject = new Sensor(tag:tagEmbargoedNonReadableProject, pingCode:3333)
        sensorPastEmbargoed = new Sensor(tag:tagPastEmbargoed, pingCode:4444)

        sensorPingerNonEmbargoed = new Sensor(tag:tagNonEmbargoed, pingCode:5555)
        sensorPingerEmbargoedReadableProject = new Sensor(tag:tagEmbargoedReadableProject, pingCode:6666)
        sensorPingerEmbargoedNonReadableProject = new Sensor(tag:tagEmbargoedNonReadableProject, pingCode:7777)
        sensorPingerPastEmbargoed = new Sensor(tag:tagPastEmbargoed, pingCode:8888)

        releaseNonEmbargoed = new AnimalRelease(project:project1)
        releaseEmbargoedReadableProject = new AnimalRelease(project:project1, embargoDate:nextYear())
        releaseEmbargoedNonReadableProject = new AnimalRelease(project:project2, embargoDate:nextYear())
        releasePastEmbargoed = new AnimalRelease(project:project2, embargoDate:lastYear())

        Surgery surgeryNonEmbargoed = new Surgery(tag:tagNonEmbargoed, release:releaseNonEmbargoed)
        Surgery surgeryEmbargoedReadableProject = new Surgery(tag:tagEmbargoedReadableProject, release:releaseEmbargoedReadableProject)
        Surgery surgeryEmbargoedNonReadableProject = new Surgery(tag:tagEmbargoedNonReadableProject, release:releaseEmbargoedNonReadableProject)
        Surgery surgeryPastEmbargoed = new Surgery(tag:tagPastEmbargoed, release:releasePastEmbargoed)

		ReceiverDownloadFile receiverDownload = new ReceiverDownloadFile(requestingUser:user)
		mockDomain(ReceiverDownloadFile, [receiverDownload])
		receiverDownload.save()
		
        detectionNonEmbargoed = new ValidDetection(receiverDownload:receiverDownload)
        detectionEmbargoedReadableProject = new ValidDetection(receiverDownload:receiverDownload)
		
        detectionEmbargoedNonReadableProject = new ValidDetection(receiverDownload:receiverDownload)
        detectionPastEmbargoed = new ValidDetection(receiverDownload:receiverDownload)

        DetectionSurgery detectionSurgeryNonEmbargoed = new DetectionSurgery(surgery:surgeryNonEmbargoed, detection:detectionNonEmbargoed, sensor:sensorPingerNonEmbargoed)
        DetectionSurgery detectionSurgeryEmbargoedReadableProject = new DetectionSurgery(surgery:surgeryEmbargoedReadableProject, detection:detectionEmbargoedReadableProject, sensor:sensorPingerEmbargoedReadableProject)
        DetectionSurgery detectionSurgeryEmbargoedNonReadableProject = new DetectionSurgery(surgery:surgeryEmbargoedNonReadableProject, detection:detectionEmbargoedNonReadableProject, sensor:sensorPingerEmbargoedNonReadableProject)
        DetectionSurgery detectionSurgeryPastEmbargoed = new DetectionSurgery(surgery:surgeryPastEmbargoed, detection:detectionPastEmbargoed, sensor:sensorPingerPastEmbargoed)

        
        def tagList =     [tagNonEmbargoed,     tagEmbargoedReadableProject,     tagEmbargoedNonReadableProject,     tagPastEmbargoed]
        def sensorList =  [sensorNonEmbargoed,  sensorEmbargoedReadableProject,  sensorEmbargoedNonReadableProject,  sensorPastEmbargoed]
        sensorList +=  [sensorPingerNonEmbargoed,  sensorPingerEmbargoedReadableProject,  sensorPingerEmbargoedNonReadableProject,  sensorPingerPastEmbargoed]
        releaseList =     [releaseNonEmbargoed, releaseEmbargoedReadableProject, releaseEmbargoedNonReadableProject, releasePastEmbargoed]
        def surgeryList = [surgeryNonEmbargoed, surgeryEmbargoedReadableProject, surgeryEmbargoedNonReadableProject, surgeryPastEmbargoed]
        def detectionList =
                          [detectionNonEmbargoed, detectionEmbargoedReadableProject, detectionEmbargoedNonReadableProject, detectionPastEmbargoed]
		detectionList.each {
			it.receiverDeployment = new ReceiverDeployment(location: new GeometryFactory().createPoint(new Coordinate(145f, -42f)))
		}
        def detectionSurgeryList =
                          [detectionSurgeryNonEmbargoed, detectionSurgeryEmbargoedReadableProject, detectionSurgeryEmbargoedNonReadableProject, detectionSurgeryPastEmbargoed]
        
        mockDomain(Tag, tagList)
        mockDomain(Sensor, sensorList)
        mockDomain(AnimalRelease, releaseList)
        mockDomain(Surgery, surgeryList)
        mockDomain(ValidDetection, detectionList)
        mockDomain(DetectionSurgery, detectionSurgeryList)
        
        releaseNonEmbargoed.addToSurgeries(surgeryNonEmbargoed)
        tagNonEmbargoed.addToSurgeries(surgeryNonEmbargoed)
        tagNonEmbargoed.addToSensors(sensorNonEmbargoed)
        tagNonEmbargoed.addToSensors(sensorPingerNonEmbargoed)
        sensorNonEmbargoed.addToDetectionSurgeries(detectionSurgeryNonEmbargoed)
        detectionNonEmbargoed.addToDetectionSurgeries(detectionSurgeryNonEmbargoed)
        
        releaseEmbargoedReadableProject.addToSurgeries(surgeryEmbargoedReadableProject)
        tagEmbargoedReadableProject.addToSurgeries(surgeryEmbargoedReadableProject)
        tagEmbargoedReadableProject.addToSensors(sensorEmbargoedReadableProject)
        tagEmbargoedReadableProject.addToSensors(sensorPingerEmbargoedReadableProject)
        sensorEmbargoedReadableProject.addToDetectionSurgeries(detectionSurgeryEmbargoedReadableProject)
        detectionEmbargoedReadableProject.addToDetectionSurgeries(detectionSurgeryEmbargoedReadableProject)
        
        releaseEmbargoedNonReadableProject.addToSurgeries(surgeryEmbargoedNonReadableProject)
        tagEmbargoedNonReadableProject.addToSurgeries(surgeryEmbargoedNonReadableProject)
        tagEmbargoedNonReadableProject.addToSensors(sensorEmbargoedNonReadableProject)
        tagEmbargoedNonReadableProject.addToSensors(sensorPingerEmbargoedNonReadableProject)
        sensorEmbargoedNonReadableProject.addToDetectionSurgeries(detectionSurgeryEmbargoedNonReadableProject)
        detectionEmbargoedNonReadableProject.addToDetectionSurgeries(detectionSurgeryEmbargoedNonReadableProject)
        
        releasePastEmbargoed.addToSurgeries(surgeryPastEmbargoed)
        tagPastEmbargoed.addToSurgeries(surgeryPastEmbargoed)
        tagPastEmbargoed.addToSensors(sensorPastEmbargoed)
        tagPastEmbargoed.addToSensors(sensorPingerPastEmbargoed)
        sensorPastEmbargoed.addToDetectionSurgeries(detectionSurgeryPastEmbargoed)
        detectionPastEmbargoed.addToDetectionSurgeries(detectionSurgeryPastEmbargoed)
        
        detectionNonEmbargoed.metaClass.getProject = { project1 }
        detectionEmbargoedReadableProject.metaClass.getProject = { project1 }
        detectionEmbargoedNonReadableProject.metaClass.getProject = { project2 }
        detectionPastEmbargoed.metaClass.getProject = { project2 }
        
        tagList.each { it.save() }
        sensorList.each { it.save() }
        releaseList.each { it.save() }
        surgeryList.each { it.save() }
        detectionList.each { it.save() }
        detectionSurgeryList.each { it.save() }
		
		ReceiverDownloadFile.metaClass.getPath = { "/some/path" }
    }        

    protected void tearDown() 
    {
        super.tearDown()
    }
   
	protected def getPrincipal()
	{
		return 'jbloggs'
	}
	
	protected boolean isPermitted(String permString)
	{
		if (permString == "project:" + project1.id + ":read")
		{
			return true
		}
		
		return false
	}
	
    private Date now()
    {
        return new Date()
    }
    
    private Date nextYear()
    {
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 1)
        return cal.getTime()
    }

    private Date lastYear()
    {
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        return cal.getTime()
    }

    void testAnimalReleaseList() 
    {
        checkList(releaseController, "animalRelease")
    }

    void testAnimalReleaseNotList() 
    {
		controllerName = "animalRelease"
		actionName = "show"

        checkEmbargoed(releaseController, releaseNonEmbargoed, false, 'animalRelease')
        checkEmbargoed(releaseController, releaseEmbargoedReadableProject, false, 'animalRelease')
        checkEmbargoed(releaseController, releaseEmbargoedNonReadableProject, true, 'animalRelease')
        checkEmbargoed(releaseController, releasePastEmbargoed, false, 'animalRelease')
    }

    void testTagNotList()
    {
		controllerName = "tag"
		actionName = "show"

        checkEmbargoed(tagController, tagNonEmbargoed, false, 'tag')
        checkEmbargoed(tagController, tagEmbargoedReadableProject, false, 'tag')
        checkEmbargoed(tagController, tagEmbargoedNonReadableProject, true, 'tag')
        checkEmbargoed(tagController, tagPastEmbargoed, false, 'tag')
    }
    
    void testSensorList()
    {
        checkList(sensorController, "sensor")
    }
    
    void testSensorNotList()
    {
		controllerName = "sensor"
		actionName = "show"

        checkEmbargoed(sensorController, sensorNonEmbargoed, false, 'sensor')
        checkEmbargoed(sensorController, sensorEmbargoedReadableProject, false, 'sensor')
        checkEmbargoed(sensorController, sensorEmbargoedNonReadableProject, true, 'sensor')
        checkEmbargoed(sensorController, sensorPastEmbargoed, false, 'sensor')

		checkEmbargoed(sensorController, sensorPingerNonEmbargoed, false, 'sensor')
		checkEmbargoed(sensorController, sensorPingerEmbargoedReadableProject, false, 'sensor')
		checkEmbargoed(sensorController, sensorPingerEmbargoedNonReadableProject, true, 'sensor')
		checkEmbargoed(sensorController, sensorPingerPastEmbargoed, false, 'sensor')
    }
    
	void testDetectionList()
	{
		def model = detectionController.list()
		assertNotNull(model)
		
		FilterConfig filter = getFilter("detectionList")
		assertNotNull(filter)

		filter.after(model)
		assertNotNull(model)
		
		assertTrue(model.entityList.containsAll(detectionNonEmbargoed, detectionPastEmbargoed, detectionEmbargoedReadableProject))
		assertFalse(model.entityList.contains(detectionEmbargoedNonReadableProject))
	}
	
    void testDetectionNotList()
    {
        checkDetection(detectionNonEmbargoed, false)
        checkDetection(detectionEmbargoedReadableProject, false)
        checkDetection(detectionEmbargoedNonReadableProject, true)
        checkDetection(detectionPastEmbargoed, false)
    }
	
	void testRedirectToLoginWhenNotAuthenticated()
	{
		controllerName = "sensor"
		actionName = "show"	
		authenticated = false
		checkEmbargoed(sensorController, sensorEmbargoedNonReadableProject, true, 'sensor', "login", "/sensor/show/" + sensorEmbargoedNonReadableProject.id)
	}
    
	void testRedirectToUnauthorizedWhenAuthenticated()
	{
		controllerName = "sensor"
		actionName = "show"	
		authenticated = true
		checkEmbargoed(sensorController, sensorEmbargoedNonReadableProject, true, 'sensor', "unauthorized", null)
	}
    
    private void checkDetection(def detection, boolean isEmbargoed)
    {
        assert(detection)
        
        detectionController.params.id = detection.id
        def model = detectionController.show()
        assertNotNull(model)
        assertEquals(1, model.size())
        
        FilterConfig filter = getFilter("detectionNotList")
        assertNotNull(filter)

        // All entities should be there...
        filter.after(model)
        assertNotNull(model)
        assertEquals(1, model.size())
		
        if (isEmbargoed)
        {
			assertNull(model.detectionInstance)
        }
        else
        {
			assertNotNull(model.detectionInstance)
        }
    }
    
    private void checkList(def controller, def entityName)
    {
		controller.params._name = "entityName"
		
		int expectedNum = (entityName == 'sensor') ? 6 : 3
		int expectedTotal = (entityName == 'sensor') ? 8 : 4
        def model = controller.list()
		
        assertEquals(expectedNum, model.entityList.size())
        assertEquals(expectedTotal, model.total)

        // Embargoed releases should not appear at all after filter.
        FilterConfig filter = getFilter(entityName + 'List')
        assertNotNull(filter)
        
        filter.after(model)
        assertNotNull(model)
        
		int expectedNumAfterEmbargo = (entityName == 'sensor') ? 6 : 3
		int expectedTotalAfterEmbargo = (entityName == 'sensor') ? 8 : 4
        assertEquals(expectedNumAfterEmbargo, model.entityList.size())
        assertEquals(expectedTotalAfterEmbargo, model.total)
    }
    
    private void checkEmbargoed(def controller, def entity, boolean isEmbargoed, String entityName)
    {
		checkEmbargoed(controller, entity, isEmbargoed, entityName, "unauthorized", null)
    }

    private void checkEmbargoed(def controller, def entity, boolean isEmbargoed, String entityName, expectedRedirectAction, expectedTargetUri)
    {
        controller.params.id = entity.id
        assert(controller.params)
        
        def model = controller.show()
        assertNotNull(model)
        assertEquals(1, model.size())
        
        FilterConfig filter = getFilter(entityName + "NotList")
        assertNotNull(filter)
        
		EmbargoFilters.metaClass.getTargetUri = 
		{
			params ->
			
			return expectedTargetUri
		}
		
        boolean result = filter.after(model)
        
        if (isEmbargoed)
        {
            // redirect auth/unauthorized
            assertEquals("auth", redirectArgs.controller)
            assertEquals(expectedRedirectAction, redirectArgs.action)
			if (expectedTargetUri)
			{
				assertEquals(expectedTargetUri, redirectArgs.params.targetUri)
			}
        }
        else
        {
            assertNull(result)
        }
    }
}
