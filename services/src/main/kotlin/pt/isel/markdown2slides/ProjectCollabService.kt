package pt.isel.markdown2slides

import jakarta.inject.Named
import java.io.File
import java.util.concurrent.ConcurrentHashMap
/*
@Named
class ProjectCollabService {
    private val projectBuffers = ConcurrentHashMap<String, ProjectBuffer>()

    fun getBuffer(projectId: String): ProjectBuffer {
        return projectBuffers.computeIfAbsent(projectId) {
            val content = loadFromDisk(projectId)
            ProjectBuffer(projectId, content)
        }
    }

    fun updateContent(projectId: String, newContent: String) {
        getBuffer(projectId).updateContent(newContent)
    }

    fun getCurrentContent(projectId: String): String {
        return getBuffer(projectId).content
    }

    fun saveToDisk(projectId: String) {
        val content = getCurrentContent(projectId)
        writeToDisk(projectId, content)
    }

    private fun loadFromDisk(projectId: String): String {
        val file = File("projects/$projectId.md")
        return if (file.exists()) file.readText() else ""
    }

    private fun writeToDisk(projectId: String, content: String) {
        val file = File("projects/$projectId.md")
        file.writeText(content)
    }
}
*/
