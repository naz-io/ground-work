package com.nabadi.groundwork.data.local

// Hides soft-deleted rows: pending deletes and deletes that failed and await retry.
// The IS NULL check keeps FAILED rows without a recorded operation visible, since
// comparing NULL with != never matches.
internal const val NOT_DELETED =
    "(sync_state != 'PENDING_DELETE' " +
        "AND (sync_state != 'FAILED' OR sync_failedOperation IS NULL " +
        "OR sync_failedOperation != 'DELETE'))"
