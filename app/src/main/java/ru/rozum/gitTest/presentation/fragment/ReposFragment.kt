package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentReposBinding
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.presentation.fragment.adapter.RepositoryListAdapter
import ru.rozum.gitTest.presentation.fragment.util.collect
import ru.rozum.gitTest.presentation.fragment.util.setVisibility
import ru.rozum.gitTest.presentation.fragment.viewModel.ReposViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.ReposViewModel.State
import javax.inject.Inject

@AndroidEntryPoint
class ReposFragment : Fragment(R.layout.fragment_repos) {
    private val binding by viewBinding(FragmentReposBinding::bind)
    private val viewModel by viewModels<ReposViewModel>()
    private val args by navArgs<ReposFragmentArgs>()

    @Inject
    lateinit var adapter: RepositoryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installFragment()
    }

    private fun installFragment() {
        installAdapter()
        installState()
        installListeners()
    }

    private fun installAdapter() {
        binding.recyclerViewRepos.adapter = adapter
        binding.recyclerViewRepos.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun installState() {
        collect(viewLifecycleOwner, viewModel.state) { state ->
            binding.progressBarRepoDetails.visibility = setVisibility(
                state,
                State.Loading
            )
            binding.includeEmptyRepoList.root.visibility = setVisibility(
                state,
                State.Empty
            )
            binding.includeSomethingErrorRepoList.root.visibility = setVisibility(
                state,
                State.SomethingError
            )
            binding.includeConnectionErrorRepoList.root.visibility = setVisibility(
                state,
                State.ConnectionError
            )

            changeStateReposIfListLoaded(state)
        }
    }

    private fun changeStateReposIfListLoaded(state: State) {
        if (state is State.Loaded) adapter.submitList(state.repos)
    }

    private fun installListeners() {
        listOf(
            binding.includeEmptyRepoList.buttonRefreshEmpty,
            binding.includeSomethingErrorRepoList.buttonRetrySomethingError,
            binding.includeConnectionErrorRepoList.buttonRetryConnectionError
        ).forEach {
            it.setOnClickListener {
                viewModel.getRepositories()
            }
        }
        adapter.onClickRepo = ::navigateToRepoFragment
    }

    private fun navigateToRepoFragment(repo: Repo) {
        findNavController().navigate(
            ReposFragmentDirections.actionReposFragmentToRepoFragment(args.user, repo)
        )
    }
}