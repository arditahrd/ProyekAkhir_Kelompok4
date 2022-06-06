package com.example.proyekakhir_kelompok4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail;
    private EditText etPass;
    private Button btnMasuk;
    private TextView tvSignUp;
    private TextView forgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail =(EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPassword);
        btnMasuk = (Button) findViewById(R.id.btnLogin);
        tvSignUp = (TextView) findViewById(R.id.text_signup);
        forgotPassword = (TextView) findViewById(R.id.text_forgotpass);

        mAuth = FirebaseAuth.getInstance();
        btnMasuk.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                login(etEmail.getText().toString(), etPass.getText().toString());
                break;
            case R.id.text_signup:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.text_forgotpass:
                Intent intentForgotPass = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intentForgotPass);
                break;
        }
    }

    public void login(String email,String password){
        if (!validateForm()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult>
                                               task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with thesigned-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, user.toString(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a messageto the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }
        if (TextUtils.isEmpty(etPass.getText().toString())) {
            etPass.setError("Required");
            result = false;
        } else {
            etPass.setError(null);
        }
        return result;
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this,"Log In First",
                    Toast.LENGTH_SHORT).show();
        }
    }
}