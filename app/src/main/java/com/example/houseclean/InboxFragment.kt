package com.example.houseclean

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.houseclean.databinding.FragmentInboxBinding
import com.example.houseclean.databinding.FragmentPerfilBinding

class InboxFragment : Fragment(R.layout.fragment_inbox) {
    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_map, container, false)
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }
}