package com.example.proyekakhir_kelompok4;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String userID;
    public Uri imageUri;

    private Button save, cancel;
    private EditText nama, nim, prodi, fakultas;
    private TextView imgDownloadedUrl;
    private ImageView profilePic;

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
        profilePic = findViewById(R.id.profilepic);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        nama = (EditText) findViewById(R.id.nama);
        nim = (EditText) findViewById(R.id.nim);
        prodi = (EditText) findViewById(R.id.prodi);
        fakultas = (EditText) findViewById(R.id.fakultas);
        imgDownloadedUrl = (TextView) findViewById(R.id.imgDownloadedUrl);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String getprofilepicture = task.getResult().child("image").getValue().toString();
                if (task.isSuccessful()){
                    if (task.getResult().child("image").getValue() != null){
                        Picasso.get().load(getprofilepicture).resize(90,90).centerCrop().into(profilePic);
                    }
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
                    }if (task.getResult().child("image").getValue() != null){
                        imgDownloadedUrl.setText(String.valueOf(task.getResult().child("image").getValue()));
                    }else{
                        imgDownloadedUrl.setText("");
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
        String Image = imgDownloadedUrl.getText().toString();
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
                    DataStudent dataStudent = new DataStudent(Image, Username, Nim, Prodi, Fakultas);
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
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(EditProfileActivity.this,"Register First",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    public void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image ...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageReference.child("images/" + randomKey);
        mountainsRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded",
                                Toast.LENGTH_SHORT).show();

                        mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String linkProfileImg = uri.toString();
                                reference.child(userID).child("image").setValue(linkProfileImg)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Image Saved in Database"
                                                        ,Toast.LENGTH_LONG).show();
                                                updateUI(user);
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to Upload",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double proggressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Uploading: " + (int) proggressPercent + "%");
                    }
                });
    }
}
