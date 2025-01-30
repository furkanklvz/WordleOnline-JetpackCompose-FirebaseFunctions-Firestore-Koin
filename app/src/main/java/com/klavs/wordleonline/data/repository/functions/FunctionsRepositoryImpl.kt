package com.klavs.wordleonline.data.repository.functions

import com.klavs.wordleonline.data.datasource.functions.FunctionsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FunctionsRepositoryImpl(private val ds: FunctionsDatasource) : FunctionsRepository{
    override suspend fun guess(
        lobbyId: String,
        word: String,
        playerId: Int,
        isCorrect: Boolean,
        isLastGuess: Boolean
    ) =
        withContext(Dispatchers.IO){ds.guess(lobbyId, word, playerId, isCorrect, isLastGuess)}
}