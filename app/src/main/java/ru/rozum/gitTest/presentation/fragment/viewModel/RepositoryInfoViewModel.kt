package ru.rozum.gitTest.presentation.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.usecase.GetRepositoryReadmeUseCase
import ru.rozum.gitTest.domain.usecase.GetRepositoryUseCase
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val getRepositoryReadmeUseCase: GetRepositoryReadmeUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun getRepository(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        viewModelScope.launch {
            kotlin.runCatching {
                getRepositoryUseCase.invoke(repoId).also {
                    _state.value = State.Loaded(
                        it, getReadme(ownerName, repositoryName, branchName)
                    )
                }
            }.onException {
                _state.value = State.Error(it.message)
            }
        }
    }

    private suspend fun getReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): ReadmeState {
        return try {
            ReadmeState.Loading
            val markDown = getRepositoryReadmeUseCase.invoke(ownerName, repositoryName, branchName)
            if (markDown.isBlank()) return ReadmeState.Empty
            ReadmeState.Loaded(markDown)
        } catch (e: Exception) {
            ReadmeState.Error(e.message)
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
        data class Error(val error: String?) : State
        data class Loaded(
            val githubRepo: RepoDetails,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        data object Loading : ReadmeState
        data object Empty : ReadmeState
        data class Error(val error: String?) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}