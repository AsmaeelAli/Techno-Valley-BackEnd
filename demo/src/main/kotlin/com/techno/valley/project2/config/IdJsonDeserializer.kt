package com.techno.valley.project2.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.techno.valley.project2.utily.ID

class IdJsonDeserializer : JsonDeserializer<ID?>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ID? =
        try {
            p.valueAsString.toLong()
            /* .toID does not work !! */
        } catch (ex: Exception) {
            null
        }
}
