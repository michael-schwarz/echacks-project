package net.m_schwarz.journe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by michael on 05.11.16.
 */

public class ImageAdapter extends ArrayAdapter<GeoImage> {

    private final Context context;
    private final GeoImage[] values;

    public ImageAdapter(Context context, GeoImage[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.image_item, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        if(BitmapCache.contains(values[position].url)){
            imageView.setImageBitmap(BitmapCache.get(values[position].url));
        } else{
            if(true || !BitmapCache.inTemptative(values[position].url)){
                BitmapCache.putTemptative(values[position].url);
                new ImageDownloader(imageView).execute(values[position].url);
            }
        }
        return rowView;
    }

    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
                mIcon = Bitmap.createScaledBitmap(mIcon,mIcon.getWidth()/2,mIcon.getHeight()/2,true);
                BitmapCache.put(urls[0],mIcon);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

