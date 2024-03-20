package ru.rozum.gitTest.exception

class NoTokenFoundException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)