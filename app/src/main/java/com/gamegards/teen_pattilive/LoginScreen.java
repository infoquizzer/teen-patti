package com.gamegards.teen_pattilive;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LoginScreen extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Login_data";
    private static final String TAG = "LoginScreen";
    EditText edtPhone, edtname, edtReferalCode;
    TextView tv, txtBirthDay;
    int pStatus = 0;
    private Handler handler = new Handler();
    ImageView imglogin;
    AlertDialog dialog;
    EditText edit_OTP;
    String verificationID;
    FirebaseAuth mAuth;
    RadioGroup radioGroup;
    boolean isSelected = true;
    RadioButton genderradioButton;
    ImageView imgBackground, imgBackgroundlogin;
    Context context = LoginScreen.this;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);

        Calendar today = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, year);
            calendar1.set(Calendar.MONTH, month);
            calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            txtBirthDay.setText(sdf.format(calendar1.getTime()));
            Integer age = calculateAge(calendar1.getTime());
            SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("age", age).apply();
        };

        imgBackground = findViewById(R.id.imgBackground);
        imgBackgroundlogin = findViewById(R.id.imgBackgroundlogin);

        String uri1 = "@drawable/" + "login_bg";  // where myresource " +
        int imageResource1 = getResources().getIdentifier(uri1, null,
                getPackageName());

        String uri2 = "@drawable/" + "login_box";  // where myresource " +
        int imageResource2 = getResources().getIdentifier(uri2, null,
                getPackageName());

        Picasso.get().load(imageResource1).into(imgBackground);
        // Picasso.get().load(imageResource2).into(imgBackgroundlogin);

        radioGroup = findViewById(R.id.radioGroup);
        mAuth = FirebaseAuth.getInstance();

        
        edtPhone = findViewById(R.id.edtPhone);
        edtname = findViewById(R.id.edtname);
        edtReferalCode = findViewById(R.id.edtReferalCode);
        imglogin = findViewById(R.id.imglogin);
        imglogin.setOnClickListener(view -> {
            if (formValidate()) {
                if (CommonFunctions.isNetworkConnected(LoginScreen.this)) {
                    //phoneLogin();
                    // Toast.makeText(LoginScreen.this,genderradioButton.getText(),
                    //Toast.LENGTH_SHORT).show();
                    RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    // Toast.makeText(LoginScreen.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if (isSelected) {

                        login(rb.getText() + "");


                    } else {

                        Toast.makeText(LoginScreen.this, "Please select Gender first ?", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    CommonFunctions.showNoInternetDialog(LoginScreen.this);
                }


            }

        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (null != rb && checkedId > -1) {
                isSelected = true;
                //Toast.makeText(LoginScreen.this, rb.getText(), Toast.LENGTH_SHORT).show();
            }

        });

        txtBirthDay = findViewById(R.id.txt_birth_day);

        txtBirthDay.setOnClickListener(v -> new DatePickerDialog(this, onDateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show());

    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Intent i = new Intent(LoginScreen.this, Homepage.class);
                             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(i);
                            Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void login(final String value) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.SEND_OTP,
                response -> {
                    progressDialog.dismiss();
                    handleResponse(response, value);
                }, error -> {
            progressDialog.dismiss();
            Toast.makeText(LoginScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", edtPhone.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


    private boolean formValidate() {

        if (edtPhone.getText().toString().isEmpty()) {
            return showToastNReturnFalse("Enter mobile number");
        } else if (edtname.getText().toString().isEmpty()) {
            return showToastNReturnFalse("Enter Name");
        } else if (txtBirthDay.getText().toString().isEmpty()) {
            return showToastNReturnFalse("Enter Birthday");
        }

        return true;
    }

    private boolean showToastNReturnFalse(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        return false;
    }


    private void handleResponse(String response, String value) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            String code = jsonObject.getString("code");


            if (code.equalsIgnoreCase("200")) {

                String otp_id = jsonObject.getString("otp_id");
                sendVerificationCodetoUser(edtPhone.getText().toString().trim());
                phoneLogin(otp_id, value);

            } else {

                if (jsonObject.has("message")) {
                    String message = jsonObject.getString("message");
                    Toast.makeText(LoginScreen.this, message, Toast.LENGTH_LONG).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void phoneLogin(final String otp_id, final String value) {
        // String phoneNumber= "+91"+edtPhone.getText().toString().trim();
        //SendVerificationCode(phoneNumber);
        final Dialog dialog = new Dialog(LoginScreen.this,
                android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialogbox_ctivity);
        dialog.setTitle("Title...");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView imgclose = dialog.findViewById(R.id.imgclose);
        edit_OTP = dialog.findViewById(R.id.edit_OTP);

        imgclose.setOnClickListener(v -> dialog.dismiss());

        ImageView imglogin = dialog.findViewById(R.id.imglogin);

        imglogin.setOnClickListener(v -> {
            if (edit_OTP.getText().toString().length() > 0) {


                String verify_code = edit_OTP.getText().toString();


                VerifyCode(verify_code, otp_id, value);

            } else {
                Toast.makeText(getApplicationContext(), "Please Enter OTP",
                        Toast.LENGTH_SHORT).show();

            }

        });

        dialog.show();

    }

    private void sendVerificationCodetoUser(String phoneNo) {

        PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                "+91"+phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            String code= credential.getSmsCode();

            if (code!=null){
                fbVerifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);
            Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private void fbVerifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private int calculateAge(Date date) {
        Date currentDate = new Date();
        int age = currentDate.getYear() - date.getYear();
        Log.d(TAG, "calculateAge: " + age);
        return age;
    }


    private void VerifyCode(final String code, final String otp_id, final String value) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.REGISTER,
                response -> {
                    progressDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String code1 = jsonObject.getString("code");


                        if (code1.equalsIgnoreCase("201")) {

                            String token = jsonObject.getString("token");

                            if (jsonObject.has("user")) {
                                JSONObject jsonObject1 = jsonObject.getJSONArray("user").getJSONObject(0);
                                String id = jsonObject1.getString("id");
                                String name = jsonObject1.getString("name");
                                String mobile = jsonObject1.getString("mobile");


                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("user_id", id);
                                editor.putString("name", name);
                                editor.putString("mobile", mobile);
                                editor.putString("token", token);
                                editor.apply();

                                fbVerifyCode(code);
/*
                                Intent i = new Intent(LoginScreen.this, Homepage.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_LONG).show();
                                */

                            } else {

                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(LoginScreen.this, "Wrong mobile number or password", Toast.LENGTH_LONG).show();
                                }

                            }


                        } else if (code1.equalsIgnoreCase("200")) {
                            String token = jsonObject.getString("token");
                            String user_id = jsonObject.getString("user_id");

                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("user_id", user_id);
                            editor.putString("name", edtname.getText().toString());
                            editor.putString("mobile", edtPhone.getText().toString());
                            editor.putString("token", token);

                            editor.apply();

                            fbVerifyCode(code);
                            /*
                            Intent i = new Intent(LoginScreen.this, Homepage.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_LONG).show();


                             */
//
                        } else {

                            if (jsonObject.has("message")) {
                                String message = jsonObject.getString("message");
                                Toast.makeText(LoginScreen.this, message, Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //  handleResponse(response);
                }, error -> {
            progressDialog.dismiss();
            Toast.makeText(LoginScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("otp", "9999");
//                params.put("name", edtname.getText().toString());
                params.put("otp_id", otp_id.trim());
                params.put("mobile", edtPhone.getText().toString());
                params.put("name", edtname.getText().toString());
                params.put("gender", value.trim());
                params.put("referral_code", edtReferalCode.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


}
