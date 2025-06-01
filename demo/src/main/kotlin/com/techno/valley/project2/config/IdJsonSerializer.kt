package com.techno.valley.project2.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.techno.valley.project2.utily.ID

class IdJsonSerializer : JsonSerializer<ID?>() {

    override fun serialize(value: ID?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value != null) {
            gen.writeString(value.toString())
        } else {
            gen.writeNull()
        }
    }
}
