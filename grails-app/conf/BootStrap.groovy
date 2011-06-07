import au.org.emii.aatams.*

class BootStrap 
{

    def init = 
    { 
        servletContext ->
        
        environments
        {
            development
            {
                Organisation csiroOrg = 
                    new Organisation(name:'CSIRO', 
                                     phoneNumber:'1234',
                                     faxNumber:'1234',
                                     postalAddress:'12 asdf road',
                                     status:EntityStatus.ACTIVE).save(failOnError: true)
                                 
                Project sealCountProject =
                    new Project(name:'Seal Count',
                                description:'Counting seals').save(failOnError: true)
                            
                OrganisationProject csiroSeals =
                    new OrganisationProject(organisation:csiroOrg,
                                            project:sealCountProject).save(failOnError: true)
                            
                Person joeBloggs =
                    new Person(name:'Joe Bloggs',
                               phoneNumber:'1234',
                               emailAddress:'jbloggs@csiro.au').save(failOnError: true)
                           
                OrganisationPerson csiroJoeBloggs =
                    new OrganisationPerson(organisation:csiroOrg,
                                           person:joeBloggs).save(failOnError: true)
                       
                DeviceManufacturer seimens = 
                    new DeviceManufacturer(manufacturerName:'Seimens').save(failOnError: true)
                    
                DeviceModel seimensXyz =
                    new DeviceModel(modelName:'XYZ', manufacturer:seimens).save(failOnError: true)
                    
                DeviceStatus newStatus = new DeviceStatus(status:'NEW').save(failOnError: true)
                DeviceStatus deployedStatus = new DeviceStatus(status:'DEPLOYED').save(failOnError: true)
                
                Receiver rx1 =
                    new Receiver(codeName:'VR2W-101336',
                                 serialNumber:'12345678',
                                 embargoDate:null,
                                 status:deployedStatus,
                                 model:seimensXyz,
                                 project:sealCountProject).save(failOnError: true)
                             
                Tag tag1 =
                    new Tag(codeName:'A69-1303-62339',
                            serialNumber:'12345678',
                            embargoDate:null,
                            codeMap:'A69-1303',
                            pingCode:'62339',
                            model:seimensXyz,
                            project:sealCountProject,
                            status:deployedStatus).save(failOnError: true)
                            
                Tag tag2 =
                    new Tag(codeName:'A69-1303-46601',
                            serialNumber:'12345678',
                            embargoDate:null,
                            codeMap:'A69-1303',
                            pingCode:'46601',
                            model:seimensXyz,
                            project:sealCountProject,
                            status:deployedStatus).save(failOnError: true)
                            
            }
        }
    }
    
    def destroy = 
    {
    }
}