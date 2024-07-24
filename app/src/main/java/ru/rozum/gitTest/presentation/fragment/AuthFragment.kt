package ru.rozum.gitTest.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.rozum.gitTest.R
import ru.rozum.gitTest.databinding.FragmentAuthBinding
import ru.rozum.gitTest.presentation.fragment.dialog.ErrorDialogFragment
import ru.rozum.gitTest.presentation.fragment.util.collect
import ru.rozum.gitTest.presentation.fragment.util.setVisibility
import ru.rozum.gitTest.presentation.fragment.viewModel.AuthViewModel
import ru.rozum.gitTest.presentation.fragment.viewModel.AuthViewModel.Action
import ru.rozum.gitTest.presentation.fragment.viewModel.AuthViewModel.State

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {
    private val binding by viewBinding(FragmentAuthBinding::bind)
    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installFragment()
    }

    private fun installFragment() {
        installListeners()
        installState()
        installActions()
        installToken()
    }

    private fun installListeners() {
        binding.buttonSignIn.setOnClickListener {
            binding.textInputTextSignIn.text.toString().also { token ->
                viewModel.signIn(token)
            }
        }

        binding.textInputTextSignIn.addTextChangedListener {
            binding.textInputLayoutSignIn.error = null
        }
    }

    private fun installState() {
        collect(viewLifecycleOwner, viewModel.state) { state ->
            binding.progressBarSignIn.visibility = setVisibility(state, State.Loading)
            changeStateAuthDependingValidInput(state)
        }
    }

    private fun changeStateAuthDependingValidInput(state: State) {
        if (state is State.InvalidInput) {
            returnBeginningStateButtonSignIn()
            binding.textInputLayoutSignIn.error = getString(state.reasonId)
        } else {
            binding.buttonSignIn.apply {
                text = null
                isClickable = false
            }
            binding.textInputLayoutSignIn.error = null
        }
    }

    private fun installActions() {
        collect(viewLifecycleOwner, viewModel.actions) { action ->
            when (action) {
                is Action.RouteToMain -> navigateToRepositoriesFragment(action)
                is Action.ShowError -> showErrorDialog(action)
            }
        }
    }

    private fun navigateToRepositoriesFragment(action: Action.RouteToMain) {
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToReposFragment(
                action.user
            )
        )
    }

    private fun showErrorDialog(action: Action.ShowError) {
        ErrorDialogFragment
            .newInstance(action.message)
            .show(requireActivity().supportFragmentManager, null)

        binding.progressBarSignIn.visibility = View.GONE
        returnBeginningStateButtonSignIn()
    }


    private fun installToken() {
        collect(viewLifecycleOwner, viewModel.token) {
            binding.textInputTextSignIn.setText(it)
        }
    }

    private fun returnBeginningStateButtonSignIn() {
        binding.buttonSignIn.apply {
            text = getString(R.string.sign)
            isClickable = true
        }
    }
}