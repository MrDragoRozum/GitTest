package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentDetailInfoBinding
import ru.rozum.gitTest.presentation.fragment.util.*
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoryInfoViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoryInfoViewModel.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private var _binding: FragmentDetailInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RepositoryInfoViewModel>()

    @Inject
    lateinit var markwon: Markwon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailInfoBinding.inflate(inflater, container, false)
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
        state()
        listeners()
    }

    private fun listeners() {
        binding.toolbarRepoDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.includeConnectionErrorRepoDetails.buttonRetryConnectionError.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun state() {
        collect(viewLifecycleOwner, viewModel.state) {
            installState(it)
        }
    }

    private fun installState(state: State) {
        binding.progressBarRepoDetails.visibility = setVisibility(state, State.Loading)
        binding.includeConnectionErrorRepoDetails.root.visibility =
            setVisibility(state, State.Error)

        if (state is State.Loaded) {
            binding.toolbarRepoDetails.title = state.githubRepo.name
            binding.detailsInfoRepoView.repoDetails = state.githubRepo
            binding.detailsInfoRepoView.visibility = View.VISIBLE
            binding.textViewReadme.visibility = View.VISIBLE
            installReadmeState(state.readmeState)
        }
    }

    private fun installReadmeState(state: ReadmeState) {
        if (state is ReadmeState.Loaded) markwon.setMarkdown(binding.textViewReadme, state.markdown)
        if (state is ReadmeState.Empty) binding.textViewReadme.text = getString(R.string.no_readme)

        binding.progressBarReadme.visibility = setVisibility(state, ReadmeState.Loading)
        binding.includeConnectionErrorRepoDetails.root.visibility =
            setVisibility(state, ReadmeState.Error)
    }
}