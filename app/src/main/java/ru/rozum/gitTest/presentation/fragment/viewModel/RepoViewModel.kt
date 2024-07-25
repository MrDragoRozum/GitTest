package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.repository.GithubRepoRepository
import ru.rozum.gitTest.exception.ClientConnectionException
import ru.rozum.gitTest.exception.NoReadmeException
import ru.rozum.gitTest.exception.ServerConnectionException
import ru.rozum.gitTest.presentation.fragment.RepoFragmentArgs
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val githubRepoRepository: GithubRepoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = RepoFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val exceptionReadme = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is NoReadmeException -> _state.value = State.Loaded(ReadmeState.Empty, repoLoaded)
            is ClientConnectionException, is ServerConnectionException -> {
                _state.value = State.Loaded(ReadmeState.Error, repoLoaded)
                isReadmeFailed = true
            }
        }
    }

    private val exceptionRepo = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is ClientConnectionException, is ServerConnectionException -> _state.value = State.Error
        }
    }

    private val repoLoaded get() = (_state.value as State.Loaded).repo
    private var isReadmeFailed: Boolean = false

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state = _state.asStateFlow()

    init {
        loadRepository()
    }

    fun getRepository() = loadRepository()

    private fun loadRepository() {
        viewModelScope.launch(exceptionRepo) {
            if (isReadmeFailed) {
                _state.value = State.Loaded(ReadmeState.Loading, repoLoaded)
                loadReadme()
                return@launch
            }

            _state.value = State.Loading
            githubRepoRepository.getRepository(args.repo.id).also { repo ->
                _state.value = State.Loaded(ReadmeState.Loading, repo)
                loadReadme()
            }
        }
    }

    private fun loadReadme() {
        viewModelScope.launch(exceptionReadme) {
            githubRepoRepository.getRepositoryReadme(
                args.user.login,
                args.repo.name,
                args.repo.branch
            ).also { readme ->
                _state.value = State.Loaded(ReadmeState.Loaded(readme), repoLoaded)
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val readmeState: ReadmeState,
            val repo: RepoDetails
        ) : State
    }

    sealed interface ReadmeState {
        data object Loading : ReadmeState, State
        data object Empty : ReadmeState
        data object Error : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}