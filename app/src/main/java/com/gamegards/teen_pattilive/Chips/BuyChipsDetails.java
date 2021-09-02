package com.gamegards.teen_pattilive.Chips;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.teen_pattilive.Const;
import com.gamegards.teen_pattilive.R;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class BuyChipsDetails extends AppCompatActivity  {
    private static final String MY_PREFS_NAME = "Login_data";
    Button btnPaynow;
    TextView txtChipsdetails;
    String plan_id = "";
    String chips_details = "";
    String amount = "";
    String RazorPay_ID = "";
    private String order_id;
    ImageView imgback, imgPaynow;
    private BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chips_details);
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        billingClient = BillingClient.newBuilder(BuyChipsDetails.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if (queryPurchases != null && queryPurchases.size() > 0) {
                        for (Purchase purchase : queryPurchases) {
                            handlePurchase(purchase);
                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


        Intent intent = getIntent();
        plan_id = intent.getStringExtra("plan_id")+"_chips";


        chips_details = intent.getStringExtra("chips_details");
        amount = intent.getStringExtra("amount");

        imgPaynow = findViewById(R.id.imgPaynow);
        txtChipsdetails = findViewById(R.id.txtChipsdetails);
        txtChipsdetails.setText("Buy " + chips_details + " Pay now Rs." + amount);

        imgPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                place_order();
           /*
                if (billingClient.isReady()) {
                    Toast.makeText(BuyChipsDetails.this, "billing client is ready", Toast.LENGTH_SHORT).show();

                    initiatePurchase(plan_id.trim());
                }

                else {
                    billingClient = BillingClient.newBuilder(BuyChipsDetails.this)
                            .setListener(purchasesUpdatedListener)
                            .enablePendingPurchases()
                            .build();

                    billingClient.startConnection(new BillingClientStateListener() {
                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                initiatePurchase(plan_id.trim());
                            }
                        }

                        @Override
                        public void onBillingServiceDisconnected() {
                            // Try to restart the connection on the next request to
                            // Google Play by calling the startConnection() method.
                        }
                    });
                }
*/
            }
        });
        imgback = findViewById(R.id.imgback);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    public void place_order() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.PLCE_ORDER,
                response -> {

                    try {


                        JSONObject jsonObject = new JSONObject(response);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");

                        if (code.equals("200")) {

                            order_id = jsonObject.getString("order_id");
                            String Total_Amount = jsonObject.getString("Total_Amount");
                            RazorPay_ID = jsonObject.getString("RazorPay_ID");
                           // Toast.makeText(BuyChipsDetails.this, "200", Toast.LENGTH_SHORT).show();

                            if (billingClient.isReady()) {
                               // Toast.makeText(BuyChipsDetails.this, "billing client 1 is ready", Toast.LENGTH_SHORT).show();

                                initiatePurchase(plan_id.trim());
                            } else {
                                billingClient = BillingClient.newBuilder(BuyChipsDetails.this)
                                        .setListener(purchasesUpdatedListener)
                                        .enablePendingPurchases()
                                        .build();

                                billingClient.startConnection(new BillingClientStateListener() {
                                    @Override
                                    public void onBillingSetupFinished(BillingResult billingResult) {
                                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                           // Toast.makeText(BuyChipsDetails.this, "billing client 2 is ready", Toast.LENGTH_SHORT).show();

                                            initiatePurchase(plan_id.trim());
                                        }
                                    }

                                    @Override
                                    public void onBillingServiceDisconnected() {
                                        // Try to restart the connection on the next request to
                                        // Google Play by calling the startConnection() method.
                                    }
                                });
                            }
                            // startPayment(order_id,Total_Amount,RazorPay_ID);
                        } else if (code.equals("404")) {
                            Toast.makeText(BuyChipsDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // NoInternet(listTicket.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.TOKEN);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                params.put("plan_id", plan_id);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsDetails.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);


    }

    private void initiatePurchase(String productId) {
        List<String> skuList = new ArrayList<>();
        skuList.add(productId);


        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {

                    @Override
                    public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {




                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                            Log.d("skulist", "onSkuDetailsResponse: working"+ productId);
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(BuyChipsDetails.this, billingFlowParams);

                            } else {
                                Toast.makeText(BuyChipsDetails.this, "Purchase Item " + productId+" not found ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BuyChipsDetails.this, "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void startPayment(String ticket_id, String total_Amount, String razorPay_ID) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

            JSONObject options = new JSONObject();
            options.put("name", prefs.getString("name", ""));
            options.put("description", "chips payment");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", total_Amount);
            options.put("order_id", razorPay_ID);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "support@androappstech.com");
            preFill.put("contact", prefs.getString("mobile", ""));
            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /*

        @Override
        public void onPaymentSuccess(String razorpayPaymentID) {
            try {
                payNow(razorpayPaymentID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPaymentError(int i, String s) {
            try {
                //Toast.makeText(this, "Payment failed: " + code + " " + response, Toast
                // .LENGTH_SHORT).show();
            } catch (Exception e) {
                //Log.e(TAG, "Exception in onPaymentError", e);
            }
        }



     */
    public void payNow(final String payment_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.PY_NOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equals("200")) {
                                Toast.makeText(BuyChipsDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                                dialog_payment_success();
                            } else if (code.equals("404")) {
                                Toast.makeText(BuyChipsDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // NoInternet(listTicket.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.TOKEN);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                params.put("order_id", order_id);
                params.put("payment_id", payment_id);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsDetails.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);


    }

    private void dialog_payment_success() {

        new SmartDialogBuilder(BuyChipsDetails.this)
                .setTitle("Your Payment has been done Successfully!")
                .setSubTitle("Your Payment has been done Successfully!")
                .setCancalable(false)

                //.setTitleFont("Do you want to Logout?") //set title font
                // .setSubTitleFont(subTitleFont) //set sub title font
                .setNegativeButtonHide(true) //hide cancel button
                .setPositiveButton("Ok", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        smartDialog.dismiss();
                        finish();
                    }
                }).setNegativeButton("Cancel", new SmartDialogClickListener() {
            @Override
            public void onClick(SmartDialog smartDialog) {
                // Toast.makeText(context,"Cancel button Click",Toast.LENGTH_SHORT).show();
                smartDialog.dismiss();

            }
        }).build().show();


//        final Dialog dialog = new Dialog(BuyChipsDetails.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setTitle("");
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.dialog_payment_successful);
//        final Button btn_payNow = (Button) dialog.findViewById(R.id.btn_payNow);
//
//        btn_payNow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                finish();
//                //Ticketlist();
//            }
//        });
//
//        dialog.setCancelable(false);
//        dialog.show();

    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
                List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(BuyChipsDetails.this, "Purchase cancelled", Toast.LENGTH_SHORT).show();
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                Toast.makeText(BuyChipsDetails.this, "Error : " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                // Handle any other error codes.
            }
        }
    };


    void handlePurchase(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            if (!purchase.isAcknowledged()) {
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                ConsumeResponseListener listener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Handle the success of the consume operation.
                            Toast.makeText(BuyChipsDetails.this, "Payment successful", Toast.LENGTH_SHORT).show();

                            payNow(purchase.getOrderId());
                        }
                    }
                };
                billingClient.consumeAsync(consumeParams, listener);
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {

            Toast.makeText(this, "Purchase pending...", Toast.LENGTH_SHORT).show();
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            Toast.makeText(this, "Purchase status unknown", Toast.LENGTH_SHORT).show();
        }


    }


}
