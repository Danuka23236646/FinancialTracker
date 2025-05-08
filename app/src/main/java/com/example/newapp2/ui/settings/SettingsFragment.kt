package com.example.newapp2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.newapp2.R
import com.example.newapp2.data.PreferencesManager
import com.example.newapp2.databinding.FragmentSettingsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    private val currencies = listOf("USD", "EUR", "GBP", "LKR")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        setupCurrencyDropdown()
        setupThemeSwitch()
        setupBackupButtons()
    }

    private fun setupBackupButtons() {
        binding.exportButton.setOnClickListener {
            val transactions = preferencesManager.getTransactions()
            preferencesManager.saveBackup(transactions)
            Toast.makeText(requireContext(), "Data exported successfully", Toast.LENGTH_SHORT).show()
        }

        binding.restoreButton.setOnClickListener {
            val backupData = preferencesManager.getBackup()
            if (backupData != null) {
                preferencesManager.saveTransactions(backupData)
                Toast.makeText(requireContext(), "Data restored successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No backup data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCurrencyDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            currencies
        )
        
        binding.currencyInput.apply {
            threshold = 0
            isFocusable = false
            inputType = 0
        }

        binding.currencyInput.setAdapter(adapter)

        val currentCurrency = preferencesManager.getCurrency()
        binding.currencyInput.setText(currentCurrency, false)

        binding.currencyInput.setOnClickListener {
            binding.currencyInput.setAdapter(adapter)
            binding.currencyInput.showDropDown()
        }

        binding.currencyInput.setOnItemClickListener { _, _, position, _ ->
            val selectedCurrency = currencies[position]
            preferencesManager.setCurrency(selectedCurrency)
            binding.currencyInput.setText(selectedCurrency, false)
        }
    }

    private fun setupThemeSwitch() {
        val isDarkTheme = preferencesManager.isDarkTheme()
        binding.themeSwitch.isChecked = isDarkTheme

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 