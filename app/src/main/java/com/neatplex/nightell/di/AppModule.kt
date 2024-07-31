package com.neatplex.nightell.di

import com.neatplex.nightell.utils.ITokenManager
import com.neatplex.nightell.utils.IValidation
import com.neatplex.nightell.utils.TokenManager
import com.neatplex.nightell.utils.Validation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract  class AppModule {
    @Binds
    abstract fun bindTokenManager(tokenManager: TokenManager): ITokenManager

    @Binds
    abstract fun bindValidation(validation: Validation): IValidation
}