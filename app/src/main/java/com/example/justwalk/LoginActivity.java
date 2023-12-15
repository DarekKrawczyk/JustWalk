package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView _username;
    private TextView _password;
    private ProgressBar _progressBar;
    private Button _loginButton;
    private FirebaseAuth _authFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _username = (TextView) findViewById(R.id.LoginEmailTV);
        _password = (TextView) findViewById(R.id.loginPassword);
        _progressBar = (ProgressBar) findViewById(R.id.signInProgressBar);
        _loginButton = (Button) findViewById(R.id.btnSendEmail);
        _authFB = FirebaseAuth.getInstance();
    }

    public void onLoginButtonClick(View view){
        String username = _username.getText().toString().trim();
        String password = _password.getText().toString().trim();
        boolean enteredProperly = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            _username.setError("Please enter a valid email!");
            _username.requestFocus();
            enteredProperly = false;
        }

        if(password.isEmpty()){
            _password.setError("Please enter a password!");
            _password.requestFocus();
            enteredProperly = false;
        }

        if(!enteredProperly){
            return;
        }
        _loginButton.setVisibility(View.INVISIBLE);
        _progressBar.setVisibility(View.VISIBLE);

        _authFB.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    _loginButton.setVisibility(View.INVISIBLE);
                    _progressBar.setVisibility(View.GONE);
                    navigateHome();
                }else{
                    _loginButton.setVisibility(View.VISIBLE);
                    _progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void onRememberPasswordButtonClick(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void navigateHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}