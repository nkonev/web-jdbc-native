package name.nkonev.example.webjdbcnative

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class WebJdbcNativeApplication()

fun main(args: Array<String>) {
	runApplication<WebJdbcNativeApplication>(*args)
}

@Component
class AppRunner(private val subjectRepository: SubjectRepository,
                private val branchRepository: BranchRepository) : ApplicationRunner {

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
}

@RestController
class MyController(private val subjectRepository: SubjectRepository) {

    @GetMapping("/subject")
    fun get() : Iterable<Subject> {
        return subjectRepository.findAll();
    }
}
