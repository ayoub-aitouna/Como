package com.come.live.who.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.come.live.R;

import com.come.live.who.Modules.PaymentModule;
import com.come.live.who.Modules.Users;
import com.come.live.who.Purchase.Prchase_MainActivity;
import com.come.live.who.Purchase.Security;
import com.come.live.who.Utils.PaymentAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import static com.come.live.who.Global.GetUserData;
import static com.come.live.who.Global.GetUserID;
import static com.come.live.who.Global.User;

public class Payments extends AppCompatActivity implements PurchasesUpdatedListener {
    private static final String TAG = "Payments";
    ArrayList<PaymentModule> data = new ArrayList<>();
    private BillingClient billingClient;
    public static final String PREF_FILE = "MyPref";
    LinearLayout progressBar;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        progressBar = findViewById(R.id.progress);
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if (queryPurchases != null && queryPurchases.size() > 0) {
                        handlePurchases(queryPurchases);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBillingServiceDisconnected() {
                progressBar.setVisibility(View.GONE);

            }
        });
        fillData();
        initView();
        PaymentAdapter adapter = new PaymentAdapter(data, position -> {
            this.position = position;
        });
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.purchase).setOnClickListener(v -> MakePayment());
    }

    private void initView() {
        ((TextView) findViewById(R.id.myCoins)).setText(String.valueOf(User.getCoins()));
    }

    private void fillData() {
        data.add(new PaymentModule("90% Discount only for fist time", "c1", 260, 300));
        data.add(new PaymentModule("70% Discount", "c1", 320, 300));
        data.add(new PaymentModule("70% Discount", "c1", 850, 600));
        data.add(new PaymentModule("70% Discount", "c1", 1200, 600));
        data.add(new PaymentModule("70% Discount", "c1", 1700, 800));
    }

    private void MakePayment() {
        StartPurchase();
    }

    void StartPurchase() {
        progressBar.setVisibility(View.VISIBLE);
        if (billingClient.isReady()) {
            progressBar.setVisibility(View.GONE);
            initiatePurchase(data.get(position).getItemKey());
        } else {
            Toast.makeText(Payments.this, "Reconnect", Toast.LENGTH_SHORT).show();
            billingClient = BillingClient
                    .newBuilder(Payments.this)
                    .enablePendingPurchases()
                    .setListener(Payments.this)
                    .build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(data.get(position).getItemKey());
                    } else {
                        Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }

    private void UpdateUser(int amount) {
        com.come.live.who.Retrofit.Retrofit.Data service = com.come.live.who.Retrofit.Retrofit.getRetrofitInstance().create(com.come.live.who.Retrofit.Retrofit.Data.class);
        Call<String> call = service.PurchaseCoins(GetUserID(this), amount);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.code() != 403 && response.body() != null) {
                    GetUserData(GetUserID(Payments.this), new Callback<Users>() {
                        @Override
                        public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                            if (response.code() == 200) User = response.body();
                        }

                        @Override
                        public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }


    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }

    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }

    private int getPurchaseCountValueFromPref(String PURCHASE_KEY) {
        return getPreferenceObject().getInt(PURCHASE_KEY, 0);
    }

    private void savePurchaseCountValueToPref(String PURCHASE_KEY, int value) {
        getPreferenceEditObject().putInt(PURCHASE_KEY, value).commit();
    }


    private void initiatePurchase(final String PRODUCT_ID) {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (skuDetailsList != null && skuDetailsList.size() > 0) {
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                            billingClient.launchBillingFlow(Payments.this, flowParams);
                        } else {
                            //try to add item/product id "c1" "c2" "c3" inside managed product in google play console
                            Toast.makeText(getApplicationContext(), "Purchase Item " + PRODUCT_ID + " not Found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Toast.makeText(getApplicationContext(), "onPurchasesUpdated", Toast.LENGTH_SHORT).show();
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void handlePurchases(List<Purchase> purchases) {
        Log.d(TAG, "handlePurchases: ");
        for (Purchase purchase : purchases) {
//            final int index = data.indexOf(purchase.getSkus());
            final int index = IndexOfList(purchase.getSkus().get(0));
//            data.indexOf(purchase.getSkus());
            //purchase found
            if (index > -1) {
                //if item is purchased
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                        // Invalid purchase
                        // show error to user
                        Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                        continue;//skip current iteration only because other items in purchase list must be checked if present
                    }
                    // else purchase is valid
                    //if item is purchased and not consumed
                    if (!purchase.isAcknowledged()) {
                        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    int consumeCountValue = getPurchaseCountValueFromPref(data.get(index).getItemKey()) + 1;
                                    savePurchaseCountValueToPref(data.get(index).getItemKey(), consumeCountValue);
                                    Toast.makeText(getApplicationContext(), "Item " + data.get(index).getItemKey() + "Consumed", Toast.LENGTH_SHORT).show();
                                    notifyList();
                                }
                            }
                        });

                        UpdateUser(data.get(position).getAmount());
                    }
                }
                //if purchase is pending
                else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    Toast.makeText(getApplicationContext(),
                            data.get(index).getItemKey() + " Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
                }
                //if purchase is refunded or unknown
                else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    Toast.makeText(getApplicationContext(), data.get(index).getItemKey() + " Purchase Status Unknown", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private int IndexOfList(String s) {
        int i = 0;
        for (PaymentModule item : data) {
            if (item.getItemKey().equals(s)) return i;
            i++;
        }
        return -1;
    }

    private void notifyList() {
    }


    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            //for old playconsole
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            //for new play console
            //To get key go to Developer Console > Select your app > Monetize > Monetization setup

            String base64Key = "Add your key here";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

}