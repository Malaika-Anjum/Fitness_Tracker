package com.miki.fitnesstracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PrefsManager(requireContext())

        view.findViewById<TextView>(R.id.text_user_name).text =
            prefs.getUsername()

        view.findViewById<TextView>(R.id.text_user_email).text =
            "Current Weight: ${prefs.getCurrentWeight()} kg"
    }
}
