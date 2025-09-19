package com.example.recipebook.presentation.ui.setting

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.recipebook.databinding.FragmentSettingBinding
import com.example.recipebook.presentation.ui.info.InfoViewModel

class SettingFragment: Fragment() {

    lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingViewModel =
            ViewModelProvider(this).get(InfoViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSetting
        settingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }
}