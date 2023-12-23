package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingAppActivity extends AppCompatActivity {

    private FirebaseAuth _auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_app);

        //getSupportActionBar().hide();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView logo = findViewById(R.id.animationViewText);
        logo.animate().translationY(1000).setDuration(1000).setStartDelay(2500);

        LottieAnimationView animation = findViewById(R.id.lottieAnimationView);
        animation.animate().translationY(-2000).setDuration(1000).setStartDelay(2500);

        _auth = FirebaseAuth.getInstance();

        Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(4000);
                } catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    // If user is logged in
                    FirebaseUser user = _auth.getCurrentUser();
                    //FirebaseUser user = null;

                    // Not logged in
                    if(user == null){
                        Intent intent = new Intent(LoadingAppActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingAppActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }

        };

        thread.start();
    }
}