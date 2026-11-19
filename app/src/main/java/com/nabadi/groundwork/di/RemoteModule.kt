package com.nabadi.groundwork.di

import com.nabadi.groundwork.data.remote.RemoteTransport
import com.nabadi.groundwork.data.remote.fake.FakeGroundWorkServer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * The sync backend is simulated (see the README's MVP scope). Replacing it with
 * a real backend means binding a real HTTP [RemoteTransport] here.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteModule {

    @Binds
    abstract fun bindRemoteTransport(server: FakeGroundWorkServer): RemoteTransport
}
