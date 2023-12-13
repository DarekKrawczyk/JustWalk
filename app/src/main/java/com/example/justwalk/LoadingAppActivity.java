package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class LoadingAppActivity extends AppCompatActivity {

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

        Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(4000);
                } catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(LoadingAppActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        };

        thread.start();
    }
}