package com.example.newapp2.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp2.R
import com.example.newapp2.data.PreferencesManager
import com.example.newapp2.data.Transaction
import com.example.newapp2.data.TransactionType
import com.example.newapp2.databinding.FragmentTransactionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.AutoCompleteTextView

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var transactionAdapter: TransactionAdapter

    private val incomeCategories = listOf("Salary", "Freelance", "Investments", "Other")
    private val expenseCategories = listOf("Food", "Transport", "Bills", "Entertainment", "Other")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        setupTypeDropdown()
        setupCategoryDropdown()
        setupRecyclerView()
        setupAddButton()
    }

    private fun setupTypeDropdown() {
        val types = TransactionType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.typeInput.setAdapter(adapter)
        binding.typeInput.setOnItemClickListener { _, _, position, _ ->
            updateCategoryDropdown(types[position])
        }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, emptyList<String>())
        binding.categoryInput.setAdapter(adapter)
    }

    private fun updateCategoryDropdown(type: String) {
        val categories = when (type) {
            TransactionType.INCOME.name -> incomeCategories
            TransactionType.EXPENSE.name -> expenseCategories
            else -> emptyList()
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding.categoryInput.setAdapter(adapter)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onEditClick = { transaction -> showEditDialog(transaction) },
            onDeleteClick = { transaction -> showDeleteDialog(transaction) }
        )
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
        updateTransactionsList()
    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            val amount = binding.amountInput.text.toString().toDoubleOrNull()
            val type = binding.typeInput.text.toString()
            val category = binding.categoryInput.text.toString()
            val description = binding.descriptionInput.text.toString()

            if (amount == null || type.isEmpty() || category.isEmpty()) {
                return@setOnClickListener
            }

            val transaction = Transaction(
                amount = amount,
                type = TransactionType.valueOf(type),
                category = category,
                description = description
            )

            preferencesManager.saveTransaction(transaction)
            clearInputs()
            updateTransactionsList()
        }
    }

    private fun showEditDialog(transaction: Transaction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_transaction, null)
        
        // Get references to dialog views
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.editAmountInput)
        val typeInput = dialogView.findViewById<AutoCompleteTextView>(R.id.editTypeInput)
        val categoryInput = dialogView.findViewById<AutoCompleteTextView>(R.id.editCategoryInput)
        val descriptionInput = dialogView.findViewById<TextInputEditText>(R.id.editDescriptionInput)

        // Set current values
        amountInput.setText(transaction.amount.toString())
        typeInput.setText(transaction.type.name, false)
        categoryInput.setText(transaction.category, false)
        descriptionInput.setText(transaction.description)

        // Setup type dropdown
        val types = TransactionType.values().map { it.name }
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        typeInput.setAdapter(typeAdapter)

        // Setup initial category dropdown
        val initialCategories = when (transaction.type) {
            TransactionType.INCOME -> incomeCategories
            TransactionType.EXPENSE -> expenseCategories
        }
        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, initialCategories)
        categoryInput.setAdapter(categoryAdapter)

        // Update category dropdown when type changes
        typeInput.setOnItemClickListener { _, _, position, _ ->
            val selectedType = types[position]
            val categories = when (selectedType) {
                TransactionType.INCOME.name -> incomeCategories
                TransactionType.EXPENSE.name -> expenseCategories
                else -> emptyList()
            }
            val newAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
            categoryInput.setAdapter(newAdapter)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.edit_transaction))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newAmount = amountInput.text.toString().toDoubleOrNull()
                val newType = typeInput.text.toString()
                val newCategory = categoryInput.text.toString()
                val newDescription = descriptionInput.text.toString()

                if (newAmount != null && newType.isNotEmpty() && newCategory.isNotEmpty()) {
                    val updatedTransaction = Transaction(
                        id = transaction.id,
                        amount = newAmount,
                        type = TransactionType.valueOf(newType),
                        category = newCategory,
                        description = newDescription,
                        date = transaction.date
                    )
                    preferencesManager.updateTransaction(transaction, updatedTransaction)
                    updateTransactionsList()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteDialog(transaction: Transaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_transaction))
            .setMessage(getString(R.string.delete_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                preferencesManager.deleteTransaction(transaction)
                updateTransactionsList()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateTransactionsList() {
        val transactions = preferencesManager.getTransactions().sortedByDescending { it.date }
        transactionAdapter.submitList(transactions)
    }

    private fun clearInputs() {
        binding.amountInput.text?.clear()
        binding.typeInput.text?.clear()
        binding.categoryInput.text?.clear()
        binding.descriptionInput.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 