package com.klavs.wordleonline.domain.model

sealed class Resource<T>(val data: T? = null, val message: Int? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: Int) : Resource<T>(message = message)
    class Loading<T>() : Resource<T>()
    class Idle<T>() : Resource<T>()
}

sealed class GameResource<T>(val data: T? = null, val message: Int? = null) {
    class Success<T>(data: T) : GameResource<T>(data)
    class Error<T>(message: Int) : GameResource<T>(message = message)
    class Loading<T> : GameResource<T>()
    class LobbyTimeOut<T> : GameResource<T>()
}