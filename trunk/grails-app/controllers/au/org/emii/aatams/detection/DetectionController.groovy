package au.org.emii.aatams.detection

class DetectionController {

    def candidateEntitiesService

    static allowedMethods = [update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : grailsApplication.config.grails.gorm.default.list.max, 100)
        [detectionInstanceList: ValidDetection.list(params), detectionInstanceTotal: ValidDetection.count()]
    }

    def create = 
    {
        redirect(controller:"receiverDownloadFile", 
                 action:"createDetections") 
    }
    
    def show = {
        def detectionInstance = ValidDetection.get(params.id)
        if (!detectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'detection.label', default: 'ValidDetection'), params.id])}"
            redirect(action: "list")
        }
        else {
            [detectionInstance: detectionInstance]
        }
    }

    def edit = {
        def detectionInstance = ValidDetection.get(params.id)
        if (!detectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'detection.label', default: 'ValidDetection'), params.id])}"
            redirect(action: "list")
        }
        else 
        {
            def model = [detectionInstance: detectionInstance]
            model.candidateDeployments = candidateEntitiesService.deployments()
            return model
        }
    }

    def update = {
        def detectionInstance = ValidDetection.get(params.id)
        if (detectionInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (detectionInstance.version > version) {
                    
                    detectionInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'detection.label', default: 'ValidDetection')] as Object[], "Another user has updated this ValidDetection while you were editing")
                    render(view: "edit", model: [detectionInstance: detectionInstance])
                    return
                }
            }
            detectionInstance.properties = params
            if (!detectionInstance.hasErrors() && detectionInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'detection.label', default: 'ValidDetection'), detectionInstance.toString()])}"
                redirect(action: "show", id: detectionInstance.id)
            }
            else {
                render(view: "edit", model: [detectionInstance: detectionInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'detection.label', default: 'ValidDetection'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def detectionInstance = ValidDetection.get(params.id)
        if (detectionInstance) {
            try {
                detectionInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'detection.label', default: 'ValidDetection'), detectionInstance.toString()])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'detection.label', default: 'ValidDetection'), detectionInstance.toString()])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'detection.label', default: 'ValidDetection'), params.id])}"
            redirect(action: "list")
        }
    }
}
