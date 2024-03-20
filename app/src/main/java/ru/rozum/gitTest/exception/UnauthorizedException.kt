package ru.rozum.gitTest.exception

class UnauthorizedException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)