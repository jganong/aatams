﻿set search_path = aatams, public;

begin;

-- Fix #329 - Bull shark tag duplicates
DELETE FROM sensor WHERE transmitter_id IN ('A69-9002-14689','A69-9002-14690','A69-9002-14674','A69-9002-14673','A69-9002-14675','A69-9002-14676','A69-9002-1432','A69-9002-14681','A69-9002-14682','A69-9002-14677','A69-9002-14678','A69-9002-1437','A69-9002-1436') AND
tag_id IN (71373811,71373773,71373806,1394086,71373823,71373801,1394082) AND transmitter_type_id = 33; 

---- Test results
-- SELECT transmitter_id, tag_id, transmitter_type_name FROM sensor
-- JOIN transmitter_type tt ON sensor.transmitter_type_id = tt.id
-- WHERE transmitter_id IN ('A69-9002-14689','A69-9002-14690','A69-9002-14674','A69-9002-14673','A69-9002-14675','A69-9002-14676','A69-9002-1432','A69-9002-14681','A69-9002-14682','A69-9002-14677','A69-9002-14678','A69-9002-1437','A69-9002-1436') AND
-- tag_id IN (71373811,71373773,71373806,1394086,71373823,71373801,1394082)
-- ORDER BY transmitter_id;

commit;