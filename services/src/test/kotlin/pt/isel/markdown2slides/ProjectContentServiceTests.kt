package pt.isel.markdown2slides

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertIs


class ProjectContentServiceTests : ServiceTestsBase() {

    private lateinit var contentService: ProjectContentService
    private lateinit var projectInfoService: ProjectInfoService

    @BeforeEach
    fun setupService() {
        contentService = ProjectContentService(trxManager, repoProjectContent)
        projectInfoService = ProjectInfoService(trxManager, repoProjectContent)
        addUser(firstUserId, firstUserName, firstUserEmail)
        addUser(secondUserId, secondUserName, secondUserEmail)
    }

    @Test
    fun `updateProjectContent stores and retrieves markdown correctly`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let {
            assertTrue(it is Either.Right)
            (it as Either.Right).value
        }

        val markdown = "# Hello Markdown"
        val updateResult = contentService.updateProjectContent(firstUserId, project.id, markdown)
        assertIs<Either.Right<Unit>>(updateResult)

        val getResult = contentService.getProjectContent(firstUserId, project.id)
        assertIs<Either.Right<ProjectContentService.ProjectDetails>>(getResult)
        assertEquals(markdown, getResult.value.content)
        assertEquals(firstUserId, getResult.value.ownerId)
    }

    @Test
    fun `updateProjectContent fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()
        val markdown = "# Non-existent Project"

        val result = contentService.updateProjectContent(firstUserId, nonExistentProjectId, markdown)
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (result as Either.Left).value)
    }

    @Test
    fun `getProjectContent fails for unauthorized user`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val result = contentService.getProjectContent(secondUserId, project.id)
        assertTrue(result is Either.Left)
        assertEquals(ProjectError.UserNotInProject, (result as Either.Left).value)
    }

    @Test
    fun `getProjectContent retrieves content for owner`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val markdown = "# Owner's Content"
        contentService.updateProjectContent(firstUserId, project.id, markdown)

        val getResult = contentService.getProjectContent(firstUserId, project.id)
        assertIs<Either.Right<ProjectContentService.ProjectDetails>>(getResult)
        assertEquals(markdown, getResult.value.content)
        assertEquals(firstUserId, getResult.value.ownerId)
    }

    @Test
    fun `getProjectContent retrieves content for collaborators`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
        }

        val markdown = "# Collaborator's Content"
        contentService.updateProjectContent(firstUserId, project.id, markdown)

        val getResult = contentService.getProjectContent(secondUserId, project.id)
        assertIs<Either.Right<ProjectContentService.ProjectDetails>>(getResult)
        assertEquals(markdown, getResult.value.content)
        assertEquals(firstUserId, getResult.value.ownerId)
    }


    @Test
    fun `upload and get image works for collaborators`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.EDITOR)
        }

        val imageName = "test-image"
        val extension = "png"
        val imageBytes = byteArrayOf(1, 2, 3, 4, 5)

        val uploadResult = contentService.uploadImage(
            secondUserId, project.id, imageName, extension, imageBytes
        )
        assertIs<Either.Right<Unit>>(uploadResult)

        val getResult = contentService.getImage(
            secondUserId, project.id, imageName, extension
        )
        assertIs<Either.Right<ByteArray>>(getResult)
        assertTrue(getResult.value.contentEquals(imageBytes))
    }

    @Test
    fun `uploadImage fails for unauthorized user`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val imageName = "not-allowed"
        val extension = "jpg"
        val imageBytes = byteArrayOf(6, 7, 8)

        val uploadResult = contentService.uploadImage(secondUserId, project.id, imageName, extension, imageBytes)
        assertTrue(uploadResult is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (uploadResult as Either.Left).value)
    }

    @Test
    fun `uploadImage fails if project does not exist`() {
        val nonExistentProjectId = UUID.randomUUID()
        val imageName = "non-existent"
        val extension = "jpg"
        val imageBytes = byteArrayOf(1, 2, 3)

        val uploadResult = contentService.uploadImage(firstUserId, nonExistentProjectId, imageName, extension, imageBytes)
        assertTrue(uploadResult is Either.Left)
        assertEquals(ProjectError.ProjectNotFound, (uploadResult as Either.Left).value)
    }

    @Test
    fun `uploadImage fails if user is a viewer`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        trxManager.run {
            repoCollaborators.addCollaborator(project.id, secondUserId, ProjectRole.VIEWER)
        }

        val imageName = "viewer-image"
        val extension = "jpg"
        val imageBytes = byteArrayOf(1, 2, 3)

        val uploadResult = contentService.uploadImage(secondUserId, project.id, imageName, extension, imageBytes)
        assertTrue(uploadResult is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (uploadResult as Either.Left).value)
    }

    @Test
    fun `deleteImage deletes existing image`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val imageName = "to-delete"
        val extension = "jpg"
        val imageBytes = byteArrayOf(7, 8, 9)

        val uploadResult = contentService.uploadImage(firstUserId, project.id, imageName, extension, imageBytes)
        assertIs<Either.Right<Unit>>(uploadResult)

        val deleteResult = contentService.deleteImage(firstUserId, project.id, imageName, extension)
        assertIs<Either.Right<Unit>>(deleteResult)

        val getResult = contentService.getImage(firstUserId, project.id, imageName, extension)
        assertTrue(getResult is Either.Left)
        assertEquals(ProjectError.ImageNotFound, (getResult as Either.Left).value)
    }

    @Test
    fun `deleteImage fails for unauthorized user`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val imageName = "not-allowed"
        val extension = "png"
        val imageBytes = byteArrayOf(10, 11, 12)

        val upload = contentService.uploadImage(firstUserId, project.id, imageName, extension, imageBytes)
        assertIs<Either.Right<Unit>>(upload)

        val deleteResult = contentService.deleteImage(secondUserId, project.id, imageName, extension)
        assertTrue(deleteResult is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (deleteResult as Either.Left).value)
    }

    @Test
    fun `getImage fails for unauthorized user`() {
        val project = projectInfoService.createProject(
            projectName, projectDescription, firstUserId, Visibility.PRIVATE
        ).let { (it as Either.Right).value }

        val imageName = "not-allowed"
        val extension = "png"
        val imageBytes = byteArrayOf(10, 11, 12)

        contentService.uploadImage(firstUserId, project.id, imageName, extension, imageBytes)

        val getResult = contentService.getImage(secondUserId, project.id, imageName, extension)
        assertTrue(getResult is Either.Left)
        assertEquals(ProjectError.ProjectNotAuthorized, (getResult as Either.Left).value)
    }
}
