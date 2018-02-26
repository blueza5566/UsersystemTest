package com.example.sonicodetsu.usersystemtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class FilemanagerFolder extends AppCompatActivity {


    StorageReference storage;
    DatabaseReference db;
    FirebaseStorage fbs;
    FirebaseDatabase fb;
    int round;
    TableLayout tb;
    TableRow tr;
    LinearLayout layout2;
    int Folderupload = 0;
    int FolderDoing = 0;
    ArrayList<String> Foldername = new ArrayList<>();
    int Foldercount = 0;
    int k= 0;
    int r= 0;
    FirebaseAuth mAuth;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager_folder);
        intent = getIntent();
        final GlobalClass globaldata = (GlobalClass) getApplicationContext();
        tb = (TableLayout) findViewById(R.id.filelist);
        fbs = fbs.getInstance();
        fb = fb.getInstance();
        db = fb.getReferenceFromUrl("https://project-final-dd369.firebaseio.com/");
        storage = fbs.getReferenceFromUrl("gs://project-final-dd369.appspot.com");
        String whatdo = intent.getStringExtra("do");
        String filelo = intent.getStringExtra("filelo");
        String id = intent.getStringExtra("fileid");
        //Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
        intent.putExtra("fileid",id);
        if(whatdo.equalsIgnoreCase("copy")){
            setResult(101,intent);
        }
        else if (whatdo.equalsIgnoreCase("move")){
            setResult(102,intent);
        }
        globaldata.getFileList().removeAllElements();
        Queryfilecount();


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

                }
            });
            tr.addView(layout3);
            layout3.addView(b2);
            layout3.addView(b);
        }



        ArrayList<String> Folderlist = new ArrayList<>();
        ArrayList<String> Folderlist2 = new ArrayList<>();
        for (FileDetail f : globaldata.getFileList()){
            layout2 = new LinearLayout(this);
            final ImageView img = new ImageView(this);
            TextView filename = new TextView(this);
            layout2.setOrientation(LinearLayout.VERTICAL);
            String testfile [] = f.getFilename().split("/");
            String testfile2 [] = f.getFilename().split("\\.");
            String testfile3 = "";
            img.setId(k);
            boolean checksame = true;
            if(Foldercount > 0 && testfile.length > 2){
                Folderlist2.add(testfile[ff]);
                int check = 0;
                if(Folderlist2.size() > 1){
                    for(int p = 0 ; p < Folderlist2.size();p++){
                        if(Folderlist2.get(p).equalsIgnoreCase(testfile[ff])){
                            check++;
                        }
                    }
                    if(check > 1){
                        checksame = false;
                        Folderlist2.remove(Folderlist2.size()-1);
                    }
                }
            }
            else if(Foldercount == 0 && testfile.length > 1) {
                Folderlist.add(testfile[0]);
                int check = 0;
                if(Folderlist.size() > 1){
                    for(int p = 0 ; p < Folderlist.size();p++){
                        if(Folderlist.get(p).equalsIgnoreCase(testfile[0])){
                            check++;
                        }
                    }
                    if(check > 1){
                        checksame = false;
                        Folderlist.remove(Folderlist.size()-1);
                    }
                }
            }

            if(Foldercount > 0){
                testfile3 = Foldername.get(Foldercount-1);
                if(testfile.length == (s2 + ff)  && !testfile[(s0+ff)].equals(fdtest)&& Foldercount == (s0+ff) && testfile[ff-1].equalsIgnoreCase(testfile3) && checksame != false){
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
                            final String selector [] = {"Open","Select"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(FilemanagerFolder.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    switch (which){
                                    case 0: onOpen(i);break;
                                    case 1: intent.putExtra("uploadlo",globaldata.getFileList().get(i).getFilename());finish();break;
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
                if (testfile.length == (s2 + ff) && !testfile[(s0 + ff)].equals(fdtest) && Foldercount == (s0 + ff) && checksame != false) {
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
                            final String selector [] = {"Open","Select"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(FilemanagerFolder.this);
                            builder.setTitle("Select what to Do");
                            builder.setItems(selector, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = img.getId();
                                    FolderDoing = 1;
                                    switch (which) {
                                        case 0: onOpen(i);break;
                                        case 1: intent.putExtra("uploadlo",globaldata.getFileList().get(i).getFilename());finish();break;
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

            tr.addView(layout2);
            layout2.addView(img);
            layout2.addView(filename);
            k++;
        }

    }

    public  void  onSelectRoot(View view){
        intent.putExtra("uploadlo","");
        finish();
    }

    public  void  onCancel(View view){
        finish();
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
    }

}
