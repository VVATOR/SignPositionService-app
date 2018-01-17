package by.gsu.signpositionservice_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.widget.ImageView;

import by.gsu.SignPositionService.models.Sign;

public class ImageActivity extends FragmentActivity {

    private Intent intent;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        intent = getIntent();
        image = ((ImageView) findViewById(R.id.image));

        intent = getIntent();

        Sign sign = (Sign) intent.getExtras().get("sign");
        if (sign != null) {
            byte[] decodedString = Base64.decode(sign.getData(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }

    }
}
