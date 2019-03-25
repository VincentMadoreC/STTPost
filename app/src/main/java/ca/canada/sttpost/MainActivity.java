package ca.canada.sttpost;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    static final int REQUEST_TAKE_PHOTO = 1;
    private EditText voiceInput;
    private TextView btnSpeak;
    private Button btnPost, btnPhoto;
    private ImageView imgView;
    private Uri filePath;
    private String currentPhotoPath;
    private String downloadUrl = "";
    private Post post;
//    private String imgCode = "";
    DatabaseReference databasePost;



    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceInput = (EditText) findViewById(R.id.voiceInput);
        imgView = (ImageView) findViewById(R.id.imgView);
        btnSpeak = (TextView) findViewById(R.id.btnSpeak);
        btnPost = (Button) findViewById(R.id.btnPost);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);

        // Firebase storage for images
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Tap the button to start the Speech Recognition
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askSpeechInput();
            }
        });

        // Tap the button to take a picture
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Tap the button to upload the post
        btnPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });

//        private Button btnTime = findViewById(R.id.btnTime);
//        btnTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View)
//        });

        databasePost = FirebaseDatabase.getInstance().getReference("Post");

    }

    // Showing google speech input dialog
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.canada.sttpost.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput.setText(result.get(0));
                }
                break;
            }
            case REQUEST_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    setPic();
                }
            }

        }
    }

    /**
     * Upload everything but the image (if any) which is stored somewhere else.
     */
    private void uploadRest(String imgUrl) {

        String body = voiceInput.getText().toString().trim();
        if (TextUtils.isEmpty(body)) {
            body = "";
        }
        String id = databasePost.push().getKey(); // generate an id

        Date date = new Date(); // get the UTC time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = df.format(date);

        String username = "vmado"; // set the username

        // Create the post object
        Post post = new Post(id, body, timestamp, username, imgUrl);

        // Save the post in the database, using the id as primary key
        databasePost.child(id).setValue(post);

        Toast.makeText(this, "Message posted!", Toast.LENGTH_LONG).show();

        // Clear the text field
        voiceInput.setText("");
    }


    /**
     * Upload the image (if any)
     */
    private void uploadImage() {

        /** If there is an image, upload it, get the URL and set that URL as argument when calling uploadRest()*/
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + filePath.getLastPathSegment());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    imgView.setImageDrawable(null); // Clear the image view
                                    uploadRest(downloadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else { /** If there is no image, set the download URL as "" */
            uploadRest("");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Display the picture in the imageView
     */
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imgView.getWidth();
        int targetH = imgView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imgView.setImageBitmap(bitmap);
        System.out.println(currentPhotoPath);


//        // From https://stackoverflow.com/questions/4830711/how-to-convert-a-image-into-base64-string
//        // Encode the image to store it in the database
//        Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//        imgCode = Base64.encodeToString(b, Base64.DEFAULT);

        filePath = Uri.fromFile(new File(currentPhotoPath));

    }

    private void uploadPost() {
        String body = voiceInput.getText().toString().trim();

        if (TextUtils.isEmpty(body) && imgView.getDrawable() == null) {
            Toast.makeText(this, "Say something or take a picture first!", Toast.LENGTH_LONG).show();
        } else {
            uploadImage();
        }
        filePath = null;
    }
}
