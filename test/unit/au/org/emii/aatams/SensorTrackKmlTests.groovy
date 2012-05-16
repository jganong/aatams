package au.org.emii.aatams

import au.org.emii.aatams.detection.*

import de.micromata.opengis.kml.v_2_2_0.Kml
import grails.test.*
import org.joda.time.DateTime

class SensorTrackKmlTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

	void testTrackNoDetections()
	{
		Kml kml = new SensorTrackKml([])
		
		def expectedKml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document/>
</kml>
'''
		assertKmlEquals(expectedKml, kml)
	}
	
	void testTrackOneDetection()
	{
		def deployment = [station: [longitude: -122f, latitude: 37f]]
			
		Kml kml = new SensorTrackKml([[transmitterId: 'A69-1303-5566', 
									   timestamp: new DateTime("2010-05-28T02:02:09+10:00").toDate(),
									   receiverDeployment: deployment]])
		
		def expectedKml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document>
        <Placemark>
            <name>A69-1303-5566</name>
            <gx:Track>
                <gx:altitudeMode>clampToGround</gx:altitudeMode>
                <when>2010-05-28T02:02:09.000+10:00</when>
                <gx:coord>-122.0 37.0</gx:coord>
            </gx:Track>
        </Placemark>
    </Document>
</kml>
'''
		assertKmlEquals(expectedKml, kml)
	}
	
	void testTrackTwoDetections()
	{
		def deployment1 = [station: [longitude: -122f, latitude: 37f]]
		def deployment2 = [station: [longitude: -145f, latitude: -42f]]
		
		Kml kml = new SensorTrackKml([[transmitterId: 'A69-1303-5566', 
									   timestamp: new DateTime("2010-05-28T02:02:09+10:00").toDate(),
									   receiverDeployment: deployment1],
									  [transmitterId: 'A69-1303-5566', 
									   timestamp: new DateTime("2010-05-28T02:05:13+10:00").toDate(),
									   receiverDeployment: deployment2]])
		
		def expectedKml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document>
        <Placemark>
            <name>A69-1303-5566</name>
            <gx:Track>
                <gx:altitudeMode>clampToGround</gx:altitudeMode>
                <when>2010-05-28T02:02:09.000+10:00</when>
                <when>2010-05-28T02:05:13.000+10:00</when>
                <gx:coord>-122.0 37.0</gx:coord>
                <gx:coord>-145.0 -42.0</gx:coord>
            </gx:Track>
        </Placemark>
    </Document>
</kml>
'''
		assertKmlEquals(expectedKml, kml)
	}
	
	void testTrackTwoDetectionsReverseOrder()
	{
		def deployment1 = [station: [longitude: -122f, latitude: 37f]]
		def deployment2 = [station: [longitude: -145f, latitude: -42f]]
		
		Kml kml = new SensorTrackKml([[transmitterId: 'A69-1303-5566',
									   timestamp: new DateTime("2010-05-28T02:05:13+10:00").toDate(),
									   receiverDeployment: deployment2],
									  [transmitterId: 'A69-1303-5566',
									   timestamp: new DateTime("2010-05-28T02:02:09+10:00").toDate(),
									   receiverDeployment: deployment1]])
		
		def expectedKml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document>
        <Placemark>
            <name>A69-1303-5566</name>
            <gx:Track>
                <gx:altitudeMode>clampToGround</gx:altitudeMode>
                <when>2010-05-28T02:02:09.000+10:00</when>
                <when>2010-05-28T02:05:13.000+10:00</when>
                <gx:coord>-122.0 37.0</gx:coord>
                <gx:coord>-145.0 -42.0</gx:coord>
            </gx:Track>
        </Placemark>
    </Document>
</kml>
'''
		assertKmlEquals(expectedKml, kml)
	}

	void testTrackThreeDetectionsTwoSensors()
	{
		def deployment1 = [station: [longitude: -122f, latitude: 37f]]
		def deployment2 = [station: [longitude: -145f, latitude: -42f]]
		
		Kml kml = new SensorTrackKml([[transmitterId: 'A69-1303-5566', 
									   timestamp: new DateTime("2010-05-28T02:02:09+10:00").toDate(),
									   receiverDeployment: deployment1],
									  [transmitterId: 'A69-1303-7788',
									   timestamp: new DateTime("2010-05-28T02:08:13+10:00").toDate(),
									   receiverDeployment: deployment1],
								      [transmitterId: 'A69-1303-5566', 
									   timestamp: new DateTime("2010-05-28T02:05:13+10:00").toDate(),
									   receiverDeployment: deployment2]])

		def expectedKml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">
    <Document>
        <Placemark>
            <name>A69-1303-5566</name>
            <gx:Track>
                <gx:altitudeMode>clampToGround</gx:altitudeMode>
                <when>2010-05-28T02:02:09.000+10:00</when>
                <when>2010-05-28T02:05:13.000+10:00</when>
                <gx:coord>-122.0 37.0</gx:coord>
                <gx:coord>-145.0 -42.0</gx:coord>
            </gx:Track>
        </Placemark>
        <Placemark>
            <name>A69-1303-7788</name>
            <gx:Track>
                <gx:altitudeMode>clampToGround</gx:altitudeMode>
                <when>2010-05-28T02:08:13.000+10:00</when>
                <gx:coord>-122.0 37.0</gx:coord>
            </gx:Track>
        </Placemark>
    </Document>
</kml>
'''
		assertKmlEquals(expectedKml, kml)
	}
	
	private void assertKmlEquals(String expectedKmlAsString, actualKml)
	{
		StringWriter writer = new StringWriter()
		actualKml.marshal(writer)
		String actualKmlAsString = writer.toString()
		
		if (expectedKmlAsString != actualKmlAsString)
		{
			println "expected:\n" + expectedKmlAsString + "end"
			println "\n\nactual:\n" + actualKmlAsString + "end"
		}
		
		assertEquals(expectedKmlAsString, actualKmlAsString)
	}
}
