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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;

public class UserSettingsActivity extends AppCompatActivity {

    private final String TAG = "SingUpActivity";
    private EditText _editTextWeight;
    private EditText _editTextAge;
    private EditText _editTextHeight;
    private TextView _welcomeTextView;
    private FirebaseAuth mAuth;
    private ProgressBar _progressBar;
    FirebaseUser user;
    private Button _saveButton;
    DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        _editTextWeight = (EditText)findViewById(R.id.editTextWeight);
        _editTextAge = (EditText)findViewById(R.id.editTextAge);
        _editTextHeight = (EditText)findViewById(R.id.editTextHeight);
        _progressBar = (ProgressBar)findViewById(R.id.saveSettingsProgressBar);
        _saveButton = (Button)findViewById(R.id.btnSaveSettings);
        _welcomeTextView = (TextView)findViewById(R.id.activity_user_settings_welcome_text);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            String uid = user.getUid();
            String email = user.getEmail();
            //Toast.makeText(this, "USER LOGGED IN", Toast.LENGTH_SHORT).show();

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User found, retrieve user details
                        int age = dataSnapshot.child("Age").getValue(Integer.class);
                        int height = dataSnapshot.child("Height").getValue(Integer.class);
                        double weight = dataSnapshot.child("Weight").getValue(Double.class);
                        String name = dataSnapshot.child("Username").getValue(String.class);

                        String ageString = String.valueOf(age);
                        String heightString = String.valueOf(height);
                        String weightString = String.valueOf(weight);

                        _editTextWeight.setText(weightString);
                        _editTextAge.setText(ageString);
                        _editTextHeight.setText(heightString);

                        _welcomeTextView.setText("Welcome " + name);

                        //Toast.makeText(UserSettingsActivity.this, "DATA ARRIVED", Toast.LENGTH_SHORT).show();
                        // Do something with the user details
                        // For example, update UI or perform some logic
                    } else {
                        //Toast.makeText(UserSettingsActivity.this, "USER DOESNT EXIST", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });

        } else {
            //Toast.makeText(this, "USER LOGIN ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public void onUserSettingButtonClick(View view){
        //Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show();
        _saveButton.setVisibility(View.INVISIBLE);
        String weight = _editTextWeight.getText().toString().trim();
        String height = _editTextHeight.getText().toString().trim();
        String age = _editTextAge.getText().toString().trim();

        Integer ageCasted = -1;
        Double weightCasted = -1.0d;
        Integer heightCasted = -1;

        if(weight.isEmpty()){
            _editTextWeight.setError("Please set Username");
            _editTextWeight.requestFocus();
        }

        if(height.isEmpty()){
            _editTextHeight.setError("Please set Password");
            _editTextHeight.requestFocus();
        }

        if(age.isEmpty()){
            _editTextAge.setError("Please set Password");
            _editTextAge.requestFocus();
        }

        try {
            weightCasted = Double.parseDouble(weight);
            System.out.println("Integer value: " + weight);
        } catch (NumberFormatException e) {
            _editTextWeight.setError("Please set correct weight value");
            _editTextWeight.requestFocus();
        }

        try {
            heightCasted = Integer.parseInt(height);
            System.out.println("Integer value: " + height);
        } catch (NumberFormatException e) {
            _editTextHeight.setError("Please set correct height value");
            _editTextHeight.requestFocus();
        }

        try {
            ageCasted = Integer.parseInt(age);
            System.out.println("Integer value: " + age);
        } catch (NumberFormatException e) {
            _editTextAge.setError("Please set correct age value");
            _editTextAge.requestFocus();
        }

        if(usersRef != null && user != null && weightCasted != -1 && heightCasted != -1 && ageCasted != -1){

            _progressBar.setVisibility(View.VISIBLE);

            usersRef.child(user.getUid()).child("Weight").setValue(weightCasted);
            usersRef.child(user.getUid()).child("Height").setValue(heightCasted);
            usersRef.child(user.getUid()).child("Age").setValue(ageCasted);

            //Toast.makeText(this, "DATA MODIFIED", Toast.LENGTH_SHORT).show();

            _saveButton.setVisibility(View.VISIBLE);

            navigateHome();
        }

        _saveButton.setVisibility(View.VISIBLE);

    }

    public void navigateHome(){
        Intent intent = new Intent(UserSettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}