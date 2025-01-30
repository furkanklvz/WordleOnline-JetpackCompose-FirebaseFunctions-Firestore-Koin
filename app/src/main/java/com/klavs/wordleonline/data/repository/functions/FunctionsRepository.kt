package com.klavs.wordleonline.data.repository.functions

interface FunctionsRepository {
    suspend fun guess(lobbyId: String, word: String, playerId: Int, isCorrect: Boolean, isLastGuess: Boolean)
}