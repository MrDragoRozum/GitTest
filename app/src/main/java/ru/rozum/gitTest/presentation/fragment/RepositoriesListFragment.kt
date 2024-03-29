package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.rozum.gitTest.databinding.FragmentRepositoriesListBinding
import ru.rozum.gitTest.presentation.fragment.adapter.RepositoryListAdapter
import ru.rozum.gitTest.presentation.fragment.util.*
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoriesListViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoriesListViewModel.State
import javax.inject.Inject

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    private var _binding: FragmentRepositoriesListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapter: RepositoryListAdapter

    private val viewModel by viewModels<RepositoriesListViewModel>()

    private val args by navArgs<RepositoriesListFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoriesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        install()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun install() {
        installAdapter()
        state()
        listeners()
    }

    private fun installAdapter() {
        binding.recyclerViewRepos.adapter = adapter
        binding.recyclerViewRepos.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun state() {
        collectSmall(viewLifecycleOwner, viewModel.state) {
            installState(it)
        }
    }

    private fun installState(state: State) {
        binding.progressBarRepoDetails.visibility = setVisibility(state, State.Loading)
        binding.includeEmptyRepoList.root.visibility = setVisibility(state, State.Empty)

        binding.includeSomethingErrorRepoList.root.visibility =
            setVisibility(state, State.SomethingError)

        binding.includeConnectionErrorRepoList.root.visibility =
            setVisibility(state, State.ConnectionError)

        if (state is State.Loaded) adapter.submitList(state.repos)
    }

    private fun listeners() {
        listOf(
            binding.includeEmptyRepoList.buttonRetryEmpty,
            binding.includeSomethingErrorRepoList.buttonRetrySomethingError,
            binding.includeConnectionErrorRepoList.buttonRefresh
        ).forEach {
            it.setOnClickListener {
                viewModel.getRepositories()
            }
        }
        adapter.onClickRepo = { repo ->
            findNavController().navigate(
                RepositoriesListFragmentDirections
                    .actionRepositoriesListFragmentToDetailInfoFragment(args.userInfo, repo)
            )
        }
    }
}