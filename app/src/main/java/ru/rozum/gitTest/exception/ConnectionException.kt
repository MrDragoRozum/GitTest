package ru.rozum.gitTest.exception

class ConnectionException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)