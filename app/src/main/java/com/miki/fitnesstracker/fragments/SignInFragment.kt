package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.databinding.SigninFragmentBinding

class SignInFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: SigninFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SigninFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()

        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task: com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> ->

                    binding.progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        navController.navigate(
                            R.id.action_signInFragment_to_homeFragment,
                            null,
                            androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.signInFragment, true)
                                .build()
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message ?: "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
