package au.org.emii.aatams

class TagFactoryService {

    static transactional = true

    def create(params) throws IllegalArgumentException {
        Tag tag = new Tag(params.tag)
        Sensor sensor = new Sensor(params)
        tag.addToSensors(sensor)

        if (tag.save(validate: true)) {
            log.info("Saved tag: " + String.valueOf(tag))
        }
        else {
            log.error(tag.errors)
        }

        return tag
    }

    def update(params) throws IllegalArgumentException {
        Tag tag = Tag.get(params.tag.id)
        Sensor sensor = new Sensor(params)
        tag.addToSensors(sensor)

        if (tag.save(validate: true)) {
            log.info("Saved tag: " + String.valueOf(tag))
        }
        else {
            log.error(tag.errors)
        }

        return tag
    }

    def lookupOrCreate(params) throws IllegalArgumentException {
        def tag = Tag.findBySerialNumber(params.serialNumber)

        if (tag == null) {
            log.info("Tag with serial number " + params.serialNumber + " not found, creating new, params: " + params)
            tag = createNewTag(params)
        }
        else {
            log.debug("Tag with serial number " + params.serialNumber + " found.")
        }

        loadDeviceStatus(tag, params)

        if (!tag.project) {
            tag.project = params.project
        }
        else if (tag.project != params.project) {
            log.warn("Tag released in different project, tag's project: " + tag.project + ", release project: " + params.project)
        }

        if (tag.save(validate: true, flush:true)) {
            log.info("Saved tag: " + String.valueOf(tag))
        }
        else {
            log.error(tag.errors)
        }

        return tag
    }

    private Tag createNewTag(params) {
        initDefaults(params)

        return new Tag(params)
    }

    private initDefaults(params) {
        if (!params.status) {
            params.status = DeviceStatus.NEW
        }
    }

    private loadDeviceStatus(tag, params) {
        if (params.status) {
            tag.status = params.status
        }
    }
}
