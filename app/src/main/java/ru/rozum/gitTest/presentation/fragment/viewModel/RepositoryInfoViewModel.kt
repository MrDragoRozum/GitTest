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
import ru.rozum.gitTest.domain.usecase.GetRepositoryReadmeUseCase
import ru.rozum.gitTest.domain.usecase.GetRepositoryUseCase
import ru.rozum.gitTest.presentation.fragment.DetailInfoFragmentArgs
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val getRepositoryReadmeUseCase: GetRepositoryReadmeUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = DetailInfoFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val exceptionReadme: CoroutineExceptionHandler
    private val exceptionRepo: CoroutineExceptionHandler
    private val _state: MutableStateFlow<State>
    private val repoLoaded get() = (_state.value as State.Loaded).githubRepo
    private var isReadmeFailed: Boolean

    init {
        _state = MutableStateFlow(State.Loading)
        isReadmeFailed = false

        exceptionReadme = CoroutineExceptionHandler { _, throwable ->
            checkThrowableOnError(throwable)
            when (throwable.message) {
                README_EMPTY -> _state.value = State.Loaded(ReadmeState.Empty, repoLoaded)
                else -> {
                    _state.value = State.Loaded(ReadmeState.Error, repoLoaded)
                    isReadmeFailed = true
                }
            }
        }
        exceptionRepo = CoroutineExceptionHandler { _, throwable ->
            checkThrowableOnError(throwable)
            _state.value = State.Error
        }

        setDataInGetRepository()
    }

    val state = _state.asStateFlow()

    fun retry() {
        setDataInGetRepository()
    }

    private fun setDataInGetRepository() {
        with(args) {
            getRepository(repo.id, userInfo.login, repo.name, repo.branch)
        }
    }

    private fun getRepository(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        if (isReadmeFailed) {
            beginLoadingReadme(ownerName, repositoryName, branchName, repoLoaded)
            return
        }
        viewModelScope.launch(exceptionRepo) {
            _state.value = State.Loading
            getRepositoryUseCase.invoke(repoId).also {
                beginLoadingReadme(ownerName, repositoryName, branchName, it)
            }

        }
    }

    private fun beginLoadingReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String,
        repo: RepoDetails
    ) {
        _state.value = State.Loaded(ReadmeState.Loading, repo)
        getReadme(ownerName, repositoryName, branchName)
    }

    private fun getReadme(ownerName: String, repositoryName: String, branchName: String) {
        viewModelScope.launch(exceptionReadme) {
            getRepositoryReadmeUseCase.invoke(ownerName, repositoryName, branchName).also {
                _state.value = State.Loaded(ReadmeState.Loaded(it), repoLoaded)
            }
        }
    }

    private fun checkThrowableOnError(throwable: Throwable) {
        if (throwable is Error) throw throwable
    }

    private companion object {
        const val README_EMPTY = "404"
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val readmeState: ReadmeState,
            val githubRepo: RepoDetails
        ) : State
    }

    sealed interface ReadmeState {
        data object Loading : ReadmeState, State
        data object Empty : ReadmeState
        data object Error : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}