package com.app.khajaghar.data.local

import retrofit2.HttpException

data class Resource<out T>(
        val status: Status, val data: T?, val message: String? = null, val error: Exception? = null
) {
    enum class Status {
        SUCCESS,
        OFFLINE_ERROR,
        ERROR,
        EMPTY,
        LOADING
    }

    companion object {
        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> empty(): Resource<T> {
            return Resource(Status.EMPTY, null)
        }

        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Exception? = null, message: String? = null, data: T? = null): Resource<T> {
            if (error is HttpException) {

                return Resource(Status.ERROR, data, message = ErrorUtils.parseError(error), error = error)

            } else {
                return Resource(Status.ERROR, data, message, error = error)

            }
        }


        fun <T> offlineError(message: String? = null, data: T? = null): Resource<T> {
            return Resource(Status.OFFLINE_ERROR, data, message)
        }
    }
}
