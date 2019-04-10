package com.example.elect.plus_minus_codex;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("messages");
    final Integer MAXLEN = 200;
    private static final int NOTIFY_ID = 101;
    ImageButton imgbut;
    EditText edittext1;
    RecyclerView mMessagesRecycler;
    String username = "";
    private final static String FILE_NAME = "content.txt";
    private NotificationManager nm;

    ArrayList<String> my_fav_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext1 = findViewById(R.id.edittext1);
        imgbut = findViewById(R.id.imgbutton);
        String maybename = openText();

        if (maybename.equals(""))  createDialog();
        else username = maybename;

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mMessagesRecycler = findViewById(R.id.recycl);
        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataadapter = new DataAdapter(this, my_fav_list);
        mMessagesRecycler.setAdapter(dataadapter);

        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ourmesg = edittext1.getText().toString();
                if (ourmesg.equals("")) {
                    return;
                }
                if (ourmesg.length() > MAXLEN) {
                    Toast.makeText(getApplicationContext(), "Your message is too long", Toast.LENGTH_SHORT).show();
                    return;
                }

                myRef.push().setValue(username+"/46433643/"+ourmesg);
                edittext1.setText("");
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String msg = dataSnapshot.getValue(String.class);
                my_fav_list.add(msg);
                if (!isForeground(getApplicationContext())) createNotification();
                dataadapter.notifyDataSetChanged();
                mMessagesRecycler.smoothScrollToPosition(my_fav_list.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings: {
                Toast.makeText(this, "You are beautiful", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public void createDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);
        final EditText userInput = promptsView.findViewById(R.id.input_text);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        username = userInput.getText().toString();
                        if (isCorrectUsername(username)) {
                            saveText(username);
                            dialog.dismiss();
                        }
                        else Toast.makeText(getApplicationContext(), "Your nickname is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    public boolean isCorrectUsername(String somename) {
        int leng = somename.length();
        if ((leng>10)||(leng<3)) return false;
        return true;
    }

    public boolean createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                .setTicker("New message")
                .setContentTitle("Yazzz!")
                .setContentText("There is new message")
                .setAutoCancel(true);
        Notification notification = builder.build();
        nm.notify(NOTIFY_ID, notification);
        return true;
    }

    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void saveText(String text){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
        }
        catch(IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String openText(){
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            return text;
        }
        catch(IOException ex) {
        }
        finally{
            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
            }
        }
        return "";
    }
}