package com.example.admin.onlinedetectold;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2015-06-29.
 */
public class Utility {
    private static Canvas canvas = null;
    private static Paint paint = null;

    public static Bitmap parseJson(Context context, FrameLayout mFl, JSONObject jsonObject, ImageView mImg, TextView mTv, Bitmap mBitmap) {
        Bitmap squareBitmap = mBitmap;
        Bitmap ageBitmap = null;
        try {
            JSONArray faces = jsonObject.getJSONArray("face");
            int length = faces.length();
            mTv.setText("finded : " + length);
            for (int i = 0; i < length; i++) {
                JSONObject face = faces.getJSONObject(i);
                JSONObject position = face.getJSONObject("position");
                double x = position.getJSONObject("center").getDouble("x");
                double y = position.getJSONObject("center").getDouble("y");
                double width = position.getDouble("width");
                double height = position.getDouble("height");
                float centerX = (float) (x / 100 * mBitmap.getWidth());
                float centerY = (float) (y / 100 * mBitmap.getHeight());
                float squareWidth = (float) (width / 100 * mBitmap.getWidth());
                float squareHeight = (float) (height / 100 * mBitmap.getHeight());

                squareBitmap = drawSquareBitmap(squareBitmap, centerX, centerY, squareWidth, squareHeight);


                JSONObject attribute = face.getJSONObject("attribute");
                String gender = attribute.getJSONObject("gender").getString("value");
                String age = attribute.getJSONObject("age").getString("value");
//                ageBitmap = buildAgeView(context, mFl, "male".equals(gender), age);
//
//                if (squareBitmap.getWidth() < mBitmap.getWidth() && squareBitmap.getHeight() < mBitmap.getHeight()) {
//                    float ratio = Math.max(squareBitmap.getWidth() * 1.0f / mBitmap.getWidth(), squareBitmap.getHeight() * 1.0f / mBitmap.getHeight());
//                    ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int) (ageBitmap.getWidth() * ratio), (int) (ageBitmap.getHeight() * ratio), false);
//                }
//                canvas .drawBitmap(ageBitmap,centerX-squareWidth/2,centerY-squareHeight/2,null);

            }


//        Log.i("tag", "bitmap=" + bitmap);
            mImg.setImageBitmap(squareBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap buildAgeView(Context context, FrameLayout mFl, boolean isMale, String age) {
        Bitmap bitmap = null;
        TextView textView = (TextView) mFl.findViewById(R.id.tv_age_gender);
        if (isMale) {
            textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.ic_launcher), null, null, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.ic_launcher), null, null, null);
        }
        textView.setText(age);
        textView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(textView.getDrawingCache());
        textView.destroyDrawingCache();


        return bitmap;
    }

    private static Bitmap drawSquareBitmap(Bitmap mBitmap,float centerX,float centerY,float squareWidth,float squareHeight) {
//        Log.i("tag", "mBitmap.getWidth()=" + mBitmap.getWidth()+"mBitmap.getHeight()=" + mBitmap.getHeight()+"mBitmap.getConfig()=" + mBitmap.getConfig());
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());

        //实例化canvas，空的bitmap为载体
        canvas = new Canvas(bitmap);
        //把原始mBitmap画到Canvas上
        canvas.drawBitmap(mBitmap, 0, 0, null);

        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5);

//        float centerX = (float) (x / 100 * mBitmap.getWidth());
//        float centerY = (float) (y / 100 * mBitmap.getHeight());
//        float squareWidth = (float) (width / 100 * mBitmap.getWidth());
//        float squareHeight = (float) (height / 100 * mBitmap.getHeight());

        //在canvas上画线
        canvas.drawLine(centerX - squareWidth / 2, centerY - squareHeight / 2, centerX + squareWidth / 2, centerY - squareHeight / 2, paint);
        canvas.drawLine(centerX - squareWidth / 2, centerY + squareHeight / 2, centerX + squareWidth / 2, centerY + squareHeight / 2, paint);
        canvas.drawLine(centerX - squareWidth / 2, centerY - squareHeight / 2, centerX - squareWidth / 2, centerY + squareHeight / 2, paint);
        canvas.drawLine(centerX + squareWidth / 2, centerY - squareHeight / 2, centerX + squareWidth / 2, centerY + squareHeight / 2, paint);

//        canvas.drawLine(0f, 0f, 100f, 0f, paint);
//        canvas.drawLine(0f, 100f, 100f, 100f, paint);
//        canvas.drawLine(100f, 0f, 100f, 100f, paint);
//        canvas.drawLine(0f, 0f, 0f, 100f, paint);

        return bitmap;
    }
}
