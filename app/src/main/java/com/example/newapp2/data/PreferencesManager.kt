package com.example.newapp2.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "FinanceAppPrefs"
        private const val KEY_TRANSACTIONS = "transactions"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_THEME = "theme"
        private const val KEY_BACKUP_DATA = "backup_data"
        private const val KEY_ONBOARDING_SEEN = "onboarding_seen"
    }

    fun saveTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.add(transaction)
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(KEY_TRANSACTIONS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.remove(transaction)
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun updateTransaction(oldTransaction: Transaction, newTransaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        val index = transactions.indexOf(oldTransaction)
        if (index != -1) {
            transactions[index] = newTransaction
            val json = gson.toJson(transactions)
            sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
        }
    }

    fun setMonthlyBudget(amount: Double) {
        sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, amount.toFloat()).apply()
    }

    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    fun setCurrency(currency: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, "USD") ?: "USD"
    }

    fun setTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_THEME, isDark).apply()
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_THEME, false)
    }

    fun saveBackup(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_BACKUP_DATA, json).apply()
    }

    fun getBackup(): List<Transaction>? {
        val json = sharedPreferences.getString(KEY_BACKUP_DATA, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun hasSeenOnboarding(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_SEEN, false)
    }

    fun setOnboardingSeen() {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply()
    }
} 