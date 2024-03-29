package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentAuthBinding
import ru.rozum.gitTest.presentation.fragment.dialog.ErrorDialogFragment
import ru.rozum.gitTest.presentation.fragment.util.*
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        collectSmall(viewLifecycleOwner, viewModel.state) {
            installState(it)
        }
    }

    private fun installState(state: State) {
        binding.progressBarSignIn.visibility = setVisibility(state, State.Loading)

        if (state is State.InvalidInput) {
            returnBeginningStateButtonSignIn()
            binding.textInputLayoutSignIn.error = state.reason
        } else {
            binding.buttonSignIn.apply {
                text = EMPTY_TEXT
                isClickable = false
            }
            binding.textInputLayoutSignIn.error = null
        }
    }

    private fun action() {
        collectSmall(viewLifecycleOwner, viewModel.actions) {
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
                returnBeginningStateButtonSignIn()
            }
        }
    }

    private fun token() {
        collectSmall(viewLifecycleOwner, viewModel.token) {
            binding.textInputTextSignIn.setText(it)
        }
    }

    private fun returnBeginningStateButtonSignIn() {
        binding.buttonSignIn.apply {
            text = getString(R.string.sign)
            isClickable = true
        }
    }

    private companion object {
        const val EMPTY_TEXT = ""
    }
}