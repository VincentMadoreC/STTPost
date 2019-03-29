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
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Upload {

    static DatabaseReference databasePost;
    private static FirebaseStorage storage;
    private static StorageReference storageReference;
    protected static ArrayList<String> downloadUrl = new ArrayList<>();
    protected static String postId;
    /**
     * Upload the image (if any)
     */
    protected static void uploadImage(final Context context, final ImageView imgView) {

        // Firebase storage for images
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        /** If there is an image, upload it, get the URL and set that URL as argument when calling uploadRest()*/
        if(MainActivity.imgUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

//            final StorageReference ref = storageReference.child("images/" + imgUri.getLastPathSegment());
            final StorageReference ref = storageReference.child("images/pic.jpg");
            ref.putFile(MainActivity.imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dlUrl = uri.toString();
                                    imgView.setImageDrawable(null); // Clear the image view
                                    addDownloadUrl(dlUrl);
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
        } else { /** If there is no image, set the download URL as "" */
            downloadUrl.add("");
            System.out.println(downloadUrl.get(0) + "!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    /**
     * Upload everything but the image (if any) which is stored somewhere else.
     */
    protected static void uploadText(Context context, TextView textView) {
        databasePost = FirebaseDatabase.getInstance().getReference("Post");

        String body = textView.getText().toString().trim();
        if (TextUtils.isEmpty(body)) {
            body = "";
        }

        postId = databasePost.push().getKey(); // generate an id

        Date date = new Date(); // get the UTC time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = df.format(date);

        String username = "vmado"; // set the username

        // Create the post object
//        Post post = new Post(postId, body, timestamp, username, downloadUrl.get(0));
        Post post = new Post(postId, body, timestamp, username, "");

        // Save the post in the database, using the id as primary key
        databasePost.child(postId).setValue(post);

        Toast.makeText(context, "Message posted!", Toast.LENGTH_LONG).show();

        // Clear the text field
        textView.setText("");
    }

    public static void addDownloadUrl(String dlUrl) {
        databasePost = FirebaseDatabase.getInstance().getReference("Post");
        databasePost.child(postId).child("imgUrl").setValue(dlUrl);
    }


    /**
     * when the image upload is complete, add the imgUrl to the JSON object
     *
     * If everything works, get rid of the static methods and instantiate an Upload oject instead
     */
}
