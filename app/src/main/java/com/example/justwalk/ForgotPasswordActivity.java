package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button _sendEmailButton;
    private ProgressBar _progressBar;
    private TextView _email;
    private FirebaseAuth _auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        _progressBar = (ProgressBar) findViewById(R.id.sendEmailProgressBar);
        _email = (TextView) findViewById(R.id.ForgotPasswordEmailTV);
        _sendEmailButton = (Button) findViewById(R.id.btnSendEmail);

        _auth = FirebaseAuth.getInstance();
    }

    public void onSendEmailForgotPasswordButtonClick(View view){
        String email = _email.getText().toString().trim();
        boolean enteredProperly = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _email.setError("Please enter a valid email!");
            _email.requestFocus();
            enteredProperly = false;
        }

        if(!enteredProperly){
            return;
        }

        _sendEmailButton.setVisibility(View.INVISIBLE);
        _progressBar.setVisibility(View.VISIBLE);

        _auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    successMailSend();
                }else{
                    failedMailSend();
                }
            }
        });
    }

    public void successMailSend(){
        _sendEmailButton.setVisibility(View.VISIBLE);
        _progressBar.setVisibility(View.INVISIBLE);
        _email.setText("");
        Toast.makeText(this, "EMAIL SENT", Toast.LENGTH_SHORT).show();
    }

    public void failedMailSend(){
        _sendEmailButton.setVisibility(View.VISIBLE);
        _progressBar.setVisibility(View.INVISIBLE);
        //_email.setError("Enter valid email!");
        //_email.requestFocus();
        _email.setText("");
        Toast.makeText(this, "FAILED TO SEND AN EMAIL", Toast.LENGTH_SHORT).show();
    }
}