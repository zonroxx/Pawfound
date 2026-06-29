package com.example.androidpetapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidpetapp.databinding.FragmentContactBinding


class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val shelterEmail = "shelter@example.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
        val message = binding.inputMessage.text?.toString()?.trim().orEmpty()

        val body = buildString {
            append(message)
            if (name.isNotEmpty() || email.isNotEmpty()) {
                append("\n\n-\n")
                if (name.isNotEmpty()) append("From: $name\n")
                if (email.isNotEmpty()) append("Email: $email\n")
            }
        }

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$shelterEmail")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Message from Pawfound app")
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No email app available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
