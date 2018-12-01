package milu.kiriu2010.loader.v2

import java.io.Serializable

data class AsyncResultOK<T>(
    var dataOK: T? = null,
    var exception: Exception? = null
): Serializable

data class AsyncResultOKNG<T,S>(
    var dataOK: T? = null,
    var dataNG: S? = null,
    var exception: Exception? = null
): Serializable
