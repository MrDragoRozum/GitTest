package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.usecase.SignInUseCase
import ru.rozum.gitTest.exception.InvalidTokenException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val signInUseCase: SignInUseCase) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions = _actions.asSharedFlow()

    private val _state = MutableSharedFlow<State>()
    val state = _state.asSharedFlow()

    private val _user = MutableSharedFlow<UserInfo>()
    val user = _user.asSharedFlow()
    fun onSignButtonPressed(token: String) {
        viewModelScope.launch {
            try {
                _state.emit(State.Loading)
                _user.emit(signInUseCase.invoke(token))
                _actions.emit(Action.RouteToMain)
            } catch (token: InvalidTokenException) {
                _state.emit(State.InvalidInput(token.message))
            } catch (exception: Exception) {
                _actions.emit(Action.ShowError(exception.message))
            } finally {
                _state.emit(State.Idle)
            }
        }
    }

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String?) : Action
        data object RouteToMain : Action
    }
}