package ru.rozum.gitTest.exception

class InvalidTokenException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException() {
}