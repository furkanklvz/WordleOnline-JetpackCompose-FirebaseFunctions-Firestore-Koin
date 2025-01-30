package com.klavs.wordleonline.data.datasource.functions

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class FunctionsDatasourceImpl(private val functions: FirebaseFunctions, private val db: FirebaseFirestore) : FunctionsDatasource{
    override suspend fun guess(
        lobbyId: String,
        word: String,
        playerId: Int,
        isCorrect: Boolean,
        isLastGuess: Boolean
    ) {
        try {
            val data = hashMapOf(
                "lobbyId" to lobbyId,
                "word" to word,
                "playerId" to playerId,
                "isCorrect" to isCorrect,
                "isLastGuess" to isLastGuess
            )
            val guessFunction = functions.getHttpsCallable("getGuess")
            guessFunction.call(data).await()
        }catch (e:Exception){
            Log.e("FunctionsDatasource", e.toString())
        }
    }
}