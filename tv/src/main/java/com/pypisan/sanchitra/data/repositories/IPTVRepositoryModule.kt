package com.pypisan.sanchitra.data.repositories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IPTVRepositoryModule {

    @Binds
    abstract fun bindIPTVRepository(
        impl: IPTVRepositoryImpl
    ): IPTVRepository
}