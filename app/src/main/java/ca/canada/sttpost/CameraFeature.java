package ca.canada.sttpost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Check https://developer.android.com/training/camera/photobasics.html for help
public class CameraFeature {

    /**
     * Creates an empty image file to write on
     * @param fileDir   The directory where the image will be saved
     * @return          The file containing the image
     */
    public static File createImageFile(File fileDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                fileDir         /* directory */
        );
        return image;
    }

    /**
     * Deletes the specified image
     * Used to delete the image once it has been uploaded
     * @param filePath  The path of the image to delete
     */
    public static void deleteImageFile(String filePath) {
        File imgFile = new File(filePath);
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
    public static Uri setPic(ImageView imgView, String imgPath) {
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

        return Uri.fromFile(new File(imgPath));
    }
}
