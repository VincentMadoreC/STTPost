package ca.canada.sttpost;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Upload2 {

    private DatabaseReference databaseReference;
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
//    protected static ArrayList<String> downloadUrl = new ArrayList<>();
    private String postKey;


    /**
     * Uploads the image (if any)
     * @param context   The context of the activity that calls the method
     * @param imgView   The ImageView component that contains the image to upload
     * @param imgUri    The URI of the image stored temporarily on the device
     */
    public void uploadImage(final Context context, final ImageView imgView, Uri imgUri) {

        // Images are stored in Firebase storage, separated from the rest of the post
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        // If there is an image, upload it, get the URL and set that URL as argument when calling uploadRest()
        if(imgUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + imgUri.getLastPathSegment());
//            final StorageReference ref = storageReference.child("images/pic.jpg");
            ref.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    imgView.setImageDrawable(null); // Clear the image view
                                    addDownloadUrl(downloadUrl);
//                                    downloadUrl.add(dlUrl);
//                                    System.out.println(downloadUrl.get(0) + "????????????????????");
//                                    uploadRest(downloadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }

    /**
     * Uploads everything but the image (if any) which is stored somewhere else.
     * @param context   The context of the activity that calls the method
     * @param textView  The TextView component that contains the text to upload
     */
    protected void uploadText(Context context, TextView textView) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Post");

        // Creates a key for the post
        this.postKey = this.databaseReference.push().getKey(); // generate an id

        // Sets the body of the post
        String body = textView.getText().toString().trim();
        if (TextUtils.isEmpty(body)) {
            body = "";
        }

        // Sets the username
        String username = "vmado";

        // Generates the timestamp to indicate when the post was created
        Date date = new Date(); // get the UTC time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = df.format(date);

        // Create the post object using the values above
        // The imgURL is set to "" while it waits to be assigned when the image is done uploading
//        Post post = new Post(postKey, body, timestamp, username, downloadUrl.get(0));
        Post post = new Post(postKey, body, timestamp, username, "");

        // Save the post in the database, using the id as primary key
        this.databaseReference.child(postKey).setValue(post);

        Toast.makeText(context, "Message posted!", Toast.LENGTH_LONG).show();

        // Clear the text field
        textView.setText("");
    }

    /**
     * Overwrite the value associated with the key 'imgUrl' (usually "") with the real download URL
     * Called after the image has been successfully uploaded.
     * @param downloadUrl   The URL of the image on the web
     */
    private void addDownloadUrl(String downloadUrl) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        this.databaseReference.child(this.postKey).child("imgUrl").setValue(downloadUrl);
    }
}
