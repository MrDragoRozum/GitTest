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
import ru.rozum.gitTest.presentation.fragment.util.collectSmall
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
        installAdapter()
        state()
        listeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listeners() {
        with(binding) {
            listOf(buttonRetry, buttonRefresh).forEach {
                it.setOnClickListener {
                    viewModel.getRepositories()
                }
            }
        }
        adapter.onClickRepo = { repo ->
            findNavController().navigate(
                RepositoriesListFragmentDirections
                    .actionRepositoriesListFragmentToDetailInfoFragment(args.userInfo, repo)
            )
        }
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
        binding.progressBarRepoDetails.visibility =
            if (state is State.Loading) View.VISIBLE else View.GONE

        // TODO: Вынести кнопки в Include
        if (state is State.Empty) {
            binding.emptyRepoList.root.visibility = View.VISIBLE
            binding.buttonRefresh.visibility = View.VISIBLE
        } else {
            binding.emptyRepoList.root.visibility = View.GONE
            binding.buttonRefresh.visibility = View.GONE
        }

        if (state is State.Loaded) {
            adapter.submitList(state.repos)
        }

        if (state is State.SomethingError) {
            binding.somethingErrorRepoList.root.visibility = View.VISIBLE
            binding.buttonRetry.visibility = View.VISIBLE
        } else {
            binding.somethingErrorRepoList.root.visibility = View.GONE
            binding.buttonRetry.visibility = View.GONE
        }

        if (state is State.ConnectionError) {
            binding.connectionErrorRepoList.root.visibility = View.VISIBLE
            binding.buttonRefresh.visibility = View.VISIBLE
        } else {
            binding.connectionErrorRepoList.root.visibility = View.GONE
            binding.buttonRefresh.visibility = View.GONE
        }
    }
}