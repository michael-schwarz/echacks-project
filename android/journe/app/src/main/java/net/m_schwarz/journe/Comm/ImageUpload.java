package net.m_schwarz.journe.Comm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by michael on 05.11.16.
 */

public class ImageUpload {
    public void doIt(File file, Location location) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/ 8,bitmap.getHeight()/ 8,false);

            FileOutputStream fOutputStream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            String charset = "UTF-8";
            String requestURL = Config.baseUrl + "/savePicture/22/" + location.getLatitude() +"/" +
                    location.getLongitude()+ "/";

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addFilePart("imagefile", file);

            List<String> response = multipart.finish();

            Log.v("rht", "SERVER REPLIED:");

            for (String line : response) {
                Log.v("rht", "Line : " + line);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
