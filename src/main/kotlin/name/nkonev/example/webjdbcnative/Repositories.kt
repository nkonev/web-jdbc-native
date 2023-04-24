package name.nkonev.example.webjdbcnative

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface SubjectRepository : CrudRepository<Subject, Long>

@Repository
interface BranchRepository : CrudRepository<Branch, UUID>

@Repository
interface BranchInfoRepository: CrudRepository<BranchInfo, Long>