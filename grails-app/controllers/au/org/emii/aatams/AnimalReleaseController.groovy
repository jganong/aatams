package au.org.emii.aatams

import au.org.emii.aatams.detection.DetectionFactoryService

class AnimalReleaseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    
    def animalFactoryService
    def candidateEntitiesService
    def detectionFactoryService
    
    def tagFactoryService
    
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : grailsApplication.config.grails.gorm.default.list.max, 100)
        [animalReleaseInstanceList: AnimalRelease.list(params), animalReleaseInstanceTotal: AnimalRelease.count()]
    }

    def create = {
        def animalReleaseInstance = new AnimalRelease()
        animalReleaseInstance.properties = params
        
        def model = 
            [animalReleaseInstance: animalReleaseInstance] \
          + [candidateProjects:candidateEntitiesService.projects()] \
          + embargoPeriods()

        return model
    }
    
    def addDependantEntity(params)
    {
        //
        // If the animalReleaseId has been specified (i.e. the request has 
        // originated as part of editing an existing animal release), then
        // look up the animal release and put it in the model.
        //
        def animalReleaseInstance
        
        if (params.id)
        {
            animalReleaseInstance = AnimalRelease.get(params.id)
            if (!animalReleaseInstance) 
            {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), params.id])}"
                redirect(action: "list")
                return
            }
        }
        else
        {
            animalReleaseInstance = new AnimalRelease()
            animalReleaseInstance.project = Project.get(params.projectId)
        }

        [animalReleaseInstance: animalReleaseInstance]
    }
    
    /**
     * This method just populates the model with appropriate objects when
     * adding surgery to an animal release.
     */
    def addSurgery =
    {
        log.debug("In addSurgery, params: " + params + ", flash: " + flash)
        addDependantEntity(params)
    }

    def addMeasurement =
    {
        log.debug("In addMeasurement, params: " + params + ", flash: " + flash)
        addDependantEntity(params)
    }

    def save = 
    {
        def animalReleaseInstance = new AnimalRelease(params)
        
        // Either animal.id or speciesId must be specified.
        if ((params.animal?.id == null) && (params.speciesId == null))
        {
            def model = 
                [animalReleaseInstance: animalReleaseInstance] \
              + [candidateProjects:candidateEntitiesService.projects()] \
              + embargoPeriods()
            
            render(view: "create", model: model)
        }
        else
        {
            // Set the embargo date if embargo period (in months) has been specified.
            setEmbargoDate(params, animalReleaseInstance)
            
            // The request can contain either a known animal, or just a species and
            // sex, in which case we need to create an animal.
            Animal animalInstance = animalFactoryService.lookupOrCreate(params)

            // Update status of any previous releases for this animal to "FINISHED".
            animalInstance.releases?.each
            {
                log.debug("Setting previous release's status to FINSIHED: " + String.valueOf(it))
                it.status = AnimalReleaseStatus.FINISHED
            }

            animalReleaseInstance.animal = animalInstance
            animalInstance.addToReleases(animalReleaseInstance)

            try
            {
    
                // Create any associated surgeries (and set associated tags' status to
                // DEPLOYED).
                DeviceStatus deployedStatus = DeviceStatus.findByStatus('DEPLOYED')
                params.surgery.findAll
                {
                    k, v ->

                    // See http://stackoverflow.com/questions/1811395/grails-indexed-parameters
                    if (!k.contains("."))
                    {
                        // Lookup or create tag (after inserting some required parameters)...
                        v.tag['project'] = Project.get(params.project.id)
                        v.tag['status'] = DeviceStatus.findByStatus('DEPLOYED')
                        v.tag['transmitterType'] = TransmitterType.findByTransmitterTypeName('PINGER')

                        def tag = tagFactoryService.lookupOrCreate(v.tag)

                        log.debug("Surgery parameters: " + v)
                        v.tag = tag
                        v.type = SurgeryType.get(v.type.id)
                        v.treatmentType = SurgeryTreatmentType.get(v.treatmentType.id)
                        Surgery surgery = new Surgery(v)
                        surgery.release = animalReleaseInstance

                        tag.addToSurgeries(surgery)

                        tag.save()
                        animalReleaseInstance.addToSurgeries(surgery)

                        // Need to update that status of the tag to DEPLOYED.
                        surgery?.tag?.status = deployedStatus

                        // Rescan detections in case they match this new surgery
                        // (ref bug #364).
                        log.debug("Rescanning existing detections for surgery: " + String.valueOf(surgery))
                        
                        assert(!tag.hasErrors())
                        assert(!surgery.hasErrors())
                        
                        detectionFactoryService.rescanForSurgery(surgery)
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                log.error(e)
                flash.message = e.message
                def model = 
                    [animalReleaseInstance: animalReleaseInstance, animal:animalInstance] \
                  + [candidateProjects:candidateEntitiesService.projects()] \
                  + embargoPeriods()

                render(view: "create", model: model)
                return
            }
            
            params.measurement.findAll
            {
                k, v ->

                // See http://stackoverflow.com/questions/1811395/grails-indexed-parameters
                if (!k.contains("."))
                {
                    AnimalMeasurement measurement = new AnimalMeasurement(v)

                    animalReleaseInstance.addToMeasurements(measurement)
                }
            }

            if (   (animalInstance.save(flush:true))
                && (animalReleaseInstance.save(flush: true)))
            {
                flash.message = "${message(code: 'default.created.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), animalReleaseInstance.toString()])}"
                redirect(action: "show", id: animalReleaseInstance.id)
            }
            else 
            {
                log.error(animalReleaseInstance?.errors)
                log.error(animalInstance?.errors)
                render(view: "create", model: [animalReleaseInstance: animalReleaseInstance, animal:animalInstance])
            }
        }
    }

    def show = {
        def animalReleaseInstance = AnimalRelease.get(params.id)
        if (!animalReleaseInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), params.id])}"
            redirect(action: "list")
        }
        else {
            [animalReleaseInstance: animalReleaseInstance]
        }
    }

    def edit = {
        def animalReleaseInstance = AnimalRelease.get(params.id)
        if (!animalReleaseInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), params.id])}"
            redirect(action: "list")
        }
        else {
            def model = [animalReleaseInstance: animalReleaseInstance]
            addEmbargoPeriodsToModel(model)
            model.candidateProjects = candidateEntitiesService.projects()

            return model
        }
    }

    def update = 
    {
        def animalReleaseInstance = AnimalRelease.get(params.id)
        if (animalReleaseInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (animalReleaseInstance.version > version) {
                    
                    animalReleaseInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'animalRelease.label', default: 'AnimalRelease')] as Object[], "Another user has updated this AnimalRelease while you were editing")
                    render(view: "edit", model: [animalReleaseInstance: animalReleaseInstance])
                    return
                }
            }
            
            def animal = animalFactoryService.lookupOrCreate(params)
            
            params.remove('animal')
            params.remove('animal.id')
            log.debug("params: " + params)
            
            animalReleaseInstance.properties = params
            animalReleaseInstance.animal = animal
            
            // Set the embargo date if embargo period (in months) has been specified.
            setEmbargoDate(params, animalReleaseInstance)
            
            if (   !animalReleaseInstance.hasErrors() 
                && !animal.hasErrors()
                && animal.save(flush: true)) 
            {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), animalReleaseInstance.toString()])}"
                redirect(action: "show", id: animalReleaseInstance.id)
            }
            else {
                render(view: "edit", model: [animalReleaseInstance: animalReleaseInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def animalReleaseInstance = AnimalRelease.get(params.id)
        if (animalReleaseInstance) {
            try {
                animalReleaseInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), animalReleaseInstance.toString()])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), animalReleaseInstance.toString()])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'animalRelease.label', default: 'AnimalRelease'), params.id])}"
            redirect(action: "list")
        }
    }
    
    def setEmbargoDate(params, animalReleaseInstance)
    {
        if (params.embargoPeriod)
        {
            Calendar releaseCalendar = animalReleaseInstance.releaseDateTime?.toGregorianCalendar()
            def embargoDate = releaseCalendar.add(Calendar.MONTH, Integer.valueOf(params.embargoPeriod))
            animalReleaseInstance.embargoDate = releaseCalendar.getTime()
        }
        else
        {
            animalReleaseInstance.embargoDate = null
        }
    }
    
    def addEmbargoPeriodsToModel(def model)
    {
        def embargoPeriods = [6: '6 months', 12: '12 months']
        model?.embargoPeriods = embargoPeriods.entrySet()
    }

    def embargoPeriods()
    {
        return [embargoPeriods:[6: '6 months', 12: '12 months']]
    }
}


