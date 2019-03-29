package ca.canada.sttpost;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int REQ_CODE_SPEECH_INPUT = 100;
    static final int REQ_CODE_TAKE_PHOTO = 1;
    static final int REQ_CODE_EDIT_TEXT = 2;

    private Uri imgUri;
    private File imgFile;
    private File fileDir;
    public String currentPhotoPath;

    private TextView textView;
    private TextView btnSpeak;
    private Button btnPost;
    private Button btnPhoto;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgUri = null;
        imgFile = null;
        fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        textView = findViewById(R.id.voiceInput);
        imgView = findViewById(R.id.imgView);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnPost = findViewById(R.id.btnPost);

        // Tap the TextView to open the TextZoomActivity
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchEditIntent();
            }
        });

        // Tap the button to start the Speech Recognition
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSpeechIntent();
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
    }

    // Shows the Google speech input dialog
    private void dispatchSpeechIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi! Say something!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            // whatever
        }
    }

    // Opens the camera and allows to take a picture
    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the picture should go
            try {
                imgFile = CameraFeature.createImageFile(fileDir);
                System.out.println(imgFile.getAbsolutePath());
                currentPhotoPath = imgFile.getAbsolutePath();
            } catch (Exception e) {
                // Error occurred while creating the File
                // ...
            }
            // Continue only if the File was successfully created
            if (imgFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.canada.sttpost.fileprovider",
                        imgFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQ_CODE_TAKE_PHOTO);
            }
        }
    }

    // Opens the TextZoomActivity
    private void dispatchEditIntent() {
        Intent intent = new Intent(this, TextZoomActivity.class);
        intent.putExtra("EXTRA_VOICE_INPUT", textView.getText().toString());
        startActivityForResult(intent, REQ_CODE_EDIT_TEXT);
    }

    // Receives and treats the results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText(result.get(0));
                }
                break;
            }
            case REQ_CODE_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    imgUri = CameraFeature.setPic(imgView, currentPhotoPath);
                }
                break;
            }
            case REQ_CODE_EDIT_TEXT: {
                if (resultCode == RESULT_OK) {
                    textView.setText(data.getData().toString());
                }
                break;
            }
        }
    }

    private void uploadPost() {
        // Instantiate an Upload object containing the references to the database.
        Upload upload = new Upload();
        if (textView.getText() == null && imgView.getDrawable() == null) {
            Toast.makeText(this, "Say something or take a picture first!", Toast.LENGTH_LONG).show();
        } else {
            upload.uploadText(this, textView);
            upload.uploadImage(this, imgView, imgUri);

            // Deletes the image from the device
            CameraFeature.deleteImageFile(imgFile.getAbsolutePath());
            imgFile = null;
            imgUri = null;
        }
    }
}
