package com.example.sonicodetsu.usersystemtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase fbd;
    DatabaseReference db;
    EditText username;
    EditText password;
    Button login;
    Button re;
    String user = "";
    String pass = "";
    ArrayList<User> userlist;
    int round = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = fbd.getInstance().getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        re = (Button) findViewById(R.id.registor);
        login = (Button) findViewById(R.id.login);
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Registor.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check = 0;
                int userid = 1001;
                for (User u : userlist){
                    if(u.getUsername().equals(username.getText().toString()) && u.getPassword().equals(password.getText().toString())){
                        check = 1;
                        globaldata.setUserid(""+userid);
                        break;
                    }
                    else if(u.getUsername().equals(username.getText().toString())){
                        check = 2;
                        break;
                    }
                    userid++;
                }

                if(check == 0) {
                    Toast.makeText(MainActivity.this,"No Data Found",Toast.LENGTH_SHORT).show();
                }
                else if (check == 1){
                    globaldata.getList().add(new User(username.getText().toString(),password.getText().toString(),new Vector<FileDetail>()));
                    Toast.makeText(MainActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,Filemanager.class);
                    startActivity(intent);
                }
                else if(check == 2){
                    Toast.makeText(MainActivity.this,"Wrong Password",Toast.LENGTH_SHORT).show();
                }

            }
        });

        QueryUsercount();


    }

    public  void QueryUserlist(){
        userlist = new ArrayList<User>();
        for (int i =0; i < round  ; i++){
            Query query2 = db.child("user").child("100"+(i+1)).orderByKey();
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.getKey().equals("username")){
                            user = ""+ds.getValue();
                        }
                        if(ds.getKey().equals("password")){
                            pass = ""+ds.getValue();
                        }
                    }
                    userlist.add(new User(user,pass));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public  void QueryUsercount(){
        round = 0;
        Query query = db.child("user").orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    round++;
                }
                QueryUserlist();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        QueryUsercount();
    }
}
