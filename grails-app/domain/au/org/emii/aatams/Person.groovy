package au.org.emii.aatams

import au.org.emii.aatams.util.ListUtils
import shiro.*

class Person extends SecUser
{
    static hasMany = [projectRoles:ProjectRole]
    static belongsTo = [organisation:Organisation]
    
    static transients = ['projects']
    
    String name;
    String emailAddress;
    String phoneNumber;
    
    // Allows for PENDING users when self-registering.
    EntityStatus status
    String registrationComment
    
    static constraints = 
    {
        name(blank:false)
        organisation()
        phoneNumber(blank:true)
        emailAddress(email:true)
        status()
        registrationComment(nullable:true, blank:true)
    }

    static mapping = 
    {
        // Speed up candidate entities service/permission utils service.
        cache:true
    }
    
    String toString()
    {
        return name
    }
    
    String getProjects()
    {
        return ListUtils.fold(projectRoles, "project")
    }
}
