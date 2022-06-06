package com.example.proyekakhir_kelompok4;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser user;

    private String userID;

    private Button editProfile, cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("dataRegister");
        userID = user.getUid();

        editProfile = findViewById(R.id.editProfile);
        cancel = findViewById(R.id.cancel);

        final TextView tvUsername = (TextView) findViewById(R.id.nama);
        final TextView tvNim = (TextView) findViewById(R.id.nim);
        final TextView tvProdi = (TextView) findViewById(R.id.prodi);
        final TextView tvFakultas = (TextView) findViewById(R.id.fakultas);

        reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().child("username").getValue() != null){
                        tvUsername.setText(String.valueOf(task.getResult().child("username").getValue()));
                    }else{
                        tvUsername.setText("Username");
                    }
                    if (task.getResult().child("nim").getValue() != null){
                        tvNim.setText(String.valueOf(task.getResult().child("nim").getValue()));
                    }else{
                        tvNim.setText("Nim");
                    }
                    if (task.getResult().child("prodi").getValue() != null){
                        tvProdi.setText(String.valueOf(task.getResult().child("prodi").getValue()));
                    }else{
                        tvProdi.setText("Prodi");
                    }
                    if (task.getResult().child("fakultas").getValue() != null){
                        tvFakultas.setText(String.valueOf(task.getResult().child("fakultas").getValue()));
                    }else{
                        tvFakultas.setText("Fakultas");
                    }
                }else {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
