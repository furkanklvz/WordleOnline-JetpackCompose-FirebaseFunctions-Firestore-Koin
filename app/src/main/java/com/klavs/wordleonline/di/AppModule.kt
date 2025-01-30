package com.klavs.wordleonline.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.klavs.wordleonline.data.datasource.firestore.FirestoreDatasource
import com.klavs.wordleonline.data.datasource.firestore.FirestoreDatasourceImpl
import com.klavs.wordleonline.data.datasource.functions.FunctionsDatasource
import com.klavs.wordleonline.data.datasource.functions.FunctionsDatasourceImpl
import com.klavs.wordleonline.data.repository.functions.FunctionsRepository
import com.klavs.wordleonline.data.repository.functions.FunctionsRepositoryImpl
import com.klavs.wordleonline.data.repository.firestore.FirestoreRepository
import com.klavs.wordleonline.data.repository.firestore.FirestoreRepositoryImpl
import com.klavs.wordleonline.uix.viewmodel.GameViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    single { Firebase.functions }
    singleOf(::FirestoreDatasourceImpl) { bind<FirestoreDatasource>() }
    singleOf(::FunctionsDatasourceImpl) { bind<FunctionsDatasource>() }
    singleOf(::FirestoreRepositoryImpl) { bind<FirestoreRepository>() }
    singleOf(::FunctionsRepositoryImpl) { bind<FunctionsRepository>() }
    viewModelOf(::GameViewModel)
}