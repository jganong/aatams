databaseChangeLog = 
{
	changeSet(author: "jburgess", id: "1332135917000-1")
	{
		grailsChange
		{
			change
			{
				def viewName = application.config.rawDetection.extract.view.name
				def viewSelect = application.config.rawDetection.extract.view.select
				
				// Need to drop first, see: http://dba.stackexchange.com/questions/586/cant-rename-columns-in-postgresql-views-with-create-or-replace
				sql.execute ('drop view ' + viewName)
				sql.execute ('create or replace view ' + viewName + ' as ' + viewSelect)
			}
		}
	}
	
	changeSet(author: "jburgess", id: "1332135917000-2", runOnChange: true)
	{
		createProcedure('''CREATE OR REPLACE FUNCTION isreleaseembargoed(release_id bigint) RETURNS boolean AS
			$$
			DECLARE
				found_animal_release animal_release%ROWTYPE;
			BEGIN
				SELECT INTO found_animal_release * FROM animal_release WHERE id = release_id;
				RETURN found_animal_release.embargo_date > current_date;
			END;
			$$ LANGUAGE plpgsql STRICT;''')
	}
	
	changeSet(author: "jburgess", id: "1332135917000-3", runOnChange: true)
	{
		dropView(viewName: "public_detection_view")
		createView('''select *,
						round(latitude::NUMERIC, 2) as public_latitude,
						round(longitude::NUMERIC, 2) as public_longitude,
						st_point(longitude, latitude) as public_location,
						case when isreleaseembargoed(animal_release_id) then 'embargoed'
						     else species_name
						end
						as public_species_name,
						case when isreleaseembargoed(animal_release_id) then 'embargoed'
						     else spcode
						end
						as public_spcode
						
						from detection_extract_view
						''', viewName: "public_detection_view")
	}

	/**
select station, installation, public_location, count(*)
from public_detection_view
group by station, installation, public_location;
	*/
}
