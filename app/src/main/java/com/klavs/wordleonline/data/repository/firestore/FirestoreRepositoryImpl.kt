package com.klavs.wordleonline.data.repository.firestore

import com.klavs.wordleonline.data.datasource.firestore.FirestoreDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FirestoreRepositoryImpl(private val ds: FirestoreDatasource) : FirestoreRepository {
    override suspend fun playRandom() = ds.playRandom().flowOn(Dispatchers.IO)
    override suspend fun closeLobby(id: String) = withContext(Dispatchers.IO) { ds.closeLobby(id) }
    override suspend fun deleteLobby(id: String) = withContext(Dispatchers.IO) { ds.deleteLobby(id) }

    override suspend fun guess(word: String, lobbyId: String, playerId: Int, isCorrect: Boolean) =
        withContext(Dispatchers.IO) { ds.guess(word, lobbyId, playerId, isCorrect) }
}