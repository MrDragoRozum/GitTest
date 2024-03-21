package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.usecase.GetRepositoriesUseCase
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state
        .asStateFlow()
        .onEach { _state.emit(State.Loading) }

    fun getRepositories() {
        viewModelScope.launch {
            kotlin.runCatching {
                getRepositoriesUseCase.invoke().also {
                    if(it.isNotEmpty()) {
                        _state.emit(State.Loaded(it))
                    } else {
                        _state.emit(State.Empty)
                    }
                }
            }.onException {
                _state.emit(State.Error(it.message))
            }.onFailure {
                _state.emit(State.Error(it.message))
            }
        }
    }

    private inline fun <R, T : R> Result<T>.onException(
        vararg exceptions: KClass<out Throwable> = arrayOf(
            ConnectException::class,
            UnknownHostException::class,
            SocketTimeoutException::class
        ),
        transform: (exception: Throwable) -> T
    ) = recoverCatching { ex ->
        if (ex::class in exceptions) transform(ex)
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String?) : State
        data object Empty : State
    }
}