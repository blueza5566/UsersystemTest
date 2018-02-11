package com.example.sonicodetsu.usersystemtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Registor extends AppCompatActivity {

    FirebaseDatabase fbd;
    DatabaseReference db;
    EditText user;
    EditText password;
    Button re;
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registor);
        db = fbd.getInstance().getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        user = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        re = (Button) findViewById(R.id.registor);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = 1001;
                Query query = db.child("user").orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            a++;
                        }
                        db.child("user").child(""+a).setValue(new User(user.getText().toString(),password.getText().toString()));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
