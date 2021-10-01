package ru.sber.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JsonCustomClassDeserializer {

    @Test
    fun `Нобходимо десериализовать данные в класс`() {
        // given
        val data = """{"client": "Иванов Иван Иванович"}"""
        val objectMapper = ObjectMapper()
            .registerModules(KotlinModule(), SimpleModule().addDeserializer(Client7::class.java, CustomDeserializer()))


        // when
        val client = objectMapper.readValue<Client7>(data)

        // then
        assertEquals("Иван", client.firstName)
        assertEquals("Иванов", client.lastName)
        assertEquals("Иванович", client.middleName)
    }

    class CustomDeserializer() : StdDeserializer<Client7>(Client7::class.java) {


        override fun deserialize(p0: JsonParser, p1: DeserializationContext): Client7 {
            val client = p0.readValueAsTree<ObjectNode>().get("client").toString()
            val arrOfNames = client.replace("\"", "").split(" ")
            return Client7(firstName = arrOfNames[1],
                            lastName = arrOfNames[0],
                            middleName = arrOfNames[2])
        }
    }
}
