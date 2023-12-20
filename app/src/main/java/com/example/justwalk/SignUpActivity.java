package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SingUpActivity";
    private EditText _editTextUsername;
    private EditText _editTextPassword;
    private EditText _editTextPasswordRepeat;
    private EditText _editTextPhoneNumber;
    private EditText _editTextEmail;
    private FirebaseAuth mAuth;
    private ProgressBar _progressBar;
    private Button _signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        _editTextUsername = (EditText)findViewById(R.id.edtSignUpFullName);
        _editTextEmail = (EditText)findViewById(R.id.edtSignUpEmail);
        _editTextPassword = (EditText)findViewById(R.id.edtSignUpPassword);
        _editTextPasswordRepeat = (EditText)findViewById(R.id.edtSignUpConfirmPassword);
        _editTextPhoneNumber = (EditText)findViewById(R.id.edtSignUpMobile);
        _progressBar = (ProgressBar)findViewById(R.id.signUpProgressBar);
        _signUpButton = (Button)findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
    }

    public void successfullSingUp(){
        Intent intent = new Intent(SignUpActivity.this, UserSettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void failedSignUp(){
        _editTextUsername.setText("");
        _editTextEmail.setText("");
        _editTextPassword.setText("");
        _editTextPasswordRepeat.setText("");
        _editTextPhoneNumber.setText("");
    }

    public void onSingUpButtonClick(View view){
        _signUpButton.setVisibility(View.INVISIBLE);
        String username = _editTextUsername.getText().toString().trim();
        String email = _editTextEmail.getText().toString().trim();
        String password = _editTextPassword.getText().toString().trim();
        String repeatPassword = _editTextPasswordRepeat.getText().toString().trim();
        String phoneNumber= _editTextPhoneNumber.getText().toString().trim();

        if(username.isEmpty()){
            _editTextUsername.setError("Please set Username");
            _editTextUsername.requestFocus();
        }

        if(password.isEmpty()){
            _editTextPassword.setError("Please set Password");
            _editTextPassword.requestFocus();
        }

        if(repeatPassword.isEmpty()){
            _editTextPasswordRepeat.setError("Please set Password");
            _editTextPasswordRepeat.requestFocus();
        }

        if(email.isEmpty()){
            _editTextEmail.setError("Please set Email");
            _editTextEmail.requestFocus();
        }

        if(phoneNumber.isEmpty()){
            _editTextPhoneNumber.setError("Please set Phone number");
            _editTextPhoneNumber.requestFocus();
        }

        if(!password.equals(repeatPassword)){
            _editTextPassword.setError("Passwords are different");
            _editTextPassword.requestFocus();
            _editTextPasswordRepeat.setError("Passwords are different");
            _editTextPasswordRepeat.requestFocus();
            _signUpButton.setVisibility(View.VISIBLE);
            return;
        }
        _progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    try {
                        User user = new User(username, password, email, phoneNumber, 0.0d, 0, 0);

                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "SIGNED IN SUCCESS", Toast.LENGTH_SHORT).show();
                                            _progressBar.setVisibility(View.GONE);
                                            successfullSingUp();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "SIGNED IN ERROR", Toast.LENGTH_SHORT).show();
                                            _progressBar.setVisibility(View.GONE);
                                            _signUpButton.setVisibility(View.VISIBLE);
                                            failedSignUp();
                                        }
                                    }
                                });
                    } catch(Exception ex){
                        Log.d(TAG, ex.getMessage().toString());
                    }
                } else{
                    Toast.makeText(SignUpActivity.this, "SIGNED IN ERROR", Toast.LENGTH_SHORT).show();
                    _progressBar.setVisibility(View.GONE);
                    _signUpButton.setVisibility(View.VISIBLE);
                    failedSignUp();
                }
            }
        });

    }
}