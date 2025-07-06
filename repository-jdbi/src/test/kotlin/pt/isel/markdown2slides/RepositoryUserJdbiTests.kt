package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class RepositoryUserJdbiTests: JdbiTests() {

    private val defaultUserId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val defaultUserName = "User1"
    private val defaultUserEmail = "user1@email.com"

    @BeforeEach
    fun clean() {
        runWithHandle { handle: Handle ->
            RepositoryCollaboratorsJdbi(handle).clear()
            RepositoryInvitationsJdbi(handle).clear()
            RepositoryProjectInfoJdbi(handle).clear()
            RepositoryUserJdbi(handle).clear()
        }
    }

    @Test
    fun `test creating a user and finding it by id`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)
            val user = User(defaultUserId, defaultUserName, defaultUserEmail)

            userRepo.save(user)

            val retrievedUser = userRepo.findById(defaultUserId)
            assert(retrievedUser != null) { "User should be created successfully" }
            assert(retrievedUser!!.id == defaultUserId) { "User ID should match" }
            assert(retrievedUser.name == defaultUserName) { "User name should match" }
            assert(retrievedUser.email == defaultUserEmail) { "User email should match" }
        }
    }

    @Test
    fun `test trying to find user that does not exist`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)

            val retrievedUser = userRepo.findById(defaultUserId)
            assert(retrievedUser == null) { "User should not be found" }
        }
    }

    @Test
    fun `test finding a user by email`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)
            val user = User(defaultUserId, defaultUserName, defaultUserEmail)

            userRepo.save(user)

            val retrievedUser = userRepo.findByEmail(defaultUserEmail)
            assert(retrievedUser != null) { "User should be found by email" }
            assert(retrievedUser!!.id == defaultUserId) { "User ID should match" }
            assert(retrievedUser.name == defaultUserName) { "User name should match" }
        }
    }

    @Test
    fun `test trying to find user by email that does not exist`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)

            val retrievedUser = userRepo.findByEmail(defaultUserEmail)
            assert(retrievedUser == null) { "User should not be found by email" }
        }
    }

    @Test
    fun `test finding all users`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)

            val usersBefore = userRepo.findAll()
            assert(usersBefore.isEmpty()) { "There should be no users in the repository initially" }

            val user1 = User(defaultUserId, defaultUserName, defaultUserEmail)
            val user2 = User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "User2", "user2@email.com")
            userRepo.save(user1)
            userRepo.save(user2)

            val users = userRepo.findAll()

            assert(users.size == 2) { "There should be two users in the repository" }
            assert(users.any { it.id == defaultUserId && it.name == defaultUserName && it.email == defaultUserEmail }) { "First user should be present" }
            assert(users.any { it.id == user2.id && it.name == user2.name && it.email == user2.email }) { "Second user should be present" }
        }
    }

    @Test
    fun `test deleting a user`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)
            val user = User(defaultUserId, defaultUserName, defaultUserEmail)
            userRepo.save(user)

            userRepo.deleteById(defaultUserId)

            val retrievedUser = userRepo.findById(defaultUserId)
            assert(retrievedUser == null) { "User should be deleted successfully" }

            val allUsers = userRepo.findAll()
            assert(allUsers.isEmpty()) { "There should be no users in the repository after deletion" }
        }
    }

    @Test
    fun `test deleting a user that does not exist`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)

            userRepo.deleteById(defaultUserId)

            assert(true) { "Deleting a non-existing user should not throw an error" }
        }
    }

    @Test
    fun `test clear removes every user`(){
        runWithHandle { handle ->
            val userRepo = RepositoryUserJdbi(handle)
            val user1 = User(defaultUserId, defaultUserName, defaultUserEmail)
            val user2 = User(UUID.fromString("00000000-0000-0000-0000-000000000002"), "User2", "user2@email.com")
            userRepo.save(user1)
            userRepo.save(user2)

            userRepo.clear()

            val allUsers = userRepo.findAll()
            assert(allUsers.isEmpty()) { "Clear should remove all users from the repository"}
        }
    }

}