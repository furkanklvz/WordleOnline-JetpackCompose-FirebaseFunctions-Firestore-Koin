package com.klavs.wordleonline.data.repository.firestore

import com.klavs.wordleonline.data.entity.Lobby
import com.klavs.wordleonline.domain.model.GameResource
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    suspend fun playRandom(): Flow<GameResource<Lobby>>
    suspend fun closeLobby(id:String)
    suspend fun deleteLobby(id:String)
    suspend fun guess(word: String, lobbyId: String, playerId:Int, isCorrect: Boolean)
}