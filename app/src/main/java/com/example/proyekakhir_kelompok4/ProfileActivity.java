package com.example.proyekakhir_kelompok4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String userID;
    public Uri imageUri;

    private Button editProfile, cancel;
    private ImageView profilePic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("dataRegister");
        userID = user.getUid();

        editProfile = findViewById(R.id.editProfile);
        cancel = findViewById(R.id.cancel);
        profilePic = findViewById(R.id.profilepic);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

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

    private void uploadPicture() {
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
                                Toast.LENGTH_LONG).show();
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
