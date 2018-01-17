package by.gsu.signpositionservice_app.gmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import by.gsu.SignPositionService.models.Sign;
import by.gsu.signpositionservice_app.R;

public class CustomGmapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    CustomGmapInfoWindowAdapter(LayoutInflater layoutInflater) {
        myContentsView = layoutInflater.inflate(R.layout.custom_gmap_info_window, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        Sign sign = (Sign) marker.getTag();
        TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
        tvTitle.setText(sign.getId() + "+" + marker.getId() + "-vva-" + marker.getTitle());
        TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());

        ImageView image = ((ImageView) myContentsView.findViewById(R.id.image));
        if (sign.getData() != null) {
            byte[] decodedString = Base64.decode(sign.getData(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            image.setImageBitmap(decodedByte);
        }
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}