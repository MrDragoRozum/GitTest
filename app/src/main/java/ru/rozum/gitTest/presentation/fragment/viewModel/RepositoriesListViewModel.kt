package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.usecase.GetRepositoriesUseCase
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val exception: CoroutineExceptionHandler

    init {
        exception = CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is ConnectException, is UnknownHostException, is SocketTimeoutException ->
                    _state.value = State.ConnectionError

                is Exception -> _state.value = State.SomethingError
                else -> throw throwable
            }
        }
        getRepositories()
    }

    fun getRepositories() {
        viewModelScope.launch(exception) {
            _state.value = State.Loading
            getRepositoriesUseCase.invoke().also {
                _state.value = if (it.isNotEmpty()) State.Loaded(it) else State.Empty
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data object ConnectionError : State
        data object SomethingError : State
        data object Empty : State
    }
}