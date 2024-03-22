package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.usecase.GetTokenUseCase
import ru.rozum.gitTest.domain.usecase.SignInUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val getTokenUseCase: GetTokenUseCase
) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions = _actions.asSharedFlow()

    private val _state = MutableSharedFlow<State>()
    val state = _state.asSharedFlow()

    private val _token = MutableSharedFlow<String>()
    val token = _token.asSharedFlow()

    init {
        viewModelScope.launch { _token.emit(getTokenUseCase.invoke()) }
    }

    fun onSignButtonPressed(token: String) {
        sign(token)
    }

    private fun sign(token: String) {
        viewModelScope.launch {
            try {
                beginToSign(token)
            } catch (token: IllegalArgumentException) {
                _state.emit(State.InvalidInput(token.message))
            } catch (exception: Exception) {
                _actions.emit(Action.ShowError(exception.message))
            } finally {
                _state.emit(State.Idle)
            }
        }
    }

    private suspend fun beginToSign(token: String) {
        _state.emit(State.Loading)
        val result = signInUseCase.invoke(token)
        _actions.emit(Action.RouteToMain(result))
    }

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class InvalidInput(val reason: String?) : State
    }

    sealed interface Action {
        data class ShowError(val message: String?) : Action
        data class RouteToMain(val user: UserInfo) : Action
    }
}