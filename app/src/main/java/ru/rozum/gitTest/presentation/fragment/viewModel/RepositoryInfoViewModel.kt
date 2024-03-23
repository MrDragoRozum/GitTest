package ru.rozum.gitTest.presentation.fragment.viewModel

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
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val getRepositoryReadmeUseCase: GetRepositoryReadmeUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val stateReadme = MutableStateFlow<ReadmeState>(ReadmeState.Loading)

    private val exceptionReadme = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            "404" -> stateReadme.value = ReadmeState.Empty
            else -> stateReadme.value = ReadmeState.Error
        }
    }

    private val exceptionRepo = CoroutineExceptionHandler { _, _ -> _state.value = State.Error }

    fun getRepository(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        viewModelScope.launch {
            val jobReadme = viewModelScope.launch(exceptionReadme) {
                getRepositoryReadmeUseCase.invoke(ownerName, repositoryName, branchName).also {
                    stateReadme.value = ReadmeState.Loaded(it)
                }
            }

            viewModelScope.launch(exceptionRepo) {
                getRepositoryUseCase.invoke(repoId).also {
                    jobReadme.join()
                    _state.value = State.Loaded(it, stateReadme.value)
                }
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(
            val githubRepo: RepoDetails,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        data object Loading : ReadmeState
        data object Empty : ReadmeState
        data object Error : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}