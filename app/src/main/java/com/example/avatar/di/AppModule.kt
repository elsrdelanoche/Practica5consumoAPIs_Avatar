package com.example.avatar.di

import android.content.Context
import androidx.room.Room
import com.example.avatar.data.local.AvatarDatabase
import com.example.avatar.data.local.CharacterDao
import com.example.avatar.data.remote.LastAirbenderApi
import com.example.avatar.data.repository.CharacterRepositoryImpl
import com.example.avatar.domain.repository.CharacterRepository
import com.example.avatar.presentation.login.google_auth.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAvatarDatabase(@ApplicationContext context: Context): AvatarDatabase {
        return Room.databaseBuilder(
            context,
            AvatarDatabase::class.java,
            "avatar_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Singleton
    @Provides
    fun provideCharacterDao(db: AvatarDatabase): CharacterDao = db.characterDao()

    @Singleton
    @Provides
    fun provideLastAirbenderApi(): LastAirbenderApi {
        return Retrofit.Builder()
            .baseUrl("https://last-airbender-api.fly.dev")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LastAirbenderApi::class.java)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideCharacterRepository(
        api: LastAirbenderApi,
        dao: CharacterDao,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): CharacterRepository {
        return CharacterRepositoryImpl(api, dao, auth, firestore)
    }

    @Singleton
    @Provides
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context
    ): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
}
