package hanu.a2_2001040108.mycart.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class ImageHandler {
        static Bitmap bmImage = null;

        public static Bitmap fetchImage(String url) {
                try {
                        InputStream is = new URL(url).openStream();
                        bmImage = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                        Log.e("Cart activity", e.getMessage());
                }
                return bmImage;
        }
}
