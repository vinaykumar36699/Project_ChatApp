package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration_Activity extends AppCompatActivity {
    TextView txt_signin,btn_SignUp;
    CircleImageView profile_image;
    EditText reg_name, reg_email, reg_password, reg_cPassword;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        txt_signin = findViewById(R.id.txt_signin);
        profile_image = findViewById(R.id.profile_image);
        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        reg_cPassword = findViewById(R.id.reg_CPassword);
        btn_SignUp = findViewById(R.id.signUp_btn);

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = reg_name.getText().toString();
                String email = reg_email.getText().toString();
                String Password = reg_password.getText().toString();
                String CPassword = reg_cPassword.getText().toString();


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(Password) || TextUtils.isEmpty(CPassword))
                {
                    Toast.makeText(Registration_Activity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else if (!email.matches(emailPattern))
                {
                    reg_email.setError("Please Enter Valid Email");
                    Toast.makeText(Registration_Activity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else if (!Password.equals(CPassword)){
                    Toast.makeText(Registration_Activity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else if (Password.length()<6){
                    Toast.makeText(Registration_Activity.this, "Enter 6 Characters Password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    auth.createUserWithEmailAndPassword(email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                
                                Toast.makeText(Registration_Activity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
                                if (imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(auth.getUid(),name,email,imageURI);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    startActivity(new Intent(Registration_Activity.this,Home_Activity.class));
                                                                }else {
                                                                    Toast.makeText(Registration_Activity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }else {
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/chatapp-ad2d0.appspot.com/o/profile.jpg?alt=media&token=d0db2722-10e8-45f5-a862-2b1743cce366";
                                    Users users = new Users(auth.getUid(),name,email,imageURI);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                startActivity(new Intent(Registration_Activity.this,Home_Activity.class));
                                            }else {
                                                Toast.makeText(Registration_Activity.this, "Error in Creating User", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(Registration_Activity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration_Activity.this,Login_Activity.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            if (data!=null){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}