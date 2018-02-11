package com.example.sonicodetsu.usersystemtest;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Vector;

public class Filemanager extends AppCompatActivity {

    StorageReference storage;
    DatabaseReference db;
    FirebaseStorage fbs;
    FirebaseDatabase fb;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager);
        fbs = fbs.getInstance();
        fb = fb.getInstance();
        db = fb.getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        storage = fbs.getReferenceFromUrl("gs://project-final-dd369.appspot.com");
        layout = (LinearLayout) findViewById(R.id.layout);
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        Button btn3 = new Button(this);
        btn3.setText("Select File");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(Intent.createChooser(intent,"Select File"),0);
                Query query = db.orderByKey().startAt("1001");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int a = 1001;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            a++;
                        }
                        //db.child("" + a).setValue(file);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        layout.addView(btn3);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        if(resultCode == RESULT_OK){
            if(requestCode == 0){
                Uri selectedUri_File = data.getData();
                java.io.File myfile = new java.io.File(selectedUri_File.toString());
                String Selectedfile = myfile.getAbsolutePath();
                //Toast.makeText(this,Selectedfile,Toast.LENGTH_SHORT).show();
                StorageReference riversRef = storage.child(globaldata.userid+"/"+myfile.getName());
                riversRef.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //Toast.makeText(Filemanager.this,"555 "+downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                        File filelo = new File(downloadUrl.getLastPathSegment().toString());
                        String filename = filelo.getName();
                        //file = new FileTest(filename,downloadUrl.toString());
                        globaldata.getFileList().add(new FileDetail(filename,downloadUrl.toString()));
                        globaldata.getList().get(0).setFileDetails(globaldata.getFileList());
                        db.child("user").child(globaldata.userid).setValue(globaldata.getList());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Filemanager.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
