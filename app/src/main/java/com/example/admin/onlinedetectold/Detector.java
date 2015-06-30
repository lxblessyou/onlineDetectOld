package com.example.admin.onlinedetectold;

import android.graphics.Bitmap;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 2015-06-29.
 */
public class Detector {
    public interface CallBackListener {
        public abstract void success(JSONObject jsonObject);

        public abstract void error(FaceppParseException e);
    }

    public static void detect(final Bitmap bitmap, final CallBackListener callBackListener) {
//                Log.i("tag", "NativeWidth=" + bitmap.getWidth() + "NativeHeight=" + bitmap.getHeight());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //把原始bitmap裁剪为小图片-不知道为什么教程里的裁切了下
//                Bitmap smallBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                Log.i("tag", "smallWidth=" + smallBitmap.getWidth() + "smallHeight=" + smallBitmap.getHeight());
                byte[] data = byteArrayOutputStream.toByteArray();

                HttpRequests httpRequests = new HttpRequests(Constants.APP_KEY, Constants.APP_SECRET, true, true);
                PostParameters postParameters = new PostParameters();
                postParameters.setImg(data);
                try {
                    JSONObject jsonObject = httpRequests.detectionDetect(postParameters);
                    if (callBackListener != null) {
                        callBackListener.success(jsonObject);
                    }
//                    Log.i("tag", jsonObject + "");
                } catch (FaceppParseException e) {
                    e.printStackTrace();
                    if (callBackListener != null) {
                        callBackListener.error(e);
                    }
                }
            }
        }).start();
    }
}
