package com.bagas.radiusence.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bagas.radiusence.FirebaseService
import com.bagas.radiusence.auth.LoginActivity
import com.bagas.radiusence.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authService: FirebaseService

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener { logoutUser() }
    }

    private fun logoutUser() {
        authService = FirebaseService()
        authService.logout()
        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
//        sharedPref?.edit()?.remove("uid")?.apply()
//        sharedPref?.edit()?.remove("name")?.apply()
//        sharedPref?.edit()?.remove("nim")?.apply()
//        sharedPref?.edit()?.remove("email")?.apply()
//        sharedPref?.edit()?.remove("avatarUrl")?.apply()
        sharedPref?.edit()?.clear()?.apply()
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}