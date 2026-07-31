package com.nabadi.groundwork.domain.model

data class SyncFailure(
    val operation: SyncOperation,
    val message: String,
    val failedAt: Long?,
)
