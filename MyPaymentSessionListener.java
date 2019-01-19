package com.example.stripe2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentResultListener;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.Customer;
import com.stripe.android.model.CustomerSource;
import com.stripe.android.model.Source;

public abstract class MyPaymentSessionListener implements PaymentSession.PaymentSessionListener {

    @Override
    public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
        final String selectedPaymentMethodId = data.getSelectedPaymentMethodId();
        CustomerSession.getInstance().retrieveCurrentCustomer(
                new CustomerSession.CustomerRetrievalListener() {
                    @Override
                    public void onCustomerRetrieved(@NonNull Customer customer) {
                        CustomerSource displaySource = customer.getSourceById(selectedPaymentMethodId);
                        // Display the card information on your screen.
                    }

                    @Override
                    public void onError(int errorCode, @Nullable String errorMessage){
                        //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Update your UI here with other data
        if (data.isPaymentReadyToCharge() && data.getPaymentResult() == PaymentResultListener.INCOMPLETE) {
            // Use the data to complete your charge - see below.
        }

    }

}
