package au.org.emii.aatams

import grails.test.*

import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.SecurityUtils

class CandidateEntitiesServiceTests extends GrailsUnitTestCase 
{
    def candidateEntitiesService
    def permService
    def person
    
    Receiver newReceiver
    Receiver deployedReceiver
    Receiver recoveredReceiver
    Receiver csiroReceiver
    
    Project notPermittedProject
    Project permittedProject

    Installation notPermittedInstallation1
    Installation notPermittedInstallation2
    Installation permittedInstallation1
    Installation permittedInstallation2
    
    protected void setUp()
    {
        super.setUp()
        
        mockLogging(PermissionUtilsService)
        permService = new PermissionUtilsService()
        
        mockLogging(CandidateEntitiesService)
        candidateEntitiesService = new CandidateEntitiesService()
        candidateEntitiesService.permissionUtilsService = permService

        // User belongs to IMOS, but not to CSIRO.
        def imos = new Organisation(name:"IMSO")
        def csiro = new Organisation(name:"CSIRO")
        def orgList = [imos, csiro]
        mockDomain(Organisation, orgList)
        orgList.each { it.save() }
        
        person = new Person(username:"person",
                            organisation:imos)
                               
        mockDomain(Person, [person])
        person.save()
        
        notPermittedProject = new Project(name:"not permitted", status:EntityStatus.ACTIVE)
        permittedProject = new Project(name:"permitted", status:EntityStatus.ACTIVE)
        def projectList = [notPermittedProject, permittedProject]
        mockDomain(Project, projectList)
        
        notPermittedInstallation1 = new Installation(name: "not permitted 1", project:notPermittedProject)
        notPermittedInstallation2 = new Installation(name: "not permitted 2", project:notPermittedProject)
        
        permittedInstallation1 = new Installation(name: "permitted 1", project:permittedProject)
        permittedInstallation2 = new Installation(name: "permitted 2", project:permittedProject)
        
        def installationList = [notPermittedInstallation1, notPermittedInstallation2, permittedInstallation1, permittedInstallation2]
        mockDomain(Installation, installationList)
        installationList.each { it.save() }
        
        notPermittedProject.addToInstallations(notPermittedInstallation1)
        notPermittedProject.addToInstallations(notPermittedInstallation2)
        permittedProject.addToInstallations(permittedInstallation1)
        permittedProject.addToInstallations(permittedInstallation2)
        
        projectList.each { it.save() }
        
        def subject = [ getPrincipal: { person.username },
                        isAuthenticated: { true },
                        hasRole: { false },
                        isPermitted:
                        {
                            permission ->
                            
                            if (permission == "project:" + permittedProject.id + ":write")
                            {
                                return true
                            }
                            return false
                        }
                      ] as Subject

        ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, 
                            [ getSubject: { subject } ] as SecurityManager )

        SecurityUtils.metaClass.static.getSubject = { subject }
        
        DeviceStatus newStatus = new DeviceStatus(status:"NEW")
        DeviceStatus deployedStatus = new DeviceStatus(status:"DEPLOYED")
        DeviceStatus recoveredStatus = new DeviceStatus(status:"RECOVERED")
        
        def statusList = [newStatus, deployedStatus, recoveredStatus]
        mockDomain(DeviceStatus, statusList)
        statusList.each { it.save() }
        
        newReceiver = new Receiver(codeName:"VRW2-111", status:newStatus, organisation:imos)
        deployedReceiver = new Receiver(codeName:"VRW2-222", status:deployedStatus, organisation:imos)
        recoveredReceiver = new Receiver(codeName:"VRW2-333", status:recoveredStatus, organisation:imos)
        csiroReceiver = new Receiver(codeName:"VRW2-444", status:recoveredStatus, organisation:csiro)

        def receiverList = [newReceiver, deployedReceiver, recoveredReceiver]
        mockDomain(Receiver, receiverList)
        receiverList.each 
        {
            imos.addToReceivers(it)
            it.save() 
        }
        
        imos.save()
    }

    protected void tearDown() 
    {
        super.tearDown()
    }

    void testReceivers() 
    {
        def receivers = candidateEntitiesService.receivers()
        
        println "receivers: " + receivers
        
        assertEquals(2, receivers.size())
        
        assertTrue(receivers.contains(newReceiver))
        assertTrue(receivers.contains(recoveredReceiver))
        assertFalse(receivers.contains(deployedReceiver))
        assertFalse(receivers.contains(csiroReceiver))
    }
    
    void testInstallations()
    {
        def installations = candidateEntitiesService.installations()
        assertEquals(2, installations.size())
        assertTrue(installations.contains(permittedInstallation1))
        assertTrue(installations.contains(permittedInstallation2))
    }

    void testProjects()
    {
        assertEquals(2, Project.list().size())
        assertEquals(2, Project.findAllByStatus(EntityStatus.ACTIVE).size())
        
        def projects = candidateEntitiesService.projects()
        assertEquals(1, projects.size())
        assertTrue(projects.contains(permittedProject ))
    }
}
