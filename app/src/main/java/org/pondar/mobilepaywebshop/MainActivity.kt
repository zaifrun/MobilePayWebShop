package org.pondar.mobilepaywebshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dk.mobilepay.sdk.Country
import dk.mobilepay.sdk.MobilePay
import dk.mobilepay.sdk.ResultCallback
import dk.mobilepay.sdk.model.FailureResult
import dk.mobilepay.sdk.model.Payment
import dk.mobilepay.sdk.model.SuccessResult
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {

    private val MOBILEPAY_PAYMENT_REQUEST_CODE = 1337
    private var isMobilePayInstalled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobilePay.getInstance().init("APPDK0000000000", Country.DENMARK)

        //Using mobile pay test merchant code!
        isMobilePayInstalled = MobilePay.getInstance().isMobilePayInstalled(applicationContext)



        buy_button.setOnClickListener { placeOrder(50.0,"my_order_id_string") }



    }


    private fun placeOrder(price: Double, orderId: String) {

        if (isMobilePayInstalled) {
            // MobilePay is present on the system. Create a Payment object.
            val payment = Payment()
            payment.productPrice = BigDecimal(price)
            payment.orderId = orderId

            // Create a payment Intent using the Payment object from above.
            val paymentIntent = MobilePay.getInstance().createPaymentIntent(payment)

            // We now jump to MobilePay to complete the transaction. Start MobilePay and wait for the result using an unique result code of your choice.
            startActivityForResult(paymentIntent, MOBILEPAY_PAYMENT_REQUEST_CODE)
        } else {
            // MobilePay is not installed. Use the SDK to create an Intent to take the user to Google Play and download MobilePay.
            val intent = MobilePay.getInstance().createDownloadMobilePayIntent(applicationContext)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MOBILEPAY_PAYMENT_REQUEST_CODE) {
            // The request code matches our MobilePay Intent
            MobilePay.getInstance().handleResult(resultCode, data, object : ResultCallback {
                override fun onSuccess(result: SuccessResult?) {
                    // The payment succeeded - you can deliver the product.
                    Log.d("IntentResult","Success")
                    Toast.makeText(applicationContext,"Purchase successfull!",Toast.LENGTH_LONG).show()
                }

                override fun onFailure(result: FailureResult?) {
                    Log.d("IntentResult","failure: ${result?.errorMessage}")
                    Toast.makeText(applicationContext,"failure: ${result?.errorMessage}",Toast.LENGTH_LONG).show()


                    // The payment failed - show an appropriate error message to the user. Consult the MobilePay class documentation for possible error codes.
                }

                override fun onCancel(orderId: String?) {
                    // The payment was cancelled.
                    Log.d("IntentResult","Buy was cancelled by user")
                    Toast.makeText(applicationContext,"Buy was cancelled by user",Toast.LENGTH_LONG).show()

                }
            })
        }
    }
}