package com.example.sonicodetsu.usersystemtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Filemanager extends AppCompatActivity {


    StorageReference storage;
    DatabaseReference db;
    FirebaseStorage fbs;
    FirebaseDatabase fb;
    int round;
    ArrayList<FileDetail> file;
    TableLayout tb;
    TableRow tr;
    LinearLayout layout2;
    int Folderupload = 0;
    String Foldername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager);
        tb = (TableLayout) findViewById(R.id.filelist);
        fbs = fbs.getInstance();
        fb = fb.getInstance();
        db = fb.getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        storage = fbs.getReferenceFromUrl("gs://project-final-dd369.appspot.com");
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        globaldata.getFileList().removeAllElements();
        Queryfilecount();
    }

    public  void Queryfilecount(){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        round = 0;
        Query query =db.child("user").child(globaldata.getUserid()).child("fileDetails").orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    round++;
                }
                getfilelist();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getfilelist(){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        db = fb.getInstance().getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        for(int i = 0 ; i <round ; i++) {
            Query query = db.child("user").child(globaldata.getUserid()).child("fileDetails").child(""+(0+i)).orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String filename = "";
                    String filelocal = "";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       if(ds.getKey().equals("filename")) {
                           filename =""+ ds.getValue();
                       }
                       else {
                           filelocal = ""+ ds.getValue();
                       }
                    }
                    globaldata.getFileList().add(new FileDetail(filename,filelocal));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFile();
            }
        }, 2000);

    }

    public void showFile(){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        tr = new TableRow(this);
        tr.setPadding(5,5,5,5);
        tb.addView(tr);
        String fdtest = "";
        for (FileDetail f : globaldata.getFileList()){
            layout2 = new LinearLayout(this);
            ImageView img = new ImageView(this);
            TextView filename = new TextView(this);
            layout2.setOrientation(LinearLayout.VERTICAL);
            String testfile [] = f.getFilename().split("/");
            if(testfile.length > 1  && !testfile[0].equals(fdtest)){
                filename.setText(testfile[0]);
                fdtest = testfile[0];
                img.setImageResource(R.drawable.folder);
            }
            else {
                String filetype [] = testfile[testfile.length-1].split("\\.");
                Toast.makeText(this,""+filetype[1],Toast.LENGTH_SHORT).show();
                filename.setText(f.getFilename());
                img.setImageResource(R.drawable.file);
            }
            tr.addView(layout2);
            layout2.addView(img);
            layout2.addView(filename);
        }

    }

/*    @Override
    protected void onResume() {
        super.onResume();
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        tb.removeAllViews();
        globaldata.getFileList().removeAllElements();
        getfilelist();
    }*/

   public void onClickUpload(View view){
        Folderupload = 0;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file*//*");
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

    public void onFolder(View view){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Must Select File After Enter Folder Name");
        builder.setMessage("Enter Folder Name");
        final EditText fname = new EditText(this);
        builder.setView(fname);
        builder.setPositiveButton("Select File", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Foldername = fname.getText().toString();
                Folderupload = 1;
                Query query = db.orderByKey().startAt("1001");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int a = 1001;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            a++;
                        }
                        File file = new File("");
                        Uri selectedUri_File = Uri.fromFile(file);
                        try {
                            String toWrite = "Hello";
                            File myfile = File.createTempFile("test", ".tmp");
                            FileWriter writer = new FileWriter(myfile);
                            writer.write(toWrite);
                            writer.close();
                            selectedUri_File = Uri.fromFile(myfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(this,Selectedfile,Toast.LENGTH_SHORT).show();
                        StorageReference riversRef = storage.child(globaldata.getUserid()+"/"+fname.getText().toString()+"/new.txt");
                        riversRef.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                //Toast.makeText(Filemanager.this,"555 "+downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                                File filelo = new File(downloadUrl.getLastPathSegment().toString());
                                String filename = filelo.getName();
                                //file = new FileTest(filename,downloadUrl.toString());
                                globaldata.getFileList().add(new FileDetail(fname.getText().toString()+"/",""));
                                String Username = globaldata.getList().get(0).getUsername();
                                String Password = globaldata.getList().get(0).getPassword();
                                db.child("user").child(globaldata.userid).setValue(new User(Username,Password,globaldata.getFileList()));
                                Intent intent = new Intent(Filemanager.this,Filemanager.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        if(resultCode == RESULT_OK){
            if(requestCode == 0 && Folderupload == 0){
                Uri selectedUri_File = data.getData();
                File myfile = new File(selectedUri_File.toString());
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
                        String Username = globaldata.getList().get(0).getUsername();
                        String Password = globaldata.getList().get(0).getPassword();
                        db.child("user").child(globaldata.userid).setValue(new User(Username,Password,globaldata.getFileList()));
                        Intent intent = new Intent(Filemanager.this,Filemanager.class);
                        startActivity(intent);
                        finish();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Filemanager.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Uri selectedUri_File = data.getData();
                File myfile = new File(selectedUri_File.toString());
                String Selectedfile = myfile.getAbsolutePath();
                //Toast.makeText(this,Selectedfile,Toast.LENGTH_SHORT).show();
                StorageReference riversRef = storage.child(globaldata.userid+"/"+Foldername+"/"+myfile.getName());
                riversRef.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //Toast.makeText(Filemanager.this,"555 "+downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                        File filelo = new File(downloadUrl.getLastPathSegment().toString());
                        String filename = filelo.getName();
                        //file = new FileTest(filename,downloadUrl.toString());
                        globaldata.getFileList().add(new FileDetail(Foldername+"/"+filename,downloadUrl.toString()));
                        String Username = globaldata.getList().get(0).getUsername();
                        String Password = globaldata.getList().get(0).getPassword();
                        db.child("user").child(globaldata.userid).setValue(new User(Username,Password,globaldata.getFileList()));
                        Intent intent = new Intent(Filemanager.this,Filemanager.class);
                        startActivity(intent);
                        finish();
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

    public void onCreateFolder(){
                /*final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StorageReference riversRef2 = storage.child(globaldata.getUserid()+"/"+fname.getText().toString()+"/new.txt");
                        riversRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Filemanager.this,"Success",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 2000);*/

    }
}
