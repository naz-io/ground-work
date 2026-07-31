package com.nabadi.groundwork.data.repository

internal inline fun <reified T : Enum<T>> String?.toPersistedEnumOrNull(): T? =
    enumValues<T>().firstOrNull { it.name == this }
