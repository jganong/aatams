databaseChangeLog = {

    changeSet(author: "jburgess", id: "1430268900000-01") {

        createTable(tableName: "detection") {
            column(name: "id", type: "serial") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "detection_pkey")
            }

            // The following three columns form the "real" primary key of the detection table.
            column(name: "timestamp", type: "TIMESTAMP WITH TIME ZONE") {
                constraints(nullable: "false")
            }
            column(name: "receiver_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "transmitter_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "transmitter_name", type: "VARCHAR(255)")
            column(name: "transmitter_serial_number", type: "VARCHAR(255)")
            column(name: "sensor_value", type: "FLOAT4(8,8)")
            column(name: "sensor_unit", type: "VARCHAR(255)")
            column(name: "station_name", type: "VARCHAR(255)")

            column(name: "latitude", type: "FLOAT4(8,8)")
            column(name: "longitude", type: "FLOAT4(8,8)")

            column(name: "receiver_download_id", type: "int8") {
                constraints(nullable: "false")
            }

            column(name: "duplicate", type: "boolean")
        }
    }

    changeSet(author: "jburgess", id: "1430268900000-02") {
        grailsChange {
            change {
                // This provides a way to "pause" the migrations, in order to complete the manual load.
                log.warn "Waiting for file '/tmp/detections_loading' to disappear before moving on..."
                while (new File('/tmp/detections_loading').exists()) {
                    sleep(10000)
                }

                sql.execute("""
                    UPDATE aatams.detection
                    SET duplicate = true
                    WHERE ctid = ANY(
                      ARRAY(
                        SELECT ctid FROM (
                          SELECT id, ROW_NUMBER() OVER(PARTITION BY timestamp, transmitter_id, receiver_name ORDER BY id ASC) > 1 AS duplicate, ctid
                          FROM aatams.detection
                        ) d WHERE duplicate = true
                      )
                    )
                    """)
            }
        }
    }

    changeSet(author: "jburgess", id: "1430268900000-03") {
        createProcedure(
            '''CREATE OR REPLACE FUNCTION set_detection_duplicate_status()
               RETURNS TRIGGER AS $$

                DECLARE
                  changed_row aatams.detection%ROWTYPE;

                BEGIN
                  IF (TG_OP = 'DELETE') THEN
                    changed_row = OLD;
                  ELSE
                    changed_row = NEW;
                  END IF;

                  UPDATE aatams.detection
                  SET duplicate = subquery.duplicate
                  FROM (
                    SELECT id,
                    ROW_NUMBER() OVER(PARTITION BY timestamp, transmitter_id, receiver_name ORDER BY id asc) > 1
                      AS duplicate
                    FROM aatams.detection
                    WHERE timestamp = changed_row.timestamp
                      AND transmitter_id = changed_row.transmitter_id
                      AND receiver_name = changed_row.receiver_name
                  ) subquery
                  WHERE detection.id = subquery.id;

                  RETURN changed_row;
                END;

                $$ LANGUAGE plpgsql;'''
        )

        grailsChange {
            change {
                sql.execute(
                    '''CREATE TRIGGER check_for_detection_duplicates
                       AFTER INSERT OR DELETE ON detection
                         FOR EACH ROW EXECUTE PROCEDURE set_detection_duplicate_status();'''
                )
            }
        }
    }

    changeSet(author: "jburgess", id: "1430268900000-04") {

        [ 'timestamp', 'receiver_name', 'transmitter_id', 'receiver_download_id', 'duplicate' ].each { columnName ->
            createIndex(indexName: "detection_${columnName}_index", tableName: 'detection', unique: 'false') {
                column(name: columnName)
            }
        }

        addForeignKeyConstraint(baseColumnNames: "receiver_download_id", baseTableName: "detection", constraintName: "receiver_download_id_detection_fk", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "receiver_download_file", referencesUniqueColumn: "false")
    }

    changeSet(author: "jburgess", id: "1430268900000-05") {
        dropTable(tableName: 'invalid_detection', cascadeConstraints: true)
        dropTable(tableName: 'valid_detection', cascadeConstraints: true)
    }

    changeSet(author: "jburgess", id: "1430268900000-06", runOnChange: true) {

        grailsChange {
            change {
                sql.execute(
                    '''CREATE MATERIALIZED VIEW receiver as
                         SELECT device.id, model_name || '-' || serial_number AS receiver_name, device.organisation_id
                         FROM device
                         JOIN device_model ON device.model_id = device_model.id
                         WHERE device.class = 'au.org.emii.aatams.Receiver'
                       WITH DATA'''
                )
            }
        }

        createProcedure(
            '''CREATE FUNCTION refresh_receiver_mv()
               RETURNS trigger AS $$
                 BEGIN
                   REFRESH MATERIALIZED VIEW receiver;
                   RETURN NEW;
                 END;
               $$ LANGUAGE plpgsql;'''
        )

        [ 'device', 'device_model' ].each { triggerTable ->

            grailsChange {
                change {
                    def triggerName = "${triggerTable}_trigger"

                    sql.execute(
                        '''CREATE TRIGGER ''' + triggerName + ''' AFTER INSERT OR UPDATE OR DELETE ON ''' + triggerTable +
                        ''' FOR EACH STATEMENT
                           EXECUTE PROCEDURE refresh_receiver_mv();'''
                    )
                }
            }
        }

        createView(
            '''select receiver_id, receiver_deployment.id as receiver_deployment_id,
                 receiver_recovery.id as receiver_recovery_id,
                 initialisationdatetime_timestamp, recoverydatetime_timestamp
               from receiver_recovery
               join receiver_deployment on receiver_recovery.deployment_id = receiver_deployment.id''',
                   viewName: 'deployment_and_recovery'
        )

        createProcedure(
            '''CREATE FUNCTION invalid_reason(
                 detection detection,
                 receiver receiver,
                 deployment_and_recovery deployment_and_recovery)
               RETURNS text AS $$
                 BEGIN
                   IF detection.duplicate THEN
                     RETURN 'DUPLICATE';
                   ELSIF receiver.id IS NULL THEN
                     RETURN 'UNKNOWN_RECEIVER';
                   ELSIF deployment_and_recovery.receiver_deployment_id IS NULL THEN
                     RETURN 'NO_DEPLOYMENT_AND_RECOVERY_AT_DATE_TIME';
                   END IF;

                   RETURN NULL;
                 END;
               $$ LANGUAGE plpgsql;'''
        )
    }

    changeSet(author: "jburgess", id: "1430268900000-07", runOnChange: true) {
        [ 'receiver_deployment', 'species' ].each { tableName ->
            dropColumn(tableName: tableName, columnName: 'embargo_date')
        }
    }

    changeSet(author: "jburgess", id: "1430268900000-08", runOnChange: true) {
        createView(
            '''
              SELECT
                detection.id AS detection_id,
                detection."timestamp",
                detection.receiver_name,
                detection.transmitter_id AS transmitter_id,
                detection.transmitter_name,
                detection.transmitter_serial_number,
                detection.sensor_value,
                detection.sensor_unit,
                detection.station_name,
                detection.latitude,
                detection.longitude,

                detection.receiver_download_id,
                sec_user.name AS uploader,
                rxr_organisation.name as organisation_name,

                deployment_and_recovery.receiver_deployment_id,
                rxr_project.name AS rxr_project_name,
                installation.name AS installation_name,
                station.id AS station_id,
                station.name AS station_station_name,
                st_y(station.location) AS station_latitude,
                st_x(station.location) AS station_longitude,
                species.spcode,
                species.scientific_name,
                species.common_name,
                invalid_reason(detection.*, receiver.*, deployment_and_recovery.*) AS invalid_reason,

                sensor.transmitter_id as sensor_id,
                tag.project_id as tag_project_id,
                surgery.id AS surgery_id,
                release.id AS release_id,
                release.project_id AS release_project_id,
                embargo_date


              FROM detection

                JOIN receiver_download_file ON detection.receiver_download_id = receiver_download_file.id
                JOIN sec_user ON receiver_download_file.requesting_user_id = sec_user.id

                LEFT JOIN receiver ON detection.receiver_name::text = receiver.receiver_name
                LEFT JOIN organisation rxr_organisation ON receiver.organisation_id = rxr_organisation.id
                LEFT JOIN deployment_and_recovery deployment_and_recovery
                  ON receiver.id = deployment_and_recovery.receiver_id
                  AND detection."timestamp" >= deployment_and_recovery.initialisationdatetime_timestamp
                  AND detection."timestamp" <= deployment_and_recovery.recoverydatetime_timestamp
                LEFT JOIN receiver_deployment ON deployment_and_recovery.receiver_deployment_id = receiver_deployment.id
                LEFT JOIN installation_station station ON receiver_deployment.station_id = station.id
                LEFT JOIN installation ON station.installation_id = installation.id
                LEFT JOIN project rxr_project ON installation.project_id = rxr_project.id

                LEFT JOIN sensor ON detection.transmitter_id = sensor.transmitter_id
                LEFT JOIN device tag ON sensor.tag_id = tag.id
                LEFT JOIN surgery ON tag.id = surgery.tag_id
                LEFT JOIN animal_release release ON surgery.release_id = release.id
                LEFT JOIN animal ON release.animal_id = animal.id
                LEFT JOIN species ON animal.species_id = species.id
             ''',
             viewName: 'detection_view'
        )

        createView(
            '''SELECT * FROM detection_view WHERE invalid_reason IS NULL''',
                   viewName: 'valid_detection'
        )

        createView(
            '''SELECT * FROM detection_view WHERE invalid_reason IS NOT NULL''',
                   viewName: 'invalid_detection'
        )
    }

    changeSet(author: "jburgess", id: "1430268900000-09") {
        createView("""
            SELECT
              station_station_name AS station,
              installation_name AS installation,
              rxr_project_name AS project,
              aatams.scramblepoint(st_makepoint(station_longitude, station_latitude)) AS public_location,
              st_x(aatams.scramblepoint(st_makepoint(station_longitude, station_latitude))) AS public_lon,
              st_y(aatams.scramblepoint(st_makepoint(station_longitude, station_latitude))) AS public_lat,
              ('http://localhost:8080/aatams/installationStation/show/'::text || station_id) AS installation_station_url,
              ('http://localhost:8080/aatams/detection/lISt?filter.receiverDeployment.station.in=name&filter.receiverDeployment.station.in='::text || (station_station_name)::text) AS detection_download_url,
              count(*) AS detection_count,
              ((log((GREATEST(count(*), (1)::bigint))::double precISion) / log(((SELECT max(t.detection_count) AS max FROM (
                SELECT
                  station_station_name,
                  count(station_station_name) AS detection_count
                FROM aatams.detection_view GROUP BY station_station_name
                ) t))::double precision)) * (10)::double precision) AS relative_detection_count,
              station_id

            FROM aatams.detection_view
            WHERE station_id IS not null

            GROUP BY
              station_station_name,
              installation_name,
              rxr_project_name,
              station_longitude,
              station_latitude,
              station_id

            UNION ALL
            SELECT
              installation_station.name AS station,
              installation.name AS installation,
              project.name AS project,
              aatams.scramblepoint(installation_station.location) AS public_location,
              st_x(aatams.scramblepoint(installation_station.location)) AS public_lon,
              st_y(aatams.scramblepoint(installation_station.location)) AS public_lat,
              ('http://localhost:8080/aatams/installationStation/show/'::text || installation_station.id) AS installation_station_url,
              '' AS detection_download_url,
              0 AS detection_count,
              0 AS relative_detection_count,
              installation_station.id AS station_id
            FROM (((aatams.installation_station LEFT JOIN aatams.installation ON ((installation_station.installation_id = installation.id)))
              LEFT JOIN aatams.project ON ((installation.project_id = project.id)))
              LEFT JOIN aatams.detection_view ON ((installation_station.id = detection_view.station_id)))
            WHERE (detection_view.station_id IS NULL);
                        """,
                   viewName: 'detection_count_per_station'
                  )
    }
}
