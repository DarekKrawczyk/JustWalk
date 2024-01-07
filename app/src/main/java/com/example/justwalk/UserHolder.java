package com.example.justwalk;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHolder {
    private static FirebaseAuth mAuth;
    private static FirebaseUser user;
    private static DatabaseReference usersRef;
    public static User User;
    public static void LoadUser() {

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String email = user.getEmail();

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int age = dataSnapshot.child("Age").getValue(Integer.class);
                        int height = dataSnapshot.child("Height").getValue(Integer.class);
                        double weight = dataSnapshot.child("Weight").getValue(Double.class);
                        String name = dataSnapshot.child("Username").getValue(String.class);
                        User newUser = new User("me", "my_pass", "my_email", "my_phone", weight, height, age);
                        User = newUser;
                    } else {
                        User = null;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    User = null;
                }
            });

        } else {
            User = null;
        }
    }

    public static boolean IsUserLoaded(){
        if(User != null) return true;
        return false;
    }

    public static User GetCurrentUser(){
        return User;
    }
}
