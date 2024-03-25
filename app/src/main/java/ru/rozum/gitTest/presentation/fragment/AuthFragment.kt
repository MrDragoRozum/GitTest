package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentAuthBinding
import ru.rozum.gitTest.presentation.fragment.dialog.ErrorDialogFragment
import ru.rozum.gitTest.presentation.fragment.viewModel.AuthViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.AuthViewModel.*

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        install()
    }

    private fun install() {
        listeners()
        state()
        action()
        token()
    }

    private fun listeners() {
        binding.buttonSignIn.setOnClickListener {
            binding.textInputTextSignIn.text.toString().also {
                viewModel.onSignButtonPressed(it)
            }
        }
        binding.textInputTextSignIn.addTextChangedListener {
            binding.textInputLayoutSignIn.error = null
        }
    }

    private fun state() {
        collectSmall(viewModel.state) {
            installState(it)
        }
    }

    private fun installState(state: State) {
        with(binding) {
            progressBarSignIn.visibility = if (state is State.Loading) View.VISIBLE else View.GONE
            if (state is State.InvalidInput) {
                buttonSignIn.text = getString(R.string.sign)
                textInputLayoutSignIn.error = state.reason
            } else {
                buttonSignIn.text = EMPTY_TEXT
                textInputLayoutSignIn.error = null
            }
        }
    }

    private fun action() {
        collectSmall(viewModel.actions) {
            installAction(it)
        }
    }

    private fun installAction(action: Action) {
        when (action) {
            is Action.RouteToMain -> findNavController().navigate(
                AuthFragmentDirections.actionAuthFragmentToRepositoriesListFragment(
                    action.user
                )
            )

            is Action.ShowError -> {
                ErrorDialogFragment.newInstance(action.message)
                    .show(requireActivity().supportFragmentManager, null)
                binding.progressBarSignIn.visibility = View.GONE
                binding.buttonSignIn.text = getString(R.string.sign)
            }
        }
    }

    private fun token() {
        collectSmall(viewModel.token) {
            binding.textInputTextSignIn.setText(it)
        }
    }

    private inline fun <T> collectSmall(consumer: Flow<T>, crossinline function: (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                consumer.collectLatest {
                    function(it)
                }
            }
        }
    }

    private companion object {
        const val EMPTY_TEXT = ""
    }
}