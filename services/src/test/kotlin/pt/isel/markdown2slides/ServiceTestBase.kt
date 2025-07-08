package pt.isel.markdown2slides

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import pt.isel.markdown2slides.data.mem.TransactionManagerInMem
import pt.isel.markdown2slides.file.RepositoryProjectContent
import java.io.File
import java.nio.file.Files
import java.util.*

abstract class ServiceTestsBase {

    protected lateinit var trxManager: TransactionManagerInMem
    private lateinit var tempDir: String
    protected lateinit var repoProjectContent: RepositoryProjectContent

    private val testDataDir = "testData"

    protected val firstUserId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    protected val firstUserName = "User 1"
    protected val firstUserEmail = "user1@email.com"

    protected val secondUserId: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
    protected val secondUserName = "User 2"
    protected val secondUserEmail = "user2@email.com"

    protected val projectName = "Test Project"
    protected val projectDescription = "This is a test project"
    protected val projectVisibility = Visibility.PRIVATE



    @BeforeEach
    fun setupBase() {
        trxManager = TransactionManagerInMem()
        tempDir = Files.createTempDirectory(testDataDir).toString()
        repoProjectContent = RepositoryProjectContentFileSystem(baseDir = tempDir)
    }

    @AfterEach
    fun cleanupBase() {
        File(tempDir).deleteRecursively()
    }

    fun addUser(id: UUID, name: String, email: String) {
        trxManager.run {
            repoUser.save(User(id, name, email))
        }
    }
}
