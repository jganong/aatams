package au.org.emii.aatams.detection

import au.org.emii.aatams.test.AbstractControllerIntegrationTestCase

import grails.test.*

class DetectionControllerIntegrationTests extends AbstractControllerIntegrationTestCase {
    def dataSource

    protected void setUp() {
        super.setUp()

        permitted = true
        controller.params.format = "CSV"
    }

    void testExecuteDetectionExtract() {
        // Note: This test seems to fail intermittently for me - DN

        assertExport([:], "testExecuteDetection")
    }

    void testDetectionExtractWithReadPermission() {
        permitted = true
        authenticated = true

        setupAndExecuteWhaleDetectionExtract()

        assertContainsAllLines(controller.response.contentAsString,
            '''timestamp,station name,latitude,longitude,receiver ID,tag ID,species,uploader,transmitter ID,organisation,sensor value,sensor unit
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-7777,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-7777,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-7777,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,''')
    }

    void testDetectionExtractWithoutReadPermission() {
        permitted = false
        authenticated = true

        setupAndExecuteWhaleDetectionExtract()
        def expected = '''timestamp,station name,latitude,longitude,receiver ID,tag ID,species,uploader,transmitter ID,organisation,sensor value,sensor unit
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.1234,76.02,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,'''

        assertContainsAllLines(controller.response.contentAsString, expected)
    }

    void testDetectionExtractWithoutAuthentication() {
        permitted = false
        authenticated = false

        setupAndExecuteWhaleDetectionExtract()

        def expected = '''timestamp,station name,latitude,longitude,receiver ID,tag ID,species,uploader,transmitter ID,organisation,sensor value,sensor unit
2011-05-17 02:54:00,Whale Station,-20.12,76.01,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.12,76.01,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.12,76.01,VR2W-103377,A69-1303-6666,41110001 - Eubalaena australis (southern right whale),Joe Bloggs,A69-1303-6666,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-7777,IMOS,,
2011-05-17 02:54:01,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:02,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,
2011-05-17 02:54:00,Whale Station,-20.12,76.01,VR2W-103377,,,Joe Bloggs,A69-1303-8888,IMOS,,'''

        assertContainsAllLines(controller.response.contentAsString, expected)
    }

    private void setupAndExecuteWhaleDetectionExtract() {
        hasRole = false

        controller.params.filter = [receiverDeployment:[station:[installation:[project:[in:["name", "Whale"]]]]]]
        controller.export()
    }
}
