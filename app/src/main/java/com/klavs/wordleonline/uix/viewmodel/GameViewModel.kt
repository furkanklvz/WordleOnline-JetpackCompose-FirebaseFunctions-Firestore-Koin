package com.klavs.wordleonline.uix.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.klavs.wordleonline.data.entity.Lobby
import com.klavs.wordleonline.data.entity.LobbyStatus
import com.klavs.wordleonline.data.repository.firestore.FirestoreRepository
import com.klavs.wordleonline.data.repository.functions.FunctionsRepository
import com.klavs.wordleonline.domain.model.GameResource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val firestoreRepo: FirestoreRepository,
    private val db: FirebaseFirestore,
    private val functionsRepo: FunctionsRepository
) :
    ViewModel() {

    private val _lobbyResultFlow = MutableStateFlow<GameResource<Lobby>>(GameResource.Loading())
    val lobbyResultFlow = _lobbyResultFlow.asStateFlow()

    private var lobbyJob: Job? = null
    private var timeCounterJob: Job? = null

    init {
        playRandom()
    }


    private fun playRandom() {
        lobbyJob = viewModelScope.launch(Dispatchers.Main) {
            firestoreRepo.playRandom().collect { gameResource ->
                _lobbyResultFlow.value = gameResource
                waitForAutoCanceling(
                    gameResource = gameResource
                )
            }
        }
    }

    private fun waitForAutoCanceling(gameResource: GameResource<Lobby>) {
        if (gameResource is GameResource.Success && gameResource.data!!.status == LobbyStatus.WAITING) {
            timeCounterJob?.cancel()
            timeCounterJob = viewModelScope.launch(Dispatchers.IO) {
                delay(19000)
                deleteLobby(gameResource.data.id!!)
                _lobbyResultFlow.value = GameResource.LobbyTimeOut()
            }
        } else {
            timeCounterJob?.cancel()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun closeLobby(lobbyId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("GameViewModel", "global scope started: $lobbyId")
            lobbyJob?.cancel()
            firestoreRepo.closeLobby(lobbyId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteLobby(lobbyId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("GameViewModel", "global scope started: $lobbyId")
            lobbyJob?.cancel()
            firestoreRepo.deleteLobby(lobbyId)
        }
    }

    fun guess(word: String) {
        Log.d("GameViewModel", "guess called: $word")
        viewModelScope.launch(Dispatchers.Main) {
            val isLastGuess = _lobbyResultFlow.value.data?.guesses?.size == 5
            functionsRepo.guess(
                word = word,
                lobbyId = _lobbyResultFlow.value.data!!.id!!,
                playerId = _lobbyResultFlow.value.data!!.playerID,
                isCorrect = word == _lobbyResultFlow.value.data!!.word,
                isLastGuess = isLastGuess
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        _lobbyResultFlow.value.data?.id?.let { lobbyId ->
            closeLobby(lobbyId)
        }
    }
}