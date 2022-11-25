package name.nkonev.example.webjdbcnative

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface SubjectRepository : CrudRepository<Subject, Long>

@Repository
interface BranchRepository : CrudRepository<Branch, Long>