package com.nabadi.groundwork.core.common

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class SuspendRunCatchingTest {

    @Test
    fun `returns the block's value as a success`() {
        val result = suspendRunCatching { "saved" }

        assertEquals(Result.success("saved"), result)
    }

    @Test
    fun `turns an exception into a failure`() {
        val error = IllegalStateException("Save failed")

        val result = suspendRunCatching { throw error }

        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `rethrows cancellation instead of reporting it as a failure`() {
        val cancellation = CancellationException("Scope cancelled")

        val thrown = assertThrows(CancellationException::class.java) {
            suspendRunCatching { throw cancellation }
        }

        assertSame(cancellation, thrown)
    }

    @Test
    fun `does not catch errors`() {
        assertThrows(StackOverflowError::class.java) {
            suspendRunCatching { throw StackOverflowError() }
        }
    }
}
