package com.example.admin.onlinedetectold;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facepp.error.FaceppParseException;

import org.json.JSONObject;


public class MainActivity extends Activity implements View.OnClickListener {
    private TextView mTv;
    private Button mPickBtn, mDetectBtn;
    private ImageView mImg;
    private FrameLayout mFl;
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEnvent();
    }

    private void initEnvent() {
        mPickBtn.setOnClickListener(this);
        mDetectBtn.setOnClickListener(this);
    }

    private void initView() {
        mTv = (TextView) findViewById(R.id.tv);
        mPickBtn = (Button) findViewById(R.id.btn_pick);
        mDetectBtn = (Button) findViewById(R.id.btn_detect);
        mImg = (ImageView) findViewById(R.id.img);
        mFl = (FrameLayout) findViewById(R.id.fl);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pick:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.PICK);
                break;
            case R.id.btn_detect:
//                Toast.makeText(this,"detect",Toast.LENGTH_LONG).show();
                mFl.setVisibility(View.VISIBLE);
                Detector.detect(mBitmap, new Detector.CallBackListener() {
                    @Override
                    public void success(final JSONObject jsonObject) {
                        Log.i("tag", jsonObject + "");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.parseJson(MainActivity.this,mFl,jsonObject,mImg,mTv,mBitmap);


                                mFl.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        Log.i("tag", e + "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTv.setText("检测异常");
                                mFl.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK) {
//                Toast.makeText(this,"chenggong",Toast.LENGTH_LONG).show();
            if (data != null) {
                Uri contentResolverUri = data.getData();
                Cursor cursor = getContentResolver().query(contentResolverUri, null, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String imgPath = cursor.getString(columnIndex);
                mBitmap = reSetSize(imgPath);
                mImg.setImageBitmap(mBitmap);
                mTv.setText("detect -->");
                cursor.close();
            }
        }
    }

    private Bitmap reSetSize(String imgPath) {
        ///压缩选项
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //只解析原始图片尺寸到options中
        BitmapFactory.decodeFile(imgPath, options);
//        Log.i("tag", "NativeWidth=" + options.outWidth+"NativeHeight=" + options.outHeight);
        //取宽高的最大值，已兆（M）为单位
        double ratio = Math.max(options.outWidth * 1d / 1024d, options.outHeight * 1d / 1024d);
        //Math.ceil(ratio)意思是取比参数大的最小整数
        options.inSampleSize = (int) Math.ceil(ratio);

        ///根据压缩选项解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
//        Log.i("tag", "width=" + bitmap.getWidth() + ",height=" + bitmap.getHeight() + ",ratio =" + ratio + ",(int) Math.ceil(ratio) =" + (int) Math.ceil(ratio));

        return bitmap;
    }
}
