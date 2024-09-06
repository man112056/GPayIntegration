package com.manish.samplegpayment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class PaymentResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Simulate payment processing and set result data
        val resultIntent = Intent().apply {
            putExtra("transactionId", "TXN12345")
            putExtra("status", "Success")
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}