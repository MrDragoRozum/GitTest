package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentRepoBinding
import ru.rozum.gitTest.presentation.fragment.util.collect
import ru.rozum.gitTest.presentation.fragment.util.setVisibility
import ru.rozum.gitTest.presentation.fragment.viewModel.RepoViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.RepoViewModel.ReadmeState
import ru.rozum.gitTest.presentation.fragment.viewModel.RepoViewModel.State
import javax.inject.Inject

@AndroidEntryPoint
class RepoFragment : Fragment(R.layout.fragment_repo) {
    private val binding by viewBinding(FragmentRepoBinding::bind)
    private val viewModel by viewModels<RepoViewModel>()

    @Inject
    lateinit var markwon: Markwon

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installFragment()
    }

    private fun installFragment() {
        installState()
        installListeners()
    }

    private fun installState() {
        collect(viewLifecycleOwner, viewModel.state) { state ->
            binding.progressBarRepoDetails.visibility = setVisibility(
                state,
                State.Loading
            )
            binding.includeConnectionErrorRepoDetails.root.visibility = setVisibility(
                state,
                State.Error
            )
            changeStateRepoIfLoaded(state)
        }
    }

    private fun changeStateRepoIfLoaded(state: State) {
        if (state is State.Loaded) {
            binding.toolbarRepoDetails.title = state.repo.name
            binding.detailsInfoRepoView.repoDetails = state.repo
            binding.detailsInfoRepoView.visibility = View.VISIBLE
            binding.textViewReadme.visibility = View.VISIBLE
            installReadmeState(state.readmeState)
        }
    }

    private fun installReadmeState(state: ReadmeState) {
        if (state is ReadmeState.Loaded) markwon.setMarkdown(binding.textViewReadme, state.markdown)
        if (state is ReadmeState.Empty) binding.textViewReadme.text = getString(R.string.no_readme)

        binding.progressBarReadme.visibility = setVisibility(
            state,
            ReadmeState.Loading
        )

        binding.includeConnectionErrorRepoDetails.root.visibility = setVisibility(
            state,
            ReadmeState.Error
        )
    }

    private fun installListeners() {
        binding.toolbarRepoDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.includeConnectionErrorRepoDetails.buttonRetryConnectionError.setOnClickListener {
            viewModel.getRepository()
        }
    }

}