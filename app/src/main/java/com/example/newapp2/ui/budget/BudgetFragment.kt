package com.example.newapp2.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newapp2.R
import com.example.newapp2.data.PreferencesManager
import com.example.newapp2.data.TransactionType
import com.example.newapp2.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        setupBudgetInput()
        updateBudgetStatus()
    }

    private fun setupBudgetInput() {
        val currentBudget = preferencesManager.getMonthlyBudget()
        if (currentBudget > 0) {
            binding.budgetInput.setText(currentBudget.toString())
        }

        binding.saveBudgetButton.setOnClickListener {
            val budgetAmount = binding.budgetInput.text.toString().toDoubleOrNull()
            if (budgetAmount != null && budgetAmount > 0) {
                preferencesManager.setMonthlyBudget(budgetAmount)
                updateBudgetStatus()
            }
        }
    }

    private fun updateBudgetStatus() {
        val monthlyBudget = preferencesManager.getMonthlyBudget()
        val transactions = preferencesManager.getTransactions()
        val currentMonthExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        binding.currentBudgetText.text = "Monthly Budget: ${formatAmount(monthlyBudget)}"
        binding.currentExpenseText.text = "Current Expenses: ${formatAmount(currentMonthExpenses)}"

        val statusText = when {
            monthlyBudget == 0.0 -> "Set your monthly budget"
            currentMonthExpenses > monthlyBudget -> "You are out of Budget"
            else -> "You are in safe mode"
        }

        binding.statusText.text = statusText
        binding.statusText.setTextColor(
            requireContext().getColor(
                when {
                    monthlyBudget == 0.0 -> android.R.color.darker_gray
                    currentMonthExpenses > monthlyBudget -> android.R.color.holo_red_dark
                    else -> android.R.color.holo_green_dark
                }
            )
        )
    }

    private fun formatAmount(amount: Double): String {
        val currency = preferencesManager.getCurrency()
        return when (currency) {
            "USD" -> "$${String.format("%.2f", amount)}"
            "EUR" -> "€${String.format("%.2f", amount)}"
            "GBP" -> "£${String.format("%.2f", amount)}"
            "LKR" -> "Rs.${String.format("%.2f", amount)}"
            else -> "$${String.format("%.2f", amount)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 