package name.nkonev.example.webjdbcnative

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.http.ProblemDetail
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@ImportRuntimeHints(LiquibaseRuntimeHints::class)
@EnableScheduling
@SpringBootApplication
class WebJdbcNativeApplication()

fun main(args: Array<String>) {
	runApplication<WebJdbcNativeApplication>(*args)
}

const val queueName = "my-queue"

@Configuration
class RabbitMqConfig(private val objectMapper: ObjectMapper){
    @Bean
    fun createQueue() : Queue {
        return Queue(queueName)
    }

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        producerJackson2MessageConverter: Jackson2JsonMessageConverter
    ): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = producerJackson2MessageConverter
        return rabbitTemplate
    }

    @Bean
    fun producerJackson2MessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }
}

class LiquibaseRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerPattern("db/changelog/*.sql")
        hints.resources().registerPattern("db/changelog.yml")
    }

}

@Component
class AppRunner(private val subjectRepository: SubjectRepository,
                private val branchRepository: BranchRepository,
                private val rabbitTemplate: RabbitTemplate,
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run(args: ApplicationArguments) {
        branchRepository.deleteAll()
        subjectRepository.deleteAll()

        val subj1: Subject = subjectRepository.save(Subject(0, "Software Engineering", "Apply key aspects of software engineering processes for the development of a complex software system"))
        val subj2: Subject = subjectRepository.save(Subject(0, "Distributed System", "Explore recent advances in distributed computing systems"))
        val subj3: Subject = subjectRepository.save(Subject(0, "Business Analysis and Optimization", "understand the Internal and external factors that impact the business strategy"))
        val branch1: Branch = Branch(0, "Computer Science and Engineering", "CSE", "CSE department offers courses under ambitious curricula in computer science and computer engineering..")
        branch1.addSubject(subj1)
        branch1.addSubject(subj2)
        val createdBranch1: Branch = branchRepository.save(branch1)
        logger.info("Created first branch {}", createdBranch1)

        val branch2: Branch = Branch(0, "Information Technology", "IT", "IT is the business side of computers - usually dealing with databases, business, and accounting")
        branch2.addSubject(subj1)
        branch2.addSubject(subj3)
        val createdBranch2: Branch = branchRepository.save(branch2)
        logger.info("Created second branch {}", createdBranch2)

        logger.info("Deleting first branch {}", createdBranch1)
        branchRepository.delete(createdBranch1)
        logger.info("Searching for first branch {}", branchRepository.existsById(createdBranch1.branchId))

        logger.info("Deleting second branch {}", createdBranch2)
        branchRepository.delete(createdBranch2)
        logger.info("Searching for second branch {}", branchRepository.existsById(createdBranch2.branchId))

        logger.info("Checking if branches still presents")
        val allBranches = branchRepository.findAll()
        allBranches.forEach { logger.info("Found branch {}", it) }

        logger.info("Checking if subjects still presents")
        val allSubjects = subjectRepository.findAll()
        allSubjects.forEach { logger.info("Found subject {}", it) }
    }


    @Scheduled(cron = "* * * * * *")
    fun send() {
        val allSubjects = subjectRepository.findAll()
        allSubjects.forEach {
            logger.info("Sending subject {}", it)
            rabbitTemplate.convertAndSend(queueName, it)
        }
    }
}

@RestController
class MyController(private val subjectRepository: SubjectRepository) {

    @GetMapping("/subject")
    fun get() : Iterable<Subject> {
        return subjectRepository.findAll();
    }

    @GetMapping("/get-me-exception-please")
    fun getErrorPlease() {
        throw RuntimeException("Aaah!")
    }

    @ExceptionHandler
    fun eh (e: Exception): ProblemDetail {
        val bldr = ProblemDetail.forStatus(500)
        bldr.detail = e.message
        bldr.title = "Something bad has happened"
        return bldr
    }
}


@Component
class MyListener() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = [queueName])
    fun listen(subj: Subject) {
        logger.info("Received subject {} over RabbitMQ", subj)
    }
}
