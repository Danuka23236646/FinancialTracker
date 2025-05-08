package com.example.newapp2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newapp2.R
import com.example.newapp2.data.PreferencesManager
import com.example.newapp2.data.Transaction
import com.example.newapp2.data.TransactionType
import com.example.newapp2.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            preferencesManager = PreferencesManager(requireContext())
            setupPieChart()
            updateDashboard()
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onViewCreated", e)
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            legend.isEnabled = true
            legend.textColor = Color.parseColor("#595957")
            legend.textSize = 18f
            legend.formSize = 16f
            setDrawEntryLabels(true)
            setEntryLabelTextSize(15f)
            setEntryLabelColor(Color.parseColor("#595957"))
            setNoDataText("No transactions available")
            setHoleRadius(40f)
            setTransparentCircleRadius(45f)
            setDrawCenterText(true)
            centerText = "Income vs\nExpense"
            setCenterTextSize(18f)
            setCenterTextColor(Color.parseColor("#0a0a0a"))
        }
    }

    private fun updateDashboard() {
        try {
            val transactions = preferencesManager.getTransactions()
            val totalIncome = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val totalExpense = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            val balance = totalIncome - totalExpense

            binding.totalIncomeText.text = formatAmount(totalIncome)
            binding.totalExpenseText.text = formatAmount(totalExpense)
            binding.totalBalanceText.text = formatAmount(balance)

            if (totalIncome > 0 || totalExpense > 0) {
                updatePieChart(totalIncome, totalExpense)
            }
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error updating dashboard", e)
        }
    }

    private fun updatePieChart(income: Double, expense: Double) {
        try {
            val entries = mutableListOf<PieEntry>()
            val total = income + expense
            
            if (income > 0) entries.add(PieEntry((income / total * 100).toFloat(), "Income"))
            if (expense > 0) entries.add(PieEntry((expense / total * 100).toFloat(), "Expense"))

            if (entries.isEmpty()) {
                binding.pieChart.setNoDataText("No transactions available")
                binding.pieChart.invalidate()
                return
            }

            val dataSet = PieDataSet(entries, "Income vs Expense")
            dataSet.colors = listOf(
                ColorTemplate.rgb("#4CAF50"), // Green for income
                ColorTemplate.rgb("#F44336")  // Red for expense
            )
            dataSet.valueTextSize = 20f
            dataSet.valueTextColor = ColorTemplate.rgb("#FFFFFF")
            dataSet.valueFormatter = PercentFormatter(binding.pieChart)
            dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(binding.pieChart))
            data.setValueTextSize(20f)
            data.setValueTextColor(ColorTemplate.rgb("#FFFFFF"))

            binding.pieChart.data = data
            binding.pieChart.animateY(1000)
            binding.pieChart.invalidate()
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error updating pie chart", e)
        }
    }

    private fun formatAmount(amount: Double): String {
        return try {
            val currency = preferencesManager.getCurrency()
            when (currency) {
                "USD" -> "$${String.format("%.2f", amount)}"
                "EUR" -> "€${String.format("%.2f", amount)}"
                "GBP" -> "£${String.format("%.2f", amount)}"
                "LKR" -> "Rs.${String.format("%.2f", amount)}"
                else -> "$${String.format("%.2f", amount)}"
            }
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error formatting amount", e)
            "$${String.format("%.2f", amount)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 