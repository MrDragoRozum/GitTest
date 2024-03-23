package ru.rozum.gitTest.exception

class ConcreteCodeException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)