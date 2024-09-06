package com.manish.samplegpayment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PAYMENT_REQUEST_CODE = 1234
    private var uniqueTransactionId = ""  // This should be unique for each transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val payButton: Button = findViewById(R.id.btn_pay)
        payButton.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        uniqueTransactionId = "txn" + System.currentTimeMillis() // Generate a unique transaction ID
        Log.d("ManishK", "startPayment: transaction id is - $uniqueTransactionId")

        val gPayIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "")// add your upi id here
                .appendQueryParameter("pn", "Aman")
                .appendQueryParameter("am", "1")
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", uniqueTransactionId)
                .build()
            Log.d("ManishKK", "startPayment: $data")

            setPackage("com.google.android.apps.nbu.paisa.user")  // Ensure it targets Google Pay
        }

        try {
            startActivityForResult(gPayIntent, PAYMENT_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            println("Google Pay app not found.")
            // Handle the case where Google Pay is not installed
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val transactionResponse = data.getStringExtra("response")
                println("Transaction Response: $transactionResponse")

                // Log all extras in the intent data
                val extras = data.extras
                if (extras != null) {
                    for (key in extras.keySet()) {
                        Log.d("ManishK", "Extra key: $key, value: ${extras.get(key)}")
                    }
                }

                // Verifying transaction ID in the response
                transactionResponse?.let {
                    val transactionIdInResponse = getTransactionIdFromResponse(it)
                    val status = getStatusFromResponse(it)

                    if (transactionIdInResponse == uniqueTransactionId && status == "SUCCESS") {
                        println("Transaction successful and verified: ID = $transactionIdInResponse, Status = $status")
                    } else {
                        println("Transaction ID mismatch or transaction failed. Received ID: $transactionIdInResponse, Status: $status")
                    }
                }
            } else {
                println("Transaction failed or canceled.")
            }
        }
    }

    private fun getTransactionIdFromResponse(response: String): String {
        return Uri.parse(response).getQueryParameter("txnRef") ?: ""
    }

    private fun getStatusFromResponse(response: String): String {
        return Uri.parse(response).getQueryParameter("Status") ?: ""
    }
}