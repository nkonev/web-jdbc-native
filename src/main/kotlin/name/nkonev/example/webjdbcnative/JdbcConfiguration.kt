package name.nkonev.example.webjdbcnative


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import java.nio.ByteBuffer
import java.util.*


@EnableJdbcAuditing
@EntityScan(basePackages = ["name.nkonev.example.webjdbcnative"])
@EnableJdbcRepositories(basePackages = ["name.nkonev.example.webjdbcnative"])
@Configuration
class JdbcConfiguration(private val objectMapper: ObjectMapper) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<Converter<*, *>> {
        return listOf(
            JsonNodeWritingConverter(objectMapper),
            JsonNodeReadingConverter(objectMapper),
            ListChildWritingConverter(objectMapper),
            ListChildReadingConverter(objectMapper),
        )
    }
}

internal abstract class AbstractJsonReadingConverter<T>(
    private val objectMapper: ObjectMapper,
    private val valueType: Class<T>
) : Converter<PGobject, T> {

    override fun convert(source: PGobject): T {
        return objectMapper.readValue(source.value, valueType)
    }
}

internal abstract class AbstractJsonWritingConverter<T> (
    private val objectMapper: ObjectMapper
) : Converter<T, PGobject> {

    override fun convert(source: T): PGobject {
        val res = PGobject()
        res.type = "jsonb"
        res.value = objectMapper.writeValueAsString(source)
        return res
    }
}

internal class JsonNodeWritingConverter(objectMapper: ObjectMapper) :
    AbstractJsonWritingConverter<JsonNode>(objectMapper)


internal class JsonNodeReadingConverter(objectMapper: ObjectMapper) :
    AbstractJsonReadingConverter<JsonNode>(objectMapper, JsonNode::class.java)


//class ListChildReadingConverter(
//    private val objectMapper: ObjectMapper,
//) : Converter<PGobject, List<Child>> {
//
//    override fun convert(source: PGobject): List<Child> {
//        return objectMapper.readValue(source.value, valueType)
//    }
//}
//
//class ListChildWritingConverter (
//    private val objectMapper: ObjectMapper
//) : Converter<List<Child>, PGobject> {
//
//    override fun convert(source: List<Child>): PGobject {
//        val res = PGobject()
//        res.type = "jsonb"
//        res.value = objectMapper.writeValueAsString(source)
//        return res
//    }
//}

internal abstract class AbstractJsonReadingConverter2<T>(
    private val objectMapper: ObjectMapper,
    private val valueType: TypeReference<T>
) : Converter<PGobject, T> {

    override fun convert(source: PGobject): T {
        return objectMapper.readValue(source.value, valueType)
    }
}

internal abstract class AbstractJsonWritingConverter2<T> (
    private val objectMapper: ObjectMapper
) : Converter<T, PGobject> {

    override fun convert(source: T): PGobject {
        val res = PGobject()
        res.type = "jsonb"
        res.value = objectMapper.writeValueAsString(source)
        return res
    }
}

internal class ListChildWritingConverter(objectMapper: ObjectMapper) :
    AbstractJsonWritingConverter2<List<Child>>(objectMapper)


internal class ListChildReadingConverter(objectMapper: ObjectMapper) :
    AbstractJsonReadingConverter2<List<Child>>(objectMapper, object: TypeReference<List<Child>>() {})
