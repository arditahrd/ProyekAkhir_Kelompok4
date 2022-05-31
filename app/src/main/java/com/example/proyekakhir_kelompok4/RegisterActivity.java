package com.example.proyekakhir_kelompok4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail;
    private EditText etPass;
    private EditText etUser;
    private Button btnRegister;
    private Button btnCancel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPassword);
        etUser = (EditText) findViewById(R.id.etUsername);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancel:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String username = etUser.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (username.isEmpty()) {
            etUser.setError("Username is required!");
            etUser.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide valid email!");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPass.setError("Password is required!");
            etPass.requestFocus();
            return;
        }
        else if(password.length() < 6){
            etPass.setError("Password Must be >= 6 Characters");
            etPass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DataRegister dataRegister = new DataRegister(username);
                            FirebaseDatabase.getInstance().getReference("dataRegister")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(dataRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                                Toast.makeText(RegisterActivity.this, "User has been registered successfully!",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Failed to register! Try again!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a messageto the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(RegisterActivity.this,"Register First",
                    Toast.LENGTH_SHORT).show();
        }
    }
}