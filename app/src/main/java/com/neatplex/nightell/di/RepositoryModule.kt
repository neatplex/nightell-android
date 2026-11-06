package com.neatplex.nightell.di

import com.neatplex.nightell.data.app.AndroidAppInfo
import com.neatplex.nightell.data.connectivity.NetworkConnectivityMonitor
import com.neatplex.nightell.data.local.prefs.SessionRepositoryImpl
import com.neatplex.nightell.data.repository.AppUpdateRepositoryImpl
import com.neatplex.nightell.data.repository.AuthRepositoryImpl
import com.neatplex.nightell.data.repository.CommentRepositoryImpl
import com.neatplex.nightell.data.repository.FileRepositoryImpl
import com.neatplex.nightell.data.repository.FollowRepositoryImpl
import com.neatplex.nightell.data.repository.LikeRepositoryImpl
import com.neatplex.nightell.data.repository.PostRepositoryImpl
import com.neatplex.nightell.data.repository.SavedPostRepositoryImpl
import com.neatplex.nightell.data.repository.UserRepositoryImpl
import com.neatplex.nightell.domain.repository.AppInfo
import com.neatplex.nightell.domain.repository.AppUpdateRepository
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.CommentRepository
import com.neatplex.nightell.domain.repository.ConnectivityMonitor
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.repository.SavedPostRepository
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds each domain interface to its data-layer implementation. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds @Singleton
    abstract fun bindLikeRepository(impl: LikeRepositoryImpl): LikeRepository

    @Binds @Singleton
    abstract fun bindCommentRepository(impl: CommentRepositoryImpl): CommentRepository

    @Binds @Singleton
    abstract fun bindFollowRepository(impl: FollowRepositoryImpl): FollowRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindFileRepository(impl: FileRepositoryImpl): FileRepository

    @Binds @Singleton
    abstract fun bindSavedPostRepository(impl: SavedPostRepositoryImpl): SavedPostRepository

    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    abstract fun bindConnectivityMonitor(impl: NetworkConnectivityMonitor): ConnectivityMonitor

    @Binds @Singleton
    abstract fun bindAppUpdateRepository(impl: AppUpdateRepositoryImpl): AppUpdateRepository

    @Binds
    abstract fun bindAppInfo(impl: AndroidAppInfo): AppInfo
}
