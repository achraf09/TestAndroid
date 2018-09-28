package com.example.achraf.testandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.achraf.testandroid.entities.AccessToken;
import com.example.achraf.testandroid.entities.ApiError;
import com.example.achraf.testandroid.network.ApiService;
import com.example.achraf.testandroid.network.RetrofitBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.til_firstName)
    TextInputLayout tilFirstName;
    @BindView(R.id.til_lastName)
    TextInputLayout tilLastName;
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ButterKnife.bind(this);





        service = RetrofitBuilder.createService(ApiService.class);

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        setupRules();

        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(RegisterActivity.this, PostActivity.class));
            finish();
        }

    }

    @OnClick(R.id.btn_register)
    void register(){

        String first_name = tilFirstName.getEditText().getText().toString();
        String last_name = tilLastName.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        validator.clear();

        if(validator.validate()) {

            call = service.register(first_name, last_name, email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {

                        Log.w(TAG, "onResponse: " +response.body() );

                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(RegisterActivity.this, PostActivity.class));
                        finish();

                    } else {

                        handleErrors(response.errorBody());


                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {

                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        }

    }

    @OnClick(R.id.go_to_login)
    void redirectToLogin(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }


    private void handleErrors(ResponseBody response){
        ApiError apiError = Utils.converError(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if (error.getKey().equals("first_name")){
                tilFirstName.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("last_name")){
                tilLastName.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("email")){
                tilEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")){
                tilPassword.setError(error.getValue().get(0));
            }
        }


    }

    public void setupRules(){


        validator.addValidation(this, R.id.til_firstName, RegexTemplate.NOT_EMPTY, R.string.err_first_name);
        validator.addValidation(this, R.id.til_lastName, RegexTemplate.NOT_EMPTY, R.string.err_last_name);
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (call!= null){
            call.cancel();
            call = null;
        }
    }
}
