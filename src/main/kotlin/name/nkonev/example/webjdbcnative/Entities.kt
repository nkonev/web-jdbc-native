package name.nkonev.example.webjdbcnative

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID


@Table("subject")
data class Subject(@Id val subjectId: Int, var subjectDesc: String, var subjectName: String, val branchId: UUID)

data class Child(var name: String)

@Table("branch")
data class Branch(
    @Id val branchId: UUID?,
    var branchName: String,
    @Column("branch_short_name") var branchShortName: String,
    var description: String? = null,
    @MappedCollection(idColumn = "branch_id")
    private val subjects: MutableSet<Subject> = HashSet(),
    @Column("branch_id")
    private var branchInfo: BranchInfo? = null,
//    private var jsonData: List<Child>
    @Transient
    private val new: Boolean
): Persistable<UUID> {

    //    fun addSubject(subject: Subject) {
//        subjects.add(SubjectRef(subject.subjectId.toLong()))
//    }
    override fun getId(): UUID? {
        return branchId
    }

    override fun isNew(): Boolean {
        return new
    }

    @PersistenceCreator
    constructor(
        branchId: UUID?,
        branchName: String,
        branchShortName: String,
        description: String?,
        branchInfo: BranchInfo?
    ): this(
        branchId,
        branchName,
        branchShortName,
        description,
        mutableSetOf(),
        branchInfo,
        false
    )
}

@Table("branch_info")
data class BranchInfo(@Id val branchInfoId: Int, var branchInfoData: String, val branchId: Long)
