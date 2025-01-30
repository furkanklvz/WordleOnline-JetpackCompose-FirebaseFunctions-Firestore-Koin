package com.klavs.wordleonline.data.datasource.firestore

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.klavs.wordleonline.R
import com.klavs.wordleonline.data.entity.Lobby
import com.klavs.wordleonline.data.entity.LobbyStatus
import com.klavs.wordleonline.domain.model.GameResource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDatasourceImpl(private val db: FirebaseFirestore) : FirestoreDatasource {
    override suspend fun playRandom(): Flow<GameResource<Lobby>> = callbackFlow {
        var lobbyListener: ListenerRegistration? = null
        try {
            trySend(GameResource.Loading())
            val findResult = db.collection("lobbies")
                .whereEqualTo("status", LobbyStatus.WAITING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(1)
                .get(Source.SERVER).await()
            if (findResult.isEmpty) {
                val createLobbyResult = db.collection("lobbies").add(
                    Lobby(
                        status = LobbyStatus.WAITING,
                        createdAt = Timestamp.now()
                    )
                ).await()
                lobbyListener = createLobbyResult.addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreDatasource", error.message.toString())
                        trySend(GameResource.Error(message = R.string.something_went_wrong))
                    } else {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            val lobby = documentSnapshot.toObject(Lobby::class.java)
                            if (lobby != null) {
                                trySend(GameResource.Success(lobby.copy(playerID = 1, id = createLobbyResult.id)))
                            } else {
                                Log.e("FirestoreDatasource", "data could not converted")
                                trySend(GameResource.Error(message = R.string.something_went_wrong))
                            }
                        } else {
                            Log.e("FirestoreDatasource", "lobby doesn't exist")
                            trySend(GameResource.Error(message = R.string.something_went_wrong))
                        }
                    }
                }
            } else {
                val lobbyRef = db.collection("lobbies").document(findResult.first().id)
                val getWordResult = db.collection("turkishWords6").document("words").get().await()
                val wordList: List<String> = getWordResult.get("wordList") as List<String>
                val randomWord = wordList.random()
                val data = hashMapOf<String, Any>(
                    "id" to findResult.first().id,
                    "status" to LobbyStatus.PLAYING,
                    "turn" to 1,
                    "word" to randomWord
                )
                lobbyRef.update(data).await()
                lobbyListener = lobbyRef.addSnapshotListener { docSnapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreDatasource", error.message.toString())
                        trySend(GameResource.Error(message = R.string.something_went_wrong))
                    } else {
                        if (docSnapshot != null && docSnapshot.exists()) {
                            val lobby = docSnapshot.toObject(Lobby::class.java)
                            if (lobby != null) {
                                trySend(GameResource.Success(lobby.copy(playerID = 2)))
                            } else {
                                Log.e("FirestoreDatasource", "data could not converted")
                                trySend(GameResource.Error(message = R.string.something_went_wrong))
                            }
                        } else {
                            Log.e("FirestoreDatasource", "lobby doesn't exist")
                            trySend(GameResource.Error(message = R.string.something_went_wrong))
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("FirestoreDatasource", e.message.toString())
            trySend(GameResource.Error(message = R.string.something_went_wrong))
        }
        awaitClose {
            lobbyListener?.remove()
        }
    }

    override suspend fun closeLobby(id: String) {
        try {
            db.collection("lobbies").document(id).update("status", LobbyStatus.ENDED).await()
            Log.d("FirestoreDatasource", "Lobby ended: $id")
        } catch (e: Exception) {
            Log.e("FirestoreDatasource", e.message.toString())
        }
    }

    override suspend fun deleteLobby(id: String) {
        try {
            val lobbyRef = db.collection("lobbies").document(id)
            lobbyRef.delete().await()
            Log.d("FirestoreDatasource", "Lobby deleted: $id")
        }catch (e:Exception){
            Log.e("FirestoreDatasource", "lobby cannot be deleted $e")
        }
    }

    override suspend fun guess(word: String, lobbyId: String, playerId: Int, isCorrect: Boolean) {
        try {
            val lobbyRef = db.collection("lobbies").document(lobbyId)
            lobbyRef.update("guesses", FieldValue.arrayUnion(word)).await()
            if (isCorrect){
                lobbyRef.update("winner", playerId).await()
            }
        }catch (e:Exception){
            Log.e("FirebaseDatasource", e.toString())
        }
    }
}