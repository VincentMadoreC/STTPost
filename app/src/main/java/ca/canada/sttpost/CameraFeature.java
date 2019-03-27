package ca.canada.sttpost;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class CameraFeature {

    public static final String IMAGE_NAME = "/pic.jpg";

    /**
     * Creates an empty image file to write on
     * @param fileDir The directory where the image will be saved
     * @return
     */
    public static File createImageFile(String fileDir) {
        File imgFile = new File(fileDir + IMAGE_NAME);
        try {
            if (imgFile.exists()) {
                imgFile.delete();
            }
            imgFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgFile;
    }

    public static void deleteImageFile(String fileDir) {
        File imgFile = new File(fileDir + IMAGE_NAME);
        try {
            if (imgFile.exists()) {
                imgFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * From https://developer.android.com/training/camera/photobasics.html
     * Sets a picture in the specified ImageView using the provided file path
     * @param imgView   The ImageView component that contains the picture
     * @param imgPath   The path to the image to display
     */
    private void setPic(ImageView imgView, String imgPath) {
        // Get the dimensions of the View
        int targetW = imgView.getWidth();
        int targetH = imgView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        imgView.setImageBitmap(bitmap);


//        System.out.println(imgPath);


//        // From https://stackoverflow.com/questions/4830711/how-to-convert-a-image-into-base64-string
//        // Encode the image to store it in the database
//        Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//        imgCode = Base64.encodeToString(b, Base64.DEFAULT);

        // Update the image Uri
        MainActivity.imgUri = Uri.fromFile(new File(imgPath));
    }

//    public static void test() {
////        String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
////        MainActivity mainActivity = new MainActivity().getI;
//        String storageDir;
//        storageDir = Activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//        System.out.println(storageDir);
//
//
//    }
}
