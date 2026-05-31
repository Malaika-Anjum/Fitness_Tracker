package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.databinding.SignupFragmentBinding

class SignUpFragment : Fragment() {

    private lateinit var navController: androidx.navigation.NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: SignupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()

        // Navigate to SignInFragment when clicking "Already have an account?"
        binding.textViewSignIn.setOnClickListener {
            navController.navigate(
                R.id.action_signUpFragment_to_signInFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.signUpFragment, true)
                    .build()
            )
        }

        // Sign up button
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task: com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> ->

                    binding.progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Account created", Toast.LENGTH_SHORT).show()

                        navController.navigate(
                            R.id.action_signUpFragment_to_homeFragment,
                            null,
                            androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.signUpFragment, true)
                                .build()
                        )

                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message ?: "Signup failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
