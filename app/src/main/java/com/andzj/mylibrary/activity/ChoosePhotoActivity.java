package com.andzj.mylibrary.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyDialogUtils;
import com.andzj.mylibrary.util.MyFileOperateUtils;
import com.andzj.mylibrary.util.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zj on 2016/7/25.
 */
public class ChoosePhotoActivity extends BaseActivity implements View.OnClickListener
{
    //public static final int MODE_BOOK       = 30;
    //public static final int MODE_ACCOUNT    = 31;

    public static final String DIR_TEMP_IMAGE       = "/image/temp_image/temp.jpg";
    public static final String DIR_HEAD_IMAGE       = "/image/head_image/head.jpg";

    private Button chooseFromPhotoAlbumBtn;
    private Button takeAPhotoBtn;
    private ImageView pictureView;
    private Button confirmBtn;

    public static final int TAKE_PHOTO   = 20;
    public static final int CROP_PHOTO   = 21;
    public static final int CHOOSE_PHOTO = 22;



    private Uri imageUri;
    //private Uri cropImageUri;
    private Bitmap bitmap;

    private ProgressDialog progressDialog = null;
    private String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_choose_photo);

        chooseFromPhotoAlbumBtn = (Button) findViewById(R.id.choose_from_photo_album_btn);
        chooseFromPhotoAlbumBtn.setOnClickListener(this);
        takeAPhotoBtn = (Button) findViewById(R.id.take_a_photo_btn);
        takeAPhotoBtn.setOnClickListener(this);
        pictureView = (ImageView) findViewById(R.id.picture_view);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setVisibility(View.GONE);

        accountName = getIntent().getStringExtra("account_name");

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.choose_from_photo_album_btn:
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);

                MyLog.d("ChoosePhotoActivity","点击了 从相册中选择",false);
                //Toast.makeText(this,"此功能有bug,暂不建议使用",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.take_a_photo_btn:
            {
                File tempImage = MyFileOperateUtils.getWritableEmptyExternalFile(this,DIR_TEMP_IMAGE);
                if (tempImage != null)
                {
                    imageUri = Uri.fromFile(tempImage);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    try
                    {
                        startActivityForResult(intent,TAKE_PHOTO);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(ChoosePhotoActivity.this,"请授予相机和存储权限",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                //MyLog.d("ChoosePhotoActivity","点击了 随手拍");
                //Toast.makeText(this,"此功能有bug,暂不建议使用",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.confirm_btn:
                File headImage = MyFileOperateUtils.getWritableEmptyExternalFile(this,DIR_HEAD_IMAGE);
                if (headImage != null)
                {
                    try
                    {
                        OutputStream stream = new FileOutputStream(headImage);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                        progressDialog = MyDialogUtils.progressDialogShow(ChoosePhotoActivity.this,"正在上传头像","请稍候...",false,false,null);
                        Map<String,String> map = new HashMap<>();
                        map.put("account_name",accountName);
                        File imageFile = MyFileOperateUtils.getReadableExternalFile(ChoosePhotoActivity.this,DIR_HEAD_IMAGE);
                        if (imageFile != null)
                        {
                            MyNetwork.uploadImage(MyNetwork.Address_Upload_Image,map,imageFile,MyNetwork.NET_UPLOAD_IMAGE,handler);
                        }
                        else
                        {
                            Toast.makeText(ChoosePhotoActivity.this,"图片读取失败,请返回重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        MyLog.e("ChoosePhotoActivity","headImage == null",false);
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(this,"无法本地存储头像",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                MyLog.e("ChoosePhotoActivity","Error clicked");
                break;
        }
    }

    public Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_UPLOAD_IMAGE:
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_UPLOAD_IMAGE) + info,false);
                    if ("UpdateSuccess".equals(info))
                    {
                        String imageInfo = jsonObject.getString("image");
                        if ("SaveSuccess".equals(imageInfo))
                        {
                            UserAccount userAccount = jsonObject.getObject("user_account",UserAccount.class);
                            MainActivity.setUserAccount(userAccount);
                            Toast.makeText(ChoosePhotoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if ("SaveError".equals(imageInfo))
                        {
                            Toast.makeText(ChoosePhotoActivity.this,"上传失败",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(ChoosePhotoActivity.this,"返回数据出错(请联系管理员解决此问题):" + imageInfo,Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if ("UpdateError".equals(info))
                    {
                        Toast.makeText(ChoosePhotoActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChoosePhotoActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(ChoosePhotoActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        //MyLog.d("ChoosePhotoActivity","requestCode = " + String.valueOf(requestCode),false);
        switch (requestCode)
        {
            case TAKE_PHOTO: //拍摄完成
                if (resultCode == RESULT_OK)
                {
                    Toast.makeText(ChoosePhotoActivity.this,"拍照完成,对照片进行裁剪",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri,"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,CROP_PHOTO);
                }
                break;
            case CROP_PHOTO: //裁剪完成
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Toast.makeText(ChoosePhotoActivity.this,"裁剪完成",Toast.LENGTH_SHORT).show();
                        pictureView.setImageBitmap(bitmap);
                        confirmBtn.setVisibility(View.VISIBLE);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(ChoosePhotoActivity.this,"无法获取图片,请授予相机和存储权限",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        MyLog.e("ChoosePhotoActivity","Error No Photo");
                    }
                }
                break;
            case CHOOSE_PHOTO: //从相册中选择完成
                if (resultCode == RESULT_OK)
                {
                    if (Build.VERSION.SDK_INT >= 19)
                    {
                        handleImageOnKitKat(data);
                    }
                    else
                    {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }


    //从手机相册中选择
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data)
    {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri))
        {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority()))
            {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority()))
            {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            imagePath = getImagePath(uri,null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data)
    {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection)
    {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath)
    {
        if (imagePath != null)
        {
            //MyLog.d("ChooseImagePath:", imagePath);
            try
            {
                //bitmap = BitmapFactory.decodeFile(imagePath);
                //pictureView.setImageBitmap(bitmap);
                //confirmBtn.setVisibility(View.VISIBLE);

                File copyImageFile = MyFileOperateUtils.getWritableEmptyExternalFile(this, DIR_TEMP_IMAGE);
                if (copyImageFile != null)
                {

                    Bitmap copyBitmap = BitmapFactory.decodeFile(imagePath);
                    OutputStream stream = new FileOutputStream(copyImageFile);
                    copyBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    imageUri = Uri.fromFile(copyImageFile);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri,"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,CROP_PHOTO);
                    Toast.makeText(ChoosePhotoActivity.this,"对照片进行裁剪",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChoosePhotoActivity.this,"无法存取照片,内存不足?或授予相机和存储权限",Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Toast.makeText(ChoosePhotoActivity.this,"无法获取图片,请授予相机和存储权限",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }




    public static void actionStart(Context context,String accountName)
    {
        Intent intent = new Intent(context,ChoosePhotoActivity.class);
        intent.putExtra("account_name",accountName);
        context.startActivity(intent);
    }

    public static void actionStart(Context context,String oldImageName,int mode)
    {
        Intent intent = new Intent(context,ChoosePhotoActivity.class);
        intent.putExtra("old_image_name",oldImageName);
        intent.putExtra("mode",mode);
        context.startActivity(intent);
    }
}
