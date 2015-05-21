package au.org.emii.aatams

import au.org.emii.aatams.detection.InvalidDetection
import au.org.emii.aatams.detection.InvalidDetectionReason
import au.org.emii.aatams.detection.RawDetection
import au.org.emii.aatams.detection.ValidDetection

import groovy.sql.Sql
import org.apache.shiro.SecurityUtils

import grails.converters.XML

/**
 * Index (and meta-data) to a file which has been downloaded from a receiver as
 * part of the recovery process.
 */
class ReceiverDownloadFile
{
    def dataSource
    def grailsApplication

    ReceiverDownloadFileType type

    String name
    Date importDate

    FileProcessingStatus status

    String errMsg

    Person requestingUser

    Set<ReceiverEvent> events = new HashSet<ReceiverEvent>()

    static hasMany = [events:ReceiverEvent]
    static hasOne = [progress: ReceiverDownloadFileProgress]
    static auditable = true

    static constraints =
    {
        type()
        requestingUser(nullable: true)    // Null for bulk import
        progress(nullable: true)
    }

    static transients = ['knownSensors', 'uniqueTransmitterIds', 'percentComplete', 'path']

    static mapping =
    {
        errMsg type: 'text'
    }

    void initialiseForProcessing(filename)
    {
        errMsg = ""
        importDate = new Date()
        status = FileProcessingStatus.PROCESSING
        name = filename
        requestingUser = Person.get(SecurityUtils.getSubject().getPrincipal())
        progress = new ReceiverDownloadFileProgress(percentComplete: 0, receiverDownloadFile: this)
    }

    String getPath()
    {
        // Save the file to disk.
        def path = grailsApplication.config.fileimport.path

        if (!path.endsWith(File.separator))
        {
            path = path + File.separator
        }

        assert(id): "Download file ID cannot be null"
        path += (id + File.separator + name)
    }

    String toString()
    {
        return String.valueOf(path)
    }

    def toXml()
    {
        this as XML
    }

    Integer getPercentComplete()
    {
        return progress?.percentComplete
    }

    void setPercentComplete(percentComplete)
    {
        if (!progress)
        {
            progress = new ReceiverDownloadFileProgress(receiverDownloadFile: this)
        }

        progress.percentComplete = percentComplete
    }

    def totalDetectionCount()
    {
        return validDetectionCount() + invalidDetectionCount()
    }

    def validDetectionCount()
    {
        return 1
        //return executeCountCriteria(ValidDetection)
    }

    def invalidDetectionCount()
    {
        return 1
        //return executeCountCriteria(InvalidDetection)
    }

    def invalidDetectionCount(reason)
    {
        return 1
        // return executeCountCriteria(InvalidDetection,
        // {
        //     eq("reason", reason)
        // })
    }

    def unknownReceivers()
    {
        return [ "TODO" ]
        // def c = InvalidDetection.createCriteria()
        // def results = c.list
        // {
        //     receiverDownload
        //     {
        //         eq("id", Long.valueOf(id))
        //     }

        //     eq("reason", InvalidDetectionReason.UNKNOWN_RECEIVER)

        //     projections
        //     {
        //         distinct("receiverName")
        //     }
        // }

        // return results
    }

    private def executeCountCriteria(clazz)
    {
        executeCountCriteria(clazz, null)
    }

    private def executeCountCriteria(clazz, customRestriction)
    {
        def c = clazz.createCriteria()
        def count = c.get()
        {
            projections
            {
                rowCount()
            }

            receiverDownload
            {
                eq("id", Long.valueOf(id))
            }

            if (customRestriction)
            {
                customRestriction.delegate = delegate
                customRestriction.call()
            }
        }

        return count
    }

    List<String> getUniqueTransmitterIds()
    {
        def sql = new Sql(dataSource)

        def uniqueTransmitterIds =
            sql.rows("select distinct det.transmitter_id from valid_detection det where receiver_download_id = ? order by det.transmitter_id", this.id).collect { it.transmitter_id }
        log.debug("Unique transmitter IDs: " + uniqueTransmitterIds)

        return uniqueTransmitterIds
    }

    List<Long> getKnownSensors()
    {
        return Sensor.findAllByTransmitterIdInList(getUniqueTransmitterIds())
    }
}
