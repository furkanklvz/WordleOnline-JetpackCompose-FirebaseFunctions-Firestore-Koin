package com.klavs.wordleonline.data.entity

import com.google.firebase.Timestamp

data class Lobby(
    val id: String? = null,
    val word: String? = null,
    val playerID: Int = 0,
    val guesses: List<String> = emptyList(),
    val status: LobbyStatus = LobbyStatus.WAITING,
    val winner: Int? = null,
    val turn: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
    )
