package com.example.newapp2.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newapp2.data.Transaction
import com.example.newapp2.data.TransactionType
import com.example.newapp2.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(transaction: Transaction) {
            binding.apply {
                amountText.text = String.format("%.2f", transaction.amount)
                typeText.text = transaction.type.name
                categoryText.text = transaction.category
                descriptionText.text = transaction.description
                dateText.text = dateFormat.format(transaction.date)

                val color = when (transaction.type) {
                    TransactionType.INCOME -> android.R.color.holo_green_dark
                    TransactionType.EXPENSE -> android.R.color.holo_red_dark
                }
                amountText.setTextColor(root.context.getColor(color))

                editButton.setOnClickListener { onEditClick(transaction) }
                deleteButton.setOnClickListener { onDeleteClick(transaction) }
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 