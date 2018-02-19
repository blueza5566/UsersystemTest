package com.example.sonicodetsu.usersystemtest;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
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

import com.google.android.gms.common.stats.StatsEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;

public class Filemanager extends AppCompatActivity {


    StorageReference storage;
    DatabaseReference db;
    FirebaseStorage fbs;
    FirebaseDatabase fb;
    int round;
    TableLayout tb;
    TableRow tr;
    LinearLayout layout2;
    int Folderupload = 0;
    int FolderDelete = 0;
    ArrayList<String> Foldername = new ArrayList<>();
    int Foldercount = 0;
    String newFolder = "";
    int k= 0;
    FirebaseAuth mAuth;
    String Folderlo;
    StorageReference riversRe;
    int deletechecker;

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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword("neptuniafs@gmail.com","blue5566").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(Filemanager.this,"Success",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public  void Queryfilecount(){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        tb.removeAllViews();
        globaldata.getFileList().removeAllElements();
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

    public  void Queryfilecount2(){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        round = 0;
        Query query =db.child("user").child(globaldata.getUserid()).child("fileDetails").orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    round++;
                }
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
        k = 0;
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        tr = new TableRow(this);
        tr.setPadding(5,5,5,5);
        tb.addView(tr);
        String fdtest = "";
        int s0 = 0,s1 = 1,s2 = 2,ff = Foldercount;
        if(Foldercount > 0 ){
            Folderupload = 1;
            LinearLayout layout3 = new LinearLayout(this);
            TextView b = new TextView(this);
            ImageView b2 = new ImageView(this);
            b2.setImageResource(R.drawable.back);
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Foldername.remove(Foldercount-1);
                    Foldercount--;
                    Queryfilecount();
                /* if(Foldercount == 0){
                        Queryfilecount();
                    }
                    else {
                        onQueryFolder();
                    }*/

                }
            });
            tr.addView(layout3);
            layout3.addView(b2);
            layout3.addView(b);
        }


        for (FileDetail f : globaldata.getFileList()){
            layout2 = new LinearLayout(this);
            final ImageView img = new ImageView(this);
            TextView filename = new TextView(this);
            layout2.setOrientation(LinearLayout.VERTICAL);
            String testfile [] = f.getFilename().split("/");
            String testfile2 [] = f.getFilename().split("\\.");
            String testfile3 = "";
            img.setId(k);
            if(Foldercount > 0){
                testfile3 = Foldername.get(Foldercount-1);
                if(testfile.length == (s2 + ff)  && !testfile[(s0+ff)].equals(fdtest)&& Foldercount == (s0+ff) && testfile[ff-1].equalsIgnoreCase(testfile3)){
                    filename.setText(testfile[(s0+ff)]);
                    fdtest = testfile[(s0+ff)];
                    img.setImageResource(R.drawable.folder);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = img.getId();
                            onOpen(i);
                        }
                    });
                    img.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final String selector [] = {"Open","delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Filemanager.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    FolderDelete = 1;
                                    switch (which){
                                        case 0: onOpen(i);break;
                                        case 1: onDelete(i);break;
                                    }

                                }
                            });
                            builder.setNegativeButton("cancel", null);
                            builder.create();
                            builder.show();
                            return false;
                        }
                    });
                }
                else if(testfile2.length == 2 && Foldercount == (s0+ff) && testfile.length == (s1+ff) && testfile[ff-1].equalsIgnoreCase(testfile3)){
                    String filetype [] = testfile[testfile.length-1].split("\\.");
                    if(Foldercount == 0){
                        filename.setText(f.getFilename());
                    }
                    else{
                        filename.setText(testfile[testfile.length-1]);
                    }
                    img.setImageResource(R.drawable.file);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = img.getId();
                            onOpen(i);
                        }
                    });
                    img.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final String selector [] = {"download","delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Filemanager.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    switch (which){
                                        case 0: onOpen(i);break;
                                        case 1: onDelete(i);break;
                                    }

                                }
                            });
                            builder.setNegativeButton("cancel", null);
                            builder.create();
                            builder.show();
                            return false;
                        }
                    });
                }
            }
            else {
                if (testfile.length == (s2 + ff) && !testfile[(s0 + ff)].equals(fdtest) && Foldercount == (s0 + ff)) {
                    filename.setText(testfile[(s0 + ff)]);
                    fdtest = testfile[(s0 + ff)];
                    img.setImageResource(R.drawable.folder);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = img.getId();
                            onOpen(i);
                        }
                    });
                    img.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final String selector[] = {"Open", "delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Filemanager.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    FolderDelete = 1;
                                    switch (which) {
                                        case 0:
                                            onOpen(i);
                                            break;
                                        case 1:
                                            onDelete(i);
                                            break;
                                    }

                                }
                            });
                            builder.setNegativeButton("cancel", null);
                            builder.create();
                            builder.show();
                            return false;
                        }
                    });
                }

                else if(testfile2.length == 2 && Foldercount == (s0+ff) && testfile.length == (s1+ff)){
                    String filetype [] = testfile[testfile.length-1].split("\\.");
                    if(Foldercount == 0){
                        filename.setText(f.getFilename());
                    }
                    else{
                        filename.setText(testfile[testfile.length-1]);
                    }
                    img.setImageResource(R.drawable.file);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = img.getId();
                            onOpen(i);
                        }
                    });
                    img.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final String selector [] = {"download","delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Filemanager.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    switch (which){
                                        case 0: onOpen(i);break;
                                        case 1: onDelete(i);break;
                                    }

                                }
                            });
                            builder.setNegativeButton("cancel", null);
                            builder.create();
                            builder.show();
                            return false;
                        }
                    });
                }
            }

           /* else if(testfile.length == 2 && testfile2.length == 2 && Foldercount == 1){
                String filetype [] = testfile[testfile.length-1].split("\\.");
                filename.setText(testfile[testfile.length-1]);
                img.setImageResource(R.drawable.file);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = img.getId();
                        onOpen(i);
                    }
                });
            }
            else if(testfile.length == 2 && testfile2.length == 1 && Foldercount == 1){
                Toast.makeText(this,""+testfile,Toast.LENGTH_SHORT).show();
                filename.setText(testfile[0]);
                fdtest = testfile[0];
                img.setImageResource(R.drawable.folder);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = img.getId();
                        onOpen(i);
                    }
                });
            }*/
            tr.addView(layout2);
            layout2.addView(img);
            layout2.addView(filename);
            k++;
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
                newFolder = fname.getText().toString();
                Folderupload = 2;
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
                       /* File file = new File("");
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
                    */
                        //Toast.makeText(this,Selectedfile,Toast.LENGTH_SHORT).show();
  /*                      StorageReference riversRef = storage.child(globaldata.getUserid()+"/"+fname.getText().toString()+"/new.txt");
                        riversRef.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                //Toast.makeText(Filemanager.this,"555 "+downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                                File filelo = new File(downloadUrl.getLastPathSegment().toString());
                                String filename = filelo.getName();
                                //file = new FileTest(filename,downloadUrl.toString());
                                String Folderlo = "";
                                for(String s : Foldername){
                                    Folderlo = Folderlo + s+"/";
                                }
                                if(!Folderlo.equalsIgnoreCase("")){
                                    globaldata.getFileList().add(new FileDetail(Folderlo+fname.getText().toString()+"/",""));
                                }
                                else {
                                    globaldata.getFileList().add(new FileDetail(fname.getText().toString()+"/",""));
                                }

                                String Username = globaldata.getList().get(0).getUsername();
                                String Password = globaldata.getList().get(0).getPassword();
                                db.child("user").child(globaldata.userid).setValue(new User(Username,Password,globaldata.getFileList()));
                                Intent intent = new Intent(Filemanager.this,Filemanager.class);
                                startActivity(intent);
                                finish();
                            }
                        });*/
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
        Folderlo = "";
        for(String s : Foldername){
            Folderlo = Folderlo + s+"/";
        }
        if(resultCode == RESULT_OK){
            if(requestCode == 0 && Folderupload == 0){
                Uri selectedUri_File = data.getData();
                File myfile = new File(selectedUri_File.toString());
                String Selectedfile = myfile.getAbsolutePath();

                StorageReference riversRef = storage.child(globaldata.userid+"/"+myfile.getName());
                riversRef.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
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
                if(Folderupload == 1){
                    riversRe = storage.child(globaldata.userid+"/"+Folderlo+"/"+myfile.getName());
                }
                else{
                    riversRe = storage.child(globaldata.userid+"/"+Folderlo+newFolder+"/"+myfile.getName());
                }

                riversRe.putFile(selectedUri_File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        File filelo = new File(downloadUrl.getLastPathSegment().toString());
                        String filename = filelo.getName();
                        //file = new FileTest(filename,downloadUrl.toString());
                        if(Folderupload == 1){
                            globaldata.getFileList().add(new FileDetail(Folderlo+filename,downloadUrl.toString()));
                        }
                        else
                        {
                            globaldata.getFileList().add(new FileDetail(Folderlo+newFolder+"/"+filename,downloadUrl.toString()));
                        }
                        Folderupload = 0;
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

    public void onOpen(int i){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        final String name = globaldata.getFileList().get(i).getFilename();
        //Toast.makeText(this,name,Toast.LENGTH_SHORT).show();
        String testfile [] = name.split("/");
        if(testfile.length > 1){
            Foldername.add(testfile [0+Foldercount]);
            Foldercount++;
            Queryfilecount();
            /*onQueryFolder();*/
        }
        else {
/*           try {
                String filetype [] = name.split("\\.");
                StorageReference riversRef = storage.child(globaldata.getUserid()).child(name);
                final File downloadfile = File.createTempFile("download","."+filetype[1]);
                riversRef.getFile(downloadfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.fromFile(downloadfile));
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fileName");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                        request.allowScanningByMediaScanner();// if you want to be available from media players
                        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.fromFile(downloadfile));
                        startActivity(intent);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            storage.child(globaldata.getUserid()).child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                    request.allowScanningByMediaScanner();// if you want to be available from media players
                    DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                 /*   Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);*/
                }
            });

        }

    }

/*    public void onQueryFolder(){
        tb.removeAllViews();
        Queryfilecount2();
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        db = fb.getInstance().getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                            String filefolder [] = filename.split("/");
                            Toast.makeText(Filemanager.this,filefolder[filefolder.length-1]+"   " + Foldername + "   " + filefolder.length,Toast.LENGTH_SHORT).show();
                            if(filefolder.length > 1 && filefolder[0].equalsIgnoreCase(Foldername.get(Foldercount-1).toString())) {
                                globaldata.getFileList().add(new FileDetail(filefolder[filefolder.length-1], filelocal));
                            }
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
        }, 1000);

    }*/

    public void onDelete(int i){
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        String fileset = globaldata.getFileList().get(i).getFilename();
        if(FolderDelete == 0) {
            riversRe = storage.child(globaldata.userid + "/" + fileset);
            Toast.makeText(this, "" + riversRe.toString(), Toast.LENGTH_SHORT).show();
            globaldata.getFileList().remove(i);
            riversRe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String Username = globaldata.getList().get(0).getUsername();
                    String Password = globaldata.getList().get(0).getPassword();
                    db.child("user").child(globaldata.userid).setValue(new User(Username, Password, globaldata.getFileList()));
                    Toast.makeText(Filemanager.this, "Delete Success", Toast.LENGTH_SHORT).show();
                    Foldercount = 0;
                    Queryfilecount();
                }
            });
        }
        else {
            ArrayList<String> deletefile = new ArrayList<>();
            FolderDelete = 0;
            String localspit[] = fileset.split("/");
            String localafter = "";
            deletechecker = 0;
            for(int s = 0 ; s < (localspit.length-1) ; s++){
                localafter = localafter+localspit[s]+"/";
            }
            for (int f = 0; f < globaldata.getFileList().size() ; f++){
                String spittest[] = globaldata.getFileList().get(f).getFilename().split(localafter);
                String testtest = "";

                if(spittest.length > 1){
                    deletefile.add(globaldata.getFileList().get(f).getFilename());
                    globaldata.getFileList().remove(f);
                    f--;
                }
            }

            for (String s : deletefile){
                riversRe = storage.child(globaldata.userid + "/" + s);
                Toast.makeText(Filemanager.this, ""+riversRe.toString(), Toast.LENGTH_SHORT).show();
                riversRe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deletechecker = 1;
                    }
                });
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(deletechecker == 1) {
                        Toast.makeText(Filemanager.this, "" + deletechecker, Toast.LENGTH_SHORT).show();
                        String Username = globaldata.getList().get(0).getUsername();
                        String Password = globaldata.getList().get(0).getPassword();
                        db.child("user").child(globaldata.userid).setValue(new User(Username, Password, globaldata.getFileList()));
                        Toast.makeText(Filemanager.this, "Delete Success", Toast.LENGTH_SHORT).show();
                        Foldercount = 0;
                        Queryfilecount();
                    }
                }
            }, 2000);



        }

    }
    public void onRename(int i){

    }
    public void onMove(int i){

    }

    public void onCopy(int i){

    }
}
