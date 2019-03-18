package com.example.elect.plus_minus_codex;

import android.content.DialogInterface;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    Integer MAXLEN = 200;
    ImageButton imgbut;
    EditText edittext1;
    RecyclerView mMessagesRecycler;
    String username="";
    ArrayList<String> my_fav_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext1 = findViewById(R.id.edittext1);
        imgbut = findViewById(R.id.imgbutton);
        onCreateDialog();
        mMessagesRecycler = findViewById(R.id.recycl);
        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataadapter = new DataAdapter(this, my_fav_list);
        mMessagesRecycler.setAdapter(dataadapter);

        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String HEHA = edittext1.getText().toString();
                if (HEHA.equals("")) {
                    return;
                }
                if (HEHA.length() > MAXLEN) {
                    Toast.makeText(getApplicationContext(), "Your message is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                myRef.push().setValue(HEHA);
                edittext1.setText("");
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String msg = dataSnapshot.getValue(String.class);
                my_fav_list.add(msg);
                dataadapter.notifyDataSetChanged();
                mMessagesRecycler.smoothScrollToPosition(my_fav_list.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Toast.makeText(this, "You are beautiful", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public boolean onCreateDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.input_text);
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    username = userInput.getText().toString();
                                }
                            });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
        return true;
    }
}