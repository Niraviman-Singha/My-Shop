package com.example.myshop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myshop.R
import com.example.myshop.auth.User
import com.example.myshop.databinding.FragmentProfileBinding
import com.example.myshop.firebase.FirebaseConsts.USER_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        Firebase.firestore.collection(USER_PATH)
            .document(Firebase.auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    if (value!!.exists()) {
                        val user = value.toObject<User>()
                        binding.userNameTV.text = "Hi, ${user?.name}"
                        binding.phoneNumberTV.text = "+88 ${user?.phone_number}"
                    } else {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()

                }

                binding.userLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

            }
    }


}