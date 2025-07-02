package pt.isel.markdown2slides

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import org.springframework.stereotype.Service
import kotlinx.coroutines.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*

data class CollabUpdate(
    val clientID: String,
    @JsonDeserialize(contentUsing = ChangeJsonDeserializer::class)
    @JsonSerialize(contentUsing = ChangeJsonSerializer::class)
    val changes: List<ChangeJSON>,
    val cursor: CursorInfo
)

data class CollabUpdateWithoutCursor(
    val clientID: String,
    @JsonDeserialize(contentUsing = ChangeJsonDeserializer::class)
    @JsonSerialize(contentUsing = ChangeJsonSerializer::class)
    val changes: List<ChangeJSON>,
)

fun CollabUpdate.getLength(): Int {
    var length = 0
    this.changes.forEach {
        length += when (it) {
            is Retain -> it.length
            is Delete -> it.length
            is Replace -> it.length
        }
    }
    return length
}
data class PushRequest(val clientID: Int, val updates: List<ChangeJSON>, val cursor: CursorInfo)
data class InitResponse(val version: Int, val doc: String)
data class PushRequestBody(val updates: List<CollabUpdate>)

const val IDLE_SECONDS_TO_EVICT_FROM_MEMORY = 20L

@Service
class AuthorityService(private val projectContentService: ProjectContentService) {

    private val maxIdleTime = TimeUnit.SECONDS.toMillis(IDLE_SECONDS_TO_EVICT_FROM_MEMORY)
    private val logger = LoggerFactory.getLogger(AuthorityService::class.java)

    data class ProjectState(
        var doc: String = "Start document",
        val ownerId: UUID,
        val updates: MutableList<CollabUpdate> = mutableListOf(),
        var updatedSinceLastPersistence: Boolean = false,
        val pending: MutableList<CompletableDeferred<List<CollabUpdate>>> = mutableListOf(),
        val cursors: MutableMap<String, CursorInfo> = mutableMapOf(),
        var lastAccessed: Long = System.currentTimeMillis() // Add this line
    )

    private val projects = ConcurrentHashMap<String, ProjectState>()

    private val objectMapper = jacksonObjectMapper()

    @Scheduled(fixedDelay = 10_000)
    fun flushAllProjectsToDisk() {
        logger.info("Flushing all projects to disk")
        val now = System.currentTimeMillis()

        projects.forEach { (projectId, state) ->
            synchronized(state) {
                if (state.updatedSinceLastPersistence){
                    try {
                        projectContentService.updateProjectContent(state.ownerId, UUID.fromString(projectId), state.doc)
                        state.updatedSinceLastPersistence = false
                    } catch (e: Exception) {
                        logger.error("Failed to persist project $projectId", e)
                        return@forEach
                    }
                }/*
                if (now - state.lastAccessed >  maxIdleTime) {
                    logger.info("Evicting project $projectId from memory due to inactivity")
                    projects.remove(projectId)
                    logger.info("Project $projectId evicted from memory:" + projects.size)

                }*/
            }
        }
    }

    fun getOrCreateProject(projectId: UUID, userId:UUID): ProjectState {
        return projects.computeIfAbsent(projectId.toString()) {
            val content = projectContentService.getProjectContent(userId, projectId)
            when(content) {
                is Success -> ProjectState(doc = content.value.content, ownerId = content.value.ownerId)
                is Failure -> throw IllegalStateException("Project not found or user not authorized")
            }
        }
    }

    suspend fun pullUpdates(projectId: UUID, userId:UUID, version: Int): List<CollabUpdate> {
        val project = getOrCreateProject(projectId, userId)
        return synchronized(project) {
            if (version < project.updates.size) {
                return project.updates.subList(version, project.updates.size)
            } else {
                val deferred = CompletableDeferred<List<CollabUpdate>>()
                project.pending.add(deferred)
                deferred
            }
        }.let { deferred ->
            // Timeout after 25 seconds if no updates arrive
            withTimeoutOrNull(25_000) {
                deferred.await()
            } ?: synchronized(project) {
                // Remove from pending if it timed out and wasn't completed
                project.pending.remove(deferred)
                emptyList<CollabUpdate>()
            } }
    }

    fun getDocument(projectId: UUID, userId: UUID): InitResponse {
        val project = getOrCreateProject(projectId, userId)
        return InitResponse(
            version = project.updates.size,
            doc = project.doc
        )
    }

    fun pushUpdates(projectId: UUID, userId:UUID, version: Int, incoming: List<CollabUpdate>): Boolean {
        if(incoming.isEmpty()) {
            logger.warn("Received empty update list for project $projectId from user $userId")
            return false
        }
        val project = getOrCreateProject(projectId, userId)
        logger.info("SIZE IS " + incoming.size)
        incoming.forEach { update ->
            logger.info("Received update from ${update.clientID}: ${update.changes}")
        }

        synchronized(project) {
            // If outdated version, reject updates
            if (version != project.updates.size) {
                logger.warn("Version mismatch: expected ${project.updates.size}, got $version")
                return false
            }
            val rebased =incoming
            logger.info("Active cursors: ${project.cursors.size}:")
            project.cursors.forEach { (clientId, cursor) ->
                logger.info("Cursor for $clientId: from ${cursor.from}, to ${cursor.to}")
            }
            // Update cursors with update cursor info
            project.cursors[userId.toString()] = rebased.first().cursor
            project.updates.addAll(rebased)
            for (update in rebased) {
                if(update.getLength() != project.doc.length) {
                    logger.warn("Update length mismatch: expected ${project.doc.length}, got ${update.getLength()}")
                    return false
                }
                update.changes.forEach { change ->
                    when(change){
                        is Retain -> logger.info("Retaining ${change.length} characters")
                        is Delete -> logger.info("Deleting ${change.length} characters")
                        is Replace -> logger.info("Replacing ${change.length} characters with ${change.insert.joinToString("\n")} with length ${change.insert.joinToString("\n").length}")
                    }
                }

                var from = 0
                for( change in update.changes) {
                    when (change) {
                        is Retain -> from += change.length
                        is Delete -> {
                            project.doc = project.doc.replaceRange(from, from + change.length, "")
                            from += change.length
                        }
                        is Replace -> {
                            project.doc = project.doc.replaceRange(from, from + change.length, change.insert.joinToString("\n"))
                        }
                    }
                }
                project.updatedSinceLastPersistence = true
            }
            // Notify pending pulls
            val iters = project.pending.toList()
            project.pending.clear()
            for (pending in iters) {
                pending.complete(rebased)
            }
        }
        return true
    }

    fun updateCursor(projectId: UUID, userId: UUID, cursorInfo: CursorInfo): List<OtherCursor> {
        val project = getOrCreateProject(projectId, userId)
        synchronized(project) {
            project.lastAccessed = System.currentTimeMillis()
            project.cursors[userId.toString()] = cursorInfo
            return project.cursors
                .filterKeys { it != userId.toString() }
                .map { OtherCursor(it.key, it.value) }
        }
    }

    private fun rebaseUpdates(incoming: List<CollabUpdate>, updatesDoneSince: List<CollabUpdate>): List<CollabUpdate> {
        if(updatesDoneSince.isEmpty() || incoming.isEmpty()) return incoming
        var incomingNew = incoming
        var skip = 0
        var changes: List<ChangeJSON>? = null
        for (update in updatesDoneSince) {
            val other = if(skip < incoming.size)  incoming[skip] else null
            if (other != null && other.clientID == update.clientID) {
                if (changes != null) changes = changes.mapDesc(other.changes, true)
                skip++
            } else {
                changes = if(changes!=null) changes.composeDesc(update.changes) else update.changes
            }
        }

        if (skip != 0) incomingNew = incoming.subList(skip,incoming.size)
        return if(changes==null) incomingNew else incomingNew.map{update ->
            val updateChanges = update.changes.mappp(changes!!)
            changes = changes!!.mapDesc(update.changes, true)
            CollabUpdate(update.clientID, updateChanges, CursorInfo(update.cursor.from, update.cursor.to))
        }
    }

    // set desc
    private fun List<ChangeJSON>.mappp(other: List<ChangeJSON>, before: Boolean = false): List<ChangeJSON> {
        return if(other.isEmptyy()) this else mapSet(this, other, before, true) // set desc before true
    }

    // desc desc : desc
    private fun List<ChangeJSON>.mapDesc(other: List<ChangeJSON>, before: Boolean = false): List<ChangeJSON> {
        return if(other.isEmptyy()) this else mapSet(this, other, before) // desc desc before
    }

    private fun List<ChangeJSON>.composeDesc(other: List<ChangeJSON>): List<ChangeJSON> {
        return if(this.isEmptyy()) other else if(other.isEmptyy()) this else composeSets(this, other)
    }

    private fun composeSets(changeJSONS: List<ChangeJSON>, other: List<ChangeJSON>, mkSet: Boolean = false): List<ChangeJSON> {
        val insert: MutableList<String>? = if (mkSet) mutableListOf() else null
        var open = false
        while(true){

        }
        TODO("Not yet implemented")
    }

    private fun mapSet(changeJSONS: List<ChangeJSON>, other: List<ChangeJSON>, before: Boolean, mkSet: Boolean = false): List<ChangeJSON> {
        TODO("Not yet implemented")
    }

    private fun List<ChangeJSON>.isEmptyy():Boolean = this.isEmpty() || this.any { it is Delete || it is Replace }

    private fun getSections(update: CollabUpdate): Pair<List<Int>, List<Inserted>> {
        val sections = mutableListOf<Int>()
        val inserted = mutableListOf<Inserted>()
        var currentLength = 0
        update.changes.forEachIndexed { idx, change ->
            when (change) {
                is Retain -> {sections.add(change.length); sections.add(-1)}
                is Delete -> {sections.add(change.length); sections.add(0)}
                is Replace -> {
                    while (inserted.size < idx) inserted.add(Single(""))
                    val multiple = Multiple(change.insert.subList(1, change.insert.size))
                    inserted[idx] = multiple
                    sections.add(change.length)
                    sections.add(multiple.texts.size)
                    currentLength += change.length + change.insert.joinToString("").length
                }
            }
        }
        return Pair(sections, inserted)
    }

    private fun iterChanges(a: Pair<List<Int>, List<Inserted>>, lambda: (fromA:Int, toA: Int, fromB: Int, toB: Int, text: String) -> Unit) {
        val inserted = a.second
        val sections = a.first
        var posA = 0
        var posB = 0
        var i = 0
        while (i < sections.size){
            var len = sections[i++]
            var ins = sections[i++]
            if(ins<0){
                posA += len
                posB += len
            } else{
                var endA= posA
                var endB= posB
                var text = ""
                while(true){
                    endA += len
                    endB += ins
                    if(ins != 0) text += inserted[(i - 2).shr(1)]
                    if(i == sections.size || sections[i+1] < 0) break
                    len = sections[i++]
                    ins = sections[i++]
                }
                lambda(posA, endA, posB, endB, text)
                posA = endA
                posB = endB
            }
        }

    }
}

@RestController
@RequestMapping("/api/collab")
class CollabController(val authorityService: AuthorityService) {

    private val logger = LoggerFactory.getLogger(CollabController::class.java)

    @GetMapping("/{projectId}")
    fun getInitial(@PathVariable projectId: UUID, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Getting initial document for project $projectId")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")

        return ResponseEntity.ok(authorityService.getDocument(projectId, userId))
    }

    @GetMapping("/{projectId}/updates/{version}")
    suspend fun getUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Pulling updates for project $projectId since version $version")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val updates = authorityService.pullUpdates(projectId, userId, version)
        return ResponseEntity.ok(updates)
    }

    @PostMapping("/{projectId}/updates/{version}")
    fun postUpdates(@PathVariable projectId: UUID, @PathVariable version: Int, @RequestBody body: PushRequestBody, @AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Any> {
        logger.info("Pushing updates for project $projectId by user ${body.updates.firstOrNull()} with version $version")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val result = authorityService.pushUpdates(projectId, userId, version, body.updates)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{projectId}/cursor/{userId}")
    fun putCursor(
        @PathVariable projectId: UUID,
        @PathVariable userId: String,
        @RequestBody cursorInfo: CursorInfo,
        @AuthenticationPrincipal principal: OAuth2User
    ): ResponseEntity<Any> {
        logger.info("Updating cursor for user $userId in project $projectId to $cursorInfo")
        val userId = principal.retrieveId() ?:
            return ResponseEntity.badRequest().body("Invalid UUID format for userId")
        val others = authorityService.updateCursor(projectId, userId, cursorInfo)
        return ResponseEntity.ok(others)
    }
}

sealed class ChangeJSON

data class Retain(val length: Int) : ChangeJSON()
data class Delete(val length: Int) : ChangeJSON()
data class Replace(val length: Int, val insert: List<String>) : ChangeJSON()


sealed class Inserted

data class Single(val text: String) : Inserted()
data class Multiple(val texts: List<String>) : Inserted()

class ChangeJsonDeserializer : JsonDeserializer<ChangeJSON>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChangeJSON {
        return when {
            p.currentToken.isNumeric -> Retain(p.intValue)
            p.currentToken == JsonToken.START_ARRAY -> {
                val array = mutableListOf<Any>()
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    array.add(p.readValueAs(Any::class.java))
                }

                val first = array.firstOrNull()
                if (first is Int && array.size == 1) {
                    Delete(first)
                } else if (first is Int && array.size > 1) {
                    Replace(first, array.drop(1).map { it.toString() })
                } else {
                    throw IllegalArgumentException("Invalid change JSON array format")
                }
            }
            else -> throw IllegalArgumentException("Invalid change format")
        }
    }
}

class ChangeJsonSerializer : JsonSerializer<ChangeJSON>() {
    override fun serialize(
        value: ChangeJSON,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        when (value) {
            is Retain -> gen.writeNumber(value.length)

            is Delete -> {
                gen.writeStartArray()
                gen.writeNumber(value.length)
                gen.writeEndArray()
            }

            is Replace -> {
                gen.writeStartArray()
                gen.writeNumber(value.length)
                for (str in value.insert) {
                    gen.writeString(str)
                }
                gen.writeEndArray()
            }
        }
    }
}

data class CursorInfo(
    val from: Int,
    val to: Int
)

data class OtherCursor(
    val userId: String,
    val cursor: CursorInfo
)