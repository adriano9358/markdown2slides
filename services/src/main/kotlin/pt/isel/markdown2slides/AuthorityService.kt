package pt.isel.markdown2slides

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlinx.coroutines.*
import pt.isel.markdown2slides.utils.*
import java.util.concurrent.TimeUnit


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
        var lastAccessed: Long = System.currentTimeMillis()
    )

    private val projects = ConcurrentHashMap<String, ProjectState>()

    //private val objectMapper = jacksonObjectMapper()

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

    fun getOrCreateProject(projectId: UUID, userId: UUID): Either<ProjectError, ProjectState> {
        return when (val result = projectContentService.getProjectContent(userId, projectId)) {
            is Success -> {
                val state = projects.computeIfAbsent(projectId.toString()) {
                    ProjectState(doc = result.value.content, ownerId = result.value.ownerId)
                }
                success(state)
            }
            is Failure -> result
        }
    }

    suspend fun pullUpdates(projectId: UUID, userId: UUID, version: Int): Either<ProjectError, List<CollabUpdate>> {
        return when (val result = getOrCreateProject(projectId, userId)) {
            is Failure -> result
            is Success -> {
                val project = result.value
                val deferred = synchronized(project) {
                    if (version < project.updates.size) {
                        return success(project.updates.subList(version, project.updates.size))
                    } else {
                        val d = CompletableDeferred<List<CollabUpdate>>()
                        project.pending.add(d)
                        d
                    }
                }

                val updates = withTimeoutOrNull(25_000) {
                    deferred.await()
                } ?: synchronized(project) {
                    project.pending.remove(deferred)
                    emptyList()
                }
                success(updates)
            }
        }
        /*val project = getOrCreateProject(projectId, userId)
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
            } }*/
    }


    fun getDocument(
        projectId: UUID,
        userId: UUID
    ): Either<ProjectError, InitResponse> {
        return when (val result = getOrCreateProject(projectId, userId)) {
            is Failure -> result
            is Success -> success(
                InitResponse(
                    version = result.value.updates.size,
                    doc = result.value.doc
                )
            )
        }
    }

    /*
    fun getDocument(projectId: UUID, userId: UUID): InitResponse {
        val project = getOrCreateProject(projectId, userId)
        return InitResponse(
            version = project.updates.size,
            doc = project.doc
        )
    }*/

    /*fun pushUpdates(projectId: UUID, userId: UUID, version: Int, incoming: List<CollabUpdate>): Boolean {
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
    }*/

    fun pushUpdates(
        projectId: UUID,
        userId: UUID,
        version: Int,
        incoming: List<CollabUpdate>
    ): Either<ProjectError, Unit> {
        if (incoming.isEmpty()) {
            logger.warn("Empty update list for project $projectId from user $userId")
            return failure(ProjectError.InvalidUpdates)
        }

        return when (val result = getOrCreateProject(projectId, userId)) {
            is Failure -> result
            is Success -> {
                val project = result.value
                synchronized(project) {
                    if (version != project.updates.size) {
                        logger.warn("Version mismatch for $projectId: expected ${project.updates.size}, got $version")
                        return failure(ProjectError.VersionMismatch)
                    }

                    val rebased = incoming

                    project.cursors[userId.toString()] = rebased.first().cursor
                    project.updates.addAll(rebased)

                    for (update in rebased) {
                        var from = 0
                        for (change in update.changes) {
                            when (change) {
                                is Retain -> from += change.length
                                is Delete -> {
                                    project.doc = project.doc.replaceRange(from, from + change.length, "")
                                    from += change.length
                                }
                                is Replace -> {
                                    project.doc = project.doc.replaceRange(
                                        from,
                                        from + change.length,
                                        change.insert.joinToString("\n")
                                    )
                                }
                            }
                        }
                        project.updatedSinceLastPersistence = true
                    }

                    // Notify pending
                    val iters = project.pending.toList()
                    project.pending.clear()
                    for (pending in iters) {
                        pending.complete(rebased)
                    }
                }

                success(Unit)
            }
        }
    }

    /*fun updateCursor(projectId: UUID, userId: UUID, cursorInfo: CursorInfo): List<OtherCursor> {
        val project = getOrCreateProject(projectId, userId)
        synchronized(project) {
            project.lastAccessed = System.currentTimeMillis()
            project.cursors[userId.toString()] = cursorInfo
            return project.cursors
                .filterKeys { it != userId.toString() }
                .map { OtherCursor(it.key, it.value) }
        }
    }*/

    fun updateCursor(
        projectId: UUID,
        userId: UUID,
        cursorInfo: CursorInfo
    ): Either<ProjectError, List<OtherCursor>> {
        return when (val result = getOrCreateProject(projectId, userId)) {
            is Failure -> result
            is Success -> {
                val project = result.value
                synchronized(project) {
                    project.lastAccessed = System.currentTimeMillis()
                    project.cursors[userId.toString()] = cursorInfo
                    val others = project.cursors
                        .filterKeys { it != userId.toString() }
                        .map { OtherCursor(it.key, it.value) }
                    success(others)
                }
            }
        }
    }

    /*private fun rebaseUpdates(incoming: List<CollabUpdate>, updatesDoneSince: List<CollabUpdate>): List<CollabUpdate> {
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

    }*/
}
