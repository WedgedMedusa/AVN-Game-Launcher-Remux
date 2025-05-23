package org.skynetsoftware.avnlauncher.data.mapper

import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.data.GameEntity
import org.skynetsoftware.avnlauncher.data.GameFull
import org.skynetsoftware.avnlauncher.data.GamesFull
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.GamesList
import org.skynetsoftware.avnlauncher.domain.model.PLAY_STATE_NONE
import org.skynetsoftware.avnlauncher.domain.model.PlaySession
import org.skynetsoftware.avnlauncher.domain.model.PlayState

internal fun Game.toGameEntity() =
    GameEntity(
        title = title,
        description = description,
        developer = developer,
        imageUrl = imageUrl,
        customImageUrl = null,
        f95ZoneThreadId = f95ZoneThreadId,
        executablePaths = executablePaths,
        version = version,
        playTime = 0L,
        rating = rating,
        f95Rating = f95Rating,
        updateAvailable = updateAvailable,
        added = added,
        lastPlayed = 0L,
        hidden = hidden,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = playState.id,
        availableVersion = availableVersion,
        tags = tags,
        prefixes = prefixes,
        checkForUpdates = checkForUpdates,
        firstPlayed = 0L,
        notes = notes,
    )

@Suppress("LongMethod")
internal fun List<GamesFull>.toGames(): List<Game> {
    return groupBy { it.f95ZoneThreadId }
        .map { (f95ZoneThreadId, games) ->
            val first = games.first()
            val groupedByList = games.groupBy { it.listId }
            val playSessions = groupedByList.values.first().mapNotNull {
                if (it.playSessionStartTime == null || it.playSessionEndTime == null) {
                    null
                } else {
                    PlaySession(
                        gameId = first.f95ZoneThreadId,
                        startTime = it.playSessionStartTime,
                        endTime = it.playSessionEndTime,
                        version = it.version,
                    )
                }
            }
            val playSessionsFirstPlayedTime = playSessions.minOfOrNull { it.startTime } ?: 0L
            val firstPlayedTime = if (first.firstPlayed > 0) {
                first.firstPlayed
            } else {
                playSessionsFirstPlayedTime
            }
            val lists = groupedByList.mapNotNull {
                val firstInGroup = it.value.first()
                if (firstInGroup.listId == null || firstInGroup.listName == null) {
                    null
                } else {
                    GamesList(
                        id = firstInGroup.listId,
                        name = firstInGroup.listName,
                        description = firstInGroup.listDescription,
                    )
                }
            }
            val playState = if (first.playStateId == null || first.playStateLabel == null) {
                PLAY_STATE_NONE
            } else {
                PlayState(
                    id = first.playStateId,
                    label = first.playStateLabel,
                    description = first.playStateDescription,
                )
            }
            Game(
                title = first.title,
                description = first.description,
                developer = first.developer,
                imageUrl = first.imageUrl,
                f95ZoneThreadId = f95ZoneThreadId,
                executablePaths = first.executablePaths,
                version = first.version,
                totalPlayTime = first.playTime + playSessions.sumOf { it.endTime - it.startTime },
                rating = first.rating,
                f95Rating = first.f95Rating,
                updateAvailable = first.updateAvailable,
                added = first.added,
                lastPlayedTime = maxOf(first.lastPlayed, playSessions.maxOfOrNull { it.endTime } ?: 0L),
                hidden = first.hidden,
                releaseDate = first.releaseDate,
                firstReleaseDate = first.firstReleaseDate,
                playState = playState,
                availableVersion = first.availableVersion,
                tags = first.tags.toSet(),
                prefixes = first.prefixes.toSet(),
                checkForUpdates = first.checkForUpdates,
                firstPlayedTime = firstPlayedTime,
                notes = first.notes,
                playSessions = playSessions,
                lists = lists,
            )
        }
}

@Suppress("LongMethod")
internal fun List<GameFull>.toGame(): Game? {
    val first = firstOrNull() ?: return null
    val groupedByList = groupBy { it.listId }
    val playSessions = groupedByList.values.first().mapNotNull {
        if (it.playSessionStartTime == null || it.playSessionEndTime == null) {
            null
        } else {
            PlaySession(
                gameId = first.f95ZoneThreadId,
                startTime = it.playSessionStartTime,
                endTime = it.playSessionEndTime,
                version = it.version,
            )
        }
    }
    val playSessionsFirstPlayedTime = playSessions.minOfOrNull { it.startTime } ?: 0L
    val firstPlayedTime = if (first.firstPlayed > 0) {
        first.firstPlayed
    } else {
        playSessionsFirstPlayedTime
    }
    val lists = groupedByList.mapNotNull {
        val firstInGroup = it.value.first()
        if (firstInGroup.listId == null || firstInGroup.listName == null) {
            null
        } else {
            GamesList(
                id = firstInGroup.listId,
                name = firstInGroup.listName,
                description = firstInGroup.listDescription,
            )
        }
    }
    println(lists.joinToString { it.name })
    val playState = if (first.playStateId == null || first.playStateLabel == null) {
        PLAY_STATE_NONE
    } else {
        PlayState(
            id = first.playStateId,
            label = first.playStateLabel,
            description = first.playStateDescription,
        )
    }
    return Game(
        title = first.title,
        description = first.description,
        developer = first.developer,
        imageUrl = first.imageUrl,
        f95ZoneThreadId = first.f95ZoneThreadId,
        executablePaths = first.executablePaths,
        version = first.version,
        totalPlayTime = first.playTime + playSessions.sumOf { it.endTime - it.startTime },
        rating = first.rating,
        f95Rating = first.f95Rating,
        updateAvailable = first.updateAvailable,
        added = first.added,
        lastPlayedTime = maxOf(first.lastPlayed, playSessions.maxOfOrNull { it.endTime } ?: 0L),
        hidden = first.hidden,
        releaseDate = first.releaseDate,
        firstReleaseDate = first.firstReleaseDate,
        playState = playState,
        availableVersion = first.availableVersion,
        tags = first.tags.toSet(),
        prefixes = first.prefixes.toSet(),
        checkForUpdates = first.checkForUpdates,
        firstPlayedTime = firstPlayedTime,
        notes = first.notes,
        playSessions = playSessions,
        lists = lists,
    )
}

internal fun F95Game.toGame() =
    Game(
        title = title,
        description = description,
        developer = developer,
        imageUrl = imageUrl,
        f95ZoneThreadId = threadId,
        executablePaths = emptySet(),
        version = version,
        totalPlayTime = 0L,
        rating = 0,
        f95Rating = rating,
        updateAvailable = false,
        added = Clock.System.now().toEpochMilliseconds(),
        lastPlayedTime = 0L,
        hidden = false,
        releaseDate = releaseDate,
        firstReleaseDate = firstReleaseDate,
        playState = PLAY_STATE_NONE,
        availableVersion = null,
        tags = tags,
        prefixes = prefixes,
        checkForUpdates = true,
        firstPlayedTime = 0L,
        notes = null,
        playSessions = emptyList(),
        lists = emptyList(),
    )
