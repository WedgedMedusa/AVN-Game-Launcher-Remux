package org.skynetsoftware.avnlauncher.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skynetsoftware.avnlauncher.data.f95.F95Api
import org.skynetsoftware.avnlauncher.data.f95.model.F95Game
import org.skynetsoftware.avnlauncher.data.f95.model.F95Versions
import org.skynetsoftware.avnlauncher.domain.repository.F95Repository
import org.skynetsoftware.avnlauncher.domain.utils.Result
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class F95RepositoryImplTest : KoinTest {
    private val f95Api = mockk<F95Api>()
    private val fakef95Game = F95Game(
        threadId = 0,
        title = "",
        description = "",
        developer = "",
        imageUrl = "",
        version = "",
        rating = 0f,
        firstReleaseDate = 0L,
        releaseDate = 0L,
        tags = emptySet(),
        prefixes = emptySet(),
    )

    private val f95Repository by inject<F95Repository>()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(
                listOf(
                    module {
                        single<F95Api> { f95Api }
                        f95RepositoryKoinModule()
                    },
                ),
            )
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `getGame calls f95Api getGame`() =
        runTest {
            coEvery { f95Api.getGame(0) } returns Result.Ok(fakef95Game)
            f95Repository.getGame(0)
            coVerify { f95Api.getGame(0) }
            confirmVerified(f95Api)
        }

    @Test
    fun `getVersions calls f95Api getVersions`() =
        runTest {
            coEvery { f95Api.getVersions(any()) } returns Result.Ok(F95Versions("ok", emptyMap()))
            f95Repository.getVersions(emptyList())
            coVerify { f95Api.getVersions(any()) }
            confirmVerified(f95Api)
        }

    @Test
    fun `getGame returns Result#Ok on success`() =
        runTest {
            coEvery { f95Api.getGame(0) } returns Result.Ok(fakef95Game)
            val result = f95Repository.getGame(0)
            assertIs<Result.Ok<*>>(result)
        }

    @Test
    fun `getGame returns Result#Error on error`() =
        runTest {
            coEvery { f95Api.getGame(0) } returns Result.Error(RuntimeException())
            val result = f95Repository.getGame(0)
            assertIs<Result.Error<*>>(result)
        }

    @Test
    fun `getVersion returns Result#Ok on success`() =
        runTest {
            coEvery { f95Api.getVersions(any()) } returns Result.Ok(F95Versions("ok", emptyMap()))
            val result = f95Repository.getVersions(emptyList())
            assertIs<Result.Ok<*>>(result)
        }

    @Test
    fun `getVersions returns Result#Error on error`() =
        runTest {
            coEvery { f95Api.getVersions(any()) } returns Result.Error(RuntimeException())
            val result = f95Repository.getVersions(emptyList())
            assertIs<Result.Error<*>>(result)
        }
}
