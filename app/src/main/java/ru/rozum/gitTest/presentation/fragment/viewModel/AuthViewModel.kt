package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.R
import ru.rozum.gitTest.domain.entity.User
import ru.rozum.gitTest.domain.repository.UserRepository
import ru.rozum.gitTest.exception.ClientConnectionException
import ru.rozum.gitTest.exception.ServerConnectionException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions = _actions.asSharedFlow()

    private val _state = MutableSharedFlow<State>()
    val state = _state.asSharedFlow()

    private val _token = MutableSharedFlow<String>(replay = 1)
    val token = _token.asSharedFlow()

    private val exception = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            when (throwable) {
                is ClientConnectionException,
                is ServerConnectionException -> _actions.emit(
                    Action.ShowError(throwable.message)
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            _token.emit(userRepository.getToken())
        }
    }

    fun signIn(token: String) {
        viewModelScope.launch(exception) {
            if (isInvalidToken(token)) {
                _state.emit(State.InvalidInput(R.string.invalid_token))
                return@launch
            }

            _state.emit(State.Loading)
            userRepository.signIn(token).also { _actions.emit(Action.RouteToMain(it)) }
        }
    }

    private fun isInvalidToken(token: String) = !Regex("^ghp_[a-zA-Z0-9]{36}+\$").matches(token)

    sealed interface State {
        data object Loading : State
        data class InvalidInput(val reasonId: Int) : State
    }

    sealed interface Action {
        data class ShowError(val message: String?) : Action
        data class RouteToMain(val user: User) : Action
    }
}