package au.org.emii.aatams

import org.joda.time.*
import org.joda.time.contrib.hibernate.*

/**
 * Surgery is the process of attaching/implanting a tag to/in an animal (given
 * by the owning AnimalRelease).
 */
class Surgery 
{
    def sessionFactory
    
    static belongsTo = [release: AnimalRelease, tag: Tag]
    static hasMany = [detectionSurgeries: DetectionSurgery]
    
    static mapping =
    {
        timestamp type: PersistentDateTimeTZ,
        {
            column name: "timestamp_timestamp"
            column name: "timestamp_zone"
        }
        
        comments type: 'text'
        cache true
    }
    
    static transients = ['inWindow']
    
    static searchable =
    {
        tag component:true
    }
    
    DateTime timestamp = new DateTime(Person.defaultTimeZone())
    SurgeryType type
    SurgeryTreatmentType treatmentType
    String comments
    
    static constraints =
    {
        timestamp()
        release()
        tag()
        treatmentType()
        comments(nullable:true, blank:true)
    }
    
    String toString()
    {
        return "Tag (" + String.valueOf(tag) + "): " + String.valueOf(type)
    }
    
    /**
     * Each surgery has window, defined by whether the related release is 
     * current, and the expected tag life time.
     */
    boolean isInWindow(Date timestamp)
    {
        if (!release.isCurrent())
        {
            return false
        }
        
        def startWindow = release.releaseDateTime
        if (new DateTime(timestamp).isBefore(startWindow))
        {
            return false
        }
        
        // Now check window of operation.
        if (!tag.expectedLifeTimeDays)
        {
            // Expected life time not specified (therefore there's no
            // end window limit.
            return true
        }
        
        def endWindow = startWindow.plusDays(tag?.expectedLifeTimeDays).plusDays(grailsApplication.config.tag.expectedLifeTime.gracePeriodDays)
        if (new DateTime(timestamp).isAfter(endWindow))
        {
            return false
        }
        
        return true
    }
}
