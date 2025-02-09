
package payment;

import com.clover.sdk.v3.payments.PaymentRequest;
import com.clover.sdk.v3.payments.PaymentsService;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.CloverService;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PaymentsService paymentsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Clover SDK service
        paymentsService = new PaymentsService(this, new CloverService.PaymentListener() {
            @Override
            public void onPaymentSuccess(Payment payment) {
                // Handle successful payment
                Log.d("Clover", "Payment success: " + payment.getAmount());
            }

            @Override
            public void onPaymentFailure(Throwable error) {
                // Handle payment failure
                Log.e("Clover", "Payment failed: " + error.getMessage());
            }
        });
    }

    // Initiating a payment (example)
    private void initiatePayment(double amount) {
        // Set the payment request with the amount and other details
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount * 100);  // Amount in cents
        paymentRequest.setCurrency("USD");
        paymentRequest.setDescription("Payment for Order");

        // Start the payment
        paymentsService.startPayment(paymentRequest);
    }

    
}
