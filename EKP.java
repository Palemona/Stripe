package com.example.stripe2;

import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * An implementation of {@link EphemeralKeyProvider} that can be used to generate
 * ephemeral keys on the backend.
 */
public class EKP implements EphemeralKeyProvider {
    private @NonNull CompositeSubscription mCompositeSubscription;
    private @NonNull StripeService mStripeService;
    private @NonNull ProgressListener mProgressListener;

    public EKP(@NonNull ProgressListener progressListener) {
        //Retrofit retrofit = RetrofitFactory.getInstance();
        //mStripeService = retrofit.create(StripeService.class);
        mCompositeSubscription = new CompositeSubscription();
        mProgressListener = progressListener;
    }

    // Using RxJava for handling asynchronous responses
    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);

        mCompositeSubscription.add(
                mStripeService.createEphemeralKey(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                try {
                                    String rawKey = response.string();
                                    keyUpdateListener.onKeyUpdate(rawKey);
                                    mProgressListener.onStringResponse(rawKey);
                                } catch (IOException iox) {

                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                mProgressListener.onStringResponse(throwable.getMessage());
                            }
                        }));

        CustomerSession.initCustomerSession(new EKP(new EKP.ProgressListener() {
            @Override
            public void onStringResponse(String string) {
                if (string.startsWith("Error: ")) {
                    // Show the error to the user.
                }
            }
        }));
    }

    public interface ProgressListener {
        void onStringResponse(String string);
    }





}