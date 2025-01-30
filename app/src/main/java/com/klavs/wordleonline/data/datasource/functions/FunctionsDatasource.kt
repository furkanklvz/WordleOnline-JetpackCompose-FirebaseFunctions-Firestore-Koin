package com.klavs.wordleonline.data.datasource.functions

interface FunctionsDatasource {
    suspend fun guess(lobbyId: String, word: String, playerId: Int, isCorrect: Boolean, isLastGuess: Boolean)
}