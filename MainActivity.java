package com.example.stripe2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Customer;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.model.Card;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity  {
    //private static String url= "https://www.yaquedo.com.mx/PHPDocuments/stripetest.php";
    Button button;
    Card cardToSave;

    private BroadcastReceiver mBroadcastReceiver;
    private Customer mCustomer;
    //private ErrorDialogHandler mErrorDialogHandler;
    private PaymentSession mPaymentSession;
    private Button mStartPaymentFlowButton;

    private ProgressBar mProgressBar;
    private TextView mResultTextView;
    private TextView mResultTitleTextView;
    private Button mSelectPaymentButton;

    private PaymentSessionData mPaymentSessionData;
    private MyPaymentSessionListener mPaymentSessionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // other onCreate logic
        mPaymentSession = new PaymentSession(this);
        // Create your listener and your configuration
        // ...
        // It's fine if the savedInstanceState is null.
        //mPaymentSession.init(mPaymentSessionListener, mPaymentSessionConfig, savedInstanceState);

        //super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        button = (Button) findViewById(R.id.button);

        cardToSave = mCardInputWidget.getCard();
        if (cardToSave == null) {

        }

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardToSave.validateNumber();
                cardToSave.validateCVC();

                Stripe stripe = new Stripe(MainActivity.this, "pk_test_TYooMQauvdEDq54NiTphI7jx");
                stripe.createToken(
                        cardToSave,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to your server
                            }
                            public void onError(Exception error) {
                                // Show localized error messag

                            }
                        }
                );
            }
        });
    }


    @Override
    public void onDestroy() {
        // Don't forget to unregister in onDestroy!
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void setupPaymentSession() {
        mPaymentSession = new PaymentSession(this);
        boolean paymentSessionInitialized = mPaymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onCommunicatingStateChanged(boolean isCommunicating) {
                if (isCommunicating) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }


            @Override
            public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
                //mResultTitleTextView.setVisibility(View.VISIBLE);
                //mResultTextView.setText(formatStringResults(mPaymentSession.getPaymentSessionData()));
            }

            @Override
            public void onError(int errorCode, @Nullable String errorMessage) {
                //mErrorDialogHandler.showError(errorMessage);
            }



        }, new PaymentSessionConfig.Builder()
                //.setPrepopulatedShippingInfo(getExampleShippingInfo())
                .build());
        if (paymentSessionInitialized) {
            button.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPaymentSession.handlePaymentData(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPaymentSession.savePaymentSessionInstanceState(outState);
    }





    public void crearTarjeta(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card;
        card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();


    }

        //probando como conectar con php, pero no se si está bien, esto nada tiene que ver con la doc de stripe
    class task extends AsyncTask<String, String, Void>
    {
        InputStream is = null ;
        String result = "";

        @Override
        protected Void doInBackground(String... params) {
            String url_select = "https://www.yaquedo.com.mx/PHPDocuments/stripetest.php";

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_select);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(param));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();


                is =  httpEntity.getContent();

            } catch (Exception e) {

                Log.e("log_tag", "Error in http connection "+e.toString());
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line=br.readLine())!=null)
                {
                    sb.append(line+"\n");
                }
                is.close();
                result=sb.toString();

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error converting result "+e.toString());
            }

            return null;

        }
        protected void onPostExecute(Void v) {



    }
}



}

