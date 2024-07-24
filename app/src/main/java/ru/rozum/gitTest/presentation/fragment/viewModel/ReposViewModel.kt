package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.repository.GithubRepoRepository
import ru.rozum.gitTest.exception.ClientConnectionException
import ru.rozum.gitTest.exception.ServerConnectionException
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val githubRepoRepository: GithubRepoRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val exception = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is ClientConnectionException -> _state.value = State.ConnectionError
            is ServerConnectionException -> _state.value = State.SomethingError
        }
    }

    init {
        getRepositories()
    }

    fun getRepositories() {
        viewModelScope.launch(exception) {
            _state.value = State.Loading
            githubRepoRepository.getRepositories().also { repos ->
                _state.value = if (repos.isNotEmpty()) State.Loaded(repos) else State.Empty
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