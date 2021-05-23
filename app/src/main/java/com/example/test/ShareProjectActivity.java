package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class ShareProjectActivity extends AppCompatActivity {
    TextView tv_title, tv_name, tv_save;
    LinearLayout savePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_project);
        tv_title = findViewById(R.id.top);
        tv_name = findViewById(R.id.name);
        tv_save = findViewById(R.id.tv_save);
        savePic = findViewById(R.id.pic);
        String name = getIntent().getStringExtra("NAME");
        tv_title.setText(name);
        tv_name.setText(name);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_save.setClickable(false);
                //相关权限的申请 存储权限
                try {
                    if (ActivityCompat.checkSelfPermission(ShareProjectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(ShareProjectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
                        ActivityCompat.requestPermissions(ShareProjectActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
//                        mLDialog.setDialogText("正在保存图片...");
//                        mLDialog.show();
                        Toast.makeText(ShareProjectActivity.this, "正在保存图片", Toast.LENGTH_SHORT).show();
                        saveMyBitmap("save_pic", createViewBitmap(savePic));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    String codeUrl = "";
    private int codeId = 0;


    //权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLDialog.setDialogText("正在保存图片...");
//                    mLDialog.show();
                    Toast.makeText(ShareProjectActivity.this, "正在保存图片", Toast.LENGTH_SHORT).show();
                    try {
                        saveMyBitmap("save_pic", createViewBitmap(savePic));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tv_save.setClickable(true);
                    Toast.makeText(ShareProjectActivity.this, "请先开启读写权限", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    //使用IO流将bitmap对象存到本地指定文件夹
    public void saveMyBitmap(final String bitName, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filePath + "/DCIM/Camera/" + bitName + ".png");
                try {
                    file.createNewFile();


                    FileOutputStream fOut = null;
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    bitmap.recycle();
                    Message msg = Message.obtain();
                    msg.obj = file.getPath();
                    handler.sendMessage(msg);
                    //Toast.makeText(PayCodeActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String picFile = (String) msg.obj;
            String[] split = picFile.split("/");
            String fileName = split[split.length - 1];
            try {
                MediaStore.Images.Media.insertImage(getApplicationContext()
                        .getContentResolver(), picFile, fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + picFile)));
            Toast.makeText(ShareProjectActivity.this, "图片保存图库成功", Toast.LENGTH_LONG).show();
//            if (mLDialog != null && mLDialog.isShowing()) {
//                mLDialog.dismiss();
//            }
            tv_save.setClickable(true);
        }
    };


//将要存为图片的view传进来 生成bitmap对象

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
