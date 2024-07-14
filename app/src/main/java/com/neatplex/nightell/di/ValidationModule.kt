package com.neatplex.nightell.di

import com.neatplex.nightell.utils.Validation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {

    @Provides
    fun provideValidation(): Validation {
        return Validation()
    }
}