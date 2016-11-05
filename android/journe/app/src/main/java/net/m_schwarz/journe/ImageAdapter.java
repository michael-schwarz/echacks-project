package net.m_schwarz.journe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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

        return rowView;
    }
}
