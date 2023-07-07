package kz.rza383.auaray.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.rza383.auaray.data.repository.MyRepositoryImpl
import kz.rza383.domain.repository.MyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(
       myRepositoryImpl: MyRepositoryImpl
    ): MyRepository
}