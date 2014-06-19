/*
 * Copyright 2010 Rob Fletcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.energizedwork.grails.plugins.jodatime

import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

class DateTimeZoneTagLib {

    static namespace = "joda"

    static final ZONE_FORMATTER = DateTimeFormat.forPattern("ZZ")

    /**
     * A helper tag for creating DateTimeZone selects
     * e.g. <joda:dateTimeZoneSelect name="myTimeZone" value="${tz}" />
     */
    def dateTimeZoneSelect = {attrs ->

        log.debug "attrs = ${attrs}"
        attrs.from = DateTimeZone.getAvailableIDs();
        attrs.value = attrs.value?.ID ?: DateTimeZone.default.ID
        def time = DateTimeUtils.currentTimeMillis()

        // set the option value as a closure that formats the DateTimeZone for display
        attrs.optionValue = {
            return "$it"
        }

        // use generic select
        out << select(attrs)
    }

}
