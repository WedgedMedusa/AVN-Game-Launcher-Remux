package org.skynetsoftware.avnlauncher.domain.model

data class Game(
    val title: String,
    val description: String,
    val developer: String,
    val imageUrl: String,
    val f95ZoneThreadId: Int,
    val executablePaths: Set<String>,
    val version: String,
    val rating: Int,
    val f95Rating: Float,
    val updateAvailable: Boolean,
    val added: Long,
    val hidden: Boolean,
    val releaseDate: Long,
    val firstReleaseDate: Long,
    val playState: PlayState,
    val availableVersion: String?,
    val tags: Set<String>,
    val prefixes: Set<String>,
    val checkForUpdates: Boolean,
    val notes: String?,
    val playSessions: List<PlaySession>,
    val lists: List<GamesList>,
    val totalPlayTime: Long,
    val firstPlayedTime: Long,
    val lastPlayedTime: Long,
)
