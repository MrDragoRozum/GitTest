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
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentDetailInfoBinding
import ru.rozum.gitTest.presentation.fragment.markDown.*
import ru.rozum.gitTest.presentation.fragment.util.collectSmall
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoryInfoViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.RepositoryInfoViewModel.*


@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private var _binding: FragmentDetailInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RepositoryInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state()
        listeners()
    }

    private fun listeners() {
        binding.toolbarRepoDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonRetryDetailsRepo.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun state() {
        collectSmall(viewLifecycleOwner, viewModel.state) {
            installState(it)
        }
    }

    private fun installState(state: State) {
        binding.progressBarRepoDetails.visibility =
            if (state is State.Loading) View.VISIBLE else View.GONE

        if (state is State.Error) {
            binding.connectionErrorRepoDetails.root.visibility = View.VISIBLE
            binding.buttonRetryDetailsRepo.visibility = View.VISIBLE
        } else {
            binding.connectionErrorRepoDetails.root.visibility = View.GONE
            binding.buttonRetryDetailsRepo.visibility = View.GONE
        }


        if (state is State.Loaded) {
            binding.toolbarRepoDetails.title = state.githubRepo.name
            binding.detailsInfoRepoView.repoDetails = state.githubRepo
            binding.detailsInfoRepoView.visibility = View.VISIBLE
            binding.textViewReadme.visibility = View.VISIBLE
        }

        if (state is State.Loaded) {
            installReadmeState(state.readmeState)
        }
    }

    private fun installReadmeState(
        state: ReadmeState,
    ) {
        if (state is ReadmeState.Loaded) {
            // TODO: Вынести в DI
            val prism4j = Prism4j(GrammarLocatorDef())
            val prism4jTheme = Prism4jThemeDarkula.create()

            Markwon.builder(requireContext())
                .usePlugin(TaskListPlugin.create(requireContext()))
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(TablePlugin.create(requireContext()))
                .usePlugin(ImagesPlugin.create())
                .usePlugin(SyntaxHighlightPlugin.create(prism4j, prism4jTheme))
                .build()
                .setMarkdown(binding.textViewReadme, state.markdown)

        } else if (state is ReadmeState.Empty) {
            binding.textViewReadme.text = getString(R.string.no_readme)
        }

        binding.progressBarReadme.visibility =
            if (state is ReadmeState.Loading) View.VISIBLE else View.GONE

        // TODO: Вынести кнопку в макет include
        if (state is ReadmeState.Error) {
            binding.connectionErrorRepoDetails.root.visibility = View.VISIBLE
            binding.buttonRetryDetailsRepo.visibility = View.VISIBLE
        } else {
            binding.connectionErrorRepoDetails.root.visibility = View.GONE
            binding.buttonRetryDetailsRepo.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}