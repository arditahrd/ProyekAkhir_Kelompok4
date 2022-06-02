package com.example.proyekakhir_kelompok4;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private String userID;

    private Button save, cancel;
    private EditText nama, nim, prodi, fakultas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.saveEditProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("dataRegister");
        userID = user.getUid();
        mAuth = FirebaseAuth.getInstance();

        nama = (EditText) findViewById(R.id.nama);
        nim = (EditText) findViewById(R.id.nim);
        prodi = (EditText) findViewById(R.id.prodi);
        fakultas = (EditText) findViewById(R.id.fakultas);

        reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().child("username").getValue() != null){
                        nama.setText(String.valueOf(task.getResult().child("username").getValue()));
                    }else{
                        nama.setText("");
                    }
                    if (task.getResult().child("nim").getValue() != null){
                        nim.setText(String.valueOf(task.getResult().child("nim").getValue()));
                    }else{
                        nim.setText("");
                    }
                    if (task.getResult().child("prodi").getValue() != null){
                        prodi.setText(String.valueOf(task.getResult().child("prodi").getValue()));
                    }else{
                        prodi.setText("");
                    }
                    if (task.getResult().child("fakultas").getValue() != null){
                        fakultas.setText(String.valueOf(task.getResult().child("fakultas").getValue()));
                    }else{
                        fakultas.setText("");
                    }
                }else {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editprofile();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void editprofile(){
        String Username = nama.getText().toString();
        String Nim = nim.getText().toString();
        String Prodi = prodi.getText().toString();
        String Fakultas = fakultas.getText().toString();

        if (Username.isEmpty()) {
            nama.setError("Username is required!");
            nama.requestFocus();
            return;
        }
        if (Nim.isEmpty()) {
            nim.setError("Nim is required!");
            nim.requestFocus();
            return;
        }
        if (Prodi.isEmpty()) {
            prodi.setError("Prodi is required!");
            prodi.requestFocus();
            return;
        }
        if (Fakultas.isEmpty()) {
            fakultas.setError("Fakultas is required!");
            fakultas.requestFocus();
            return;
        }

        reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataStudent dataStudent = new DataStudent(Username, Nim, Prodi, Fakultas);
                    FirebaseDatabase.getInstance().getReference("dataRegister")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(dataStudent)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                        Toast.makeText(EditProfileActivity.this,
                                                "User has been edited!", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });

                }else{
                    // If sign in fails, display a messageto the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(EditProfileActivity.this, task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(EditProfileActivity.this,"Register First",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
