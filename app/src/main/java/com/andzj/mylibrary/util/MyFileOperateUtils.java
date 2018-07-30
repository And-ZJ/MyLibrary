package com.andzj.mylibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.service.carrier.CarrierService;
import android.widget.EditText;
import android.widget.Toast;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.activity.ChoosePhotoActivity;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by zj on 2016/8/25.
 */
public class MyFileOperateUtils
{
    //检查外部存储可读写
    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        Toast.makeText(MyApplication.getContext(),"储存器不可写",Toast.LENGTH_SHORT).show();
        return false;
    }

    //检查外部存储至少可读
    public static boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        Toast.makeText(MyApplication.getContext(),"储存器不可读",Toast.LENGTH_SHORT).show();
        return false;
    }

    //说明:在外部存储器的私有目录下创建多级文件夹,以保证多级文件夹下的文件能正常读写
    //参数:context:必要的上下文对象
    //参数:fileName:包含完整路径名和文件名的字符串
    //返回值:是否创建成功
    private static boolean createExternalFileDir(Context context,String fileName) {
        if (isExternalStorageWritable())
        {
            if (fileName != null && !"".equals(fileName))
            {
                String fileDirStr;
                String s = fileName;
                int p = s.lastIndexOf("/");
                if (p >= 0)
                {
                    fileDirStr = s.substring(0,p + 1);
                    File fileWholeDir = new File(context.getExternalFilesDir(null), fileDirStr);
                    if (!fileWholeDir.exists())
                    {
                        p = s.indexOf("/");
                        fileDirStr = s.substring(0, p + 1);
                        s = s.substring(p + 1);
                        p = s.indexOf("/");
                        do
                        {
                            fileDirStr = fileDirStr + s.substring(0, p + 1);
                            s = s.substring(p + 1);
                            try
                            {
                                File fileDir = new File(context.getExternalFilesDir(null), fileDirStr);
                                if (!fileDir.exists())
                                {
                                    if (!fileDir.mkdir())
                                    {
                                        return false;
                                    }
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            //MyLog.d("MyFileOperateUtils", "fileDirStr = " + fileDirStr,false);
                            p = s.indexOf("/");
                        } while (p >= 0);
                    }
                    MyLog.d("MyFileOperateUtils", "fileDirStr = " + fileName,false);
                }
                return  true;
            }
        }
        return false;
    }

    public static File getWritableEmptyExternalFile(Context context, String fileName)
    {
        if (createExternalFileDir(context,fileName))
        {
            try
            {
                File file = new File(context.getExternalFilesDir(null),fileName);
                if (file.exists())
                {
                    file.delete();
                }
                file.createNewFile();
                MyLog.d("MyFileOperateUtils",file.getPath(),false);
                return file;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getWritableExternalFile(Context context,String fileName)
    {
        if (isExternalStorageWritable())
        {
            try
            {
                File file = new File(context.getExternalFilesDir(null),fileName);
                if (file.exists())
                {
                    return file;
                }
                else
                {
                    if (createExternalFileDir(context,fileName))
                    {
                        file.createNewFile();
                        return file;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getReadableExternalFile(Context context, String fileName)
    {
        if (fileName == null || "".equals(fileName))
        {
            return null;
        }
        if (isExternalStorageReadable())
        {
            try
            {
                File file = new File(context.getExternalFilesDir(null),fileName);
                if (file.exists())
                {
                    return file;
                }
                MyLog.d("MyFileOperateUtils","找不到文件:" + fileName,false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //Toast.makeText(context,"找不到文件",Toast.LENGTH_SHORT).show();
        return null;
    }

    @Deprecated
    public static File getReadableExternalFile(Context context, File fileName)
    {
        if (isExternalStorageReadable())
        {
            try
            {
                if (fileName.exists())
                {
                    return fileName;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        MyLog.d("MyFileOperateUtils","找不到文件:" + fileName,false);
        return null;
    }

    @Deprecated
    public static Bitmap readBitmapFromExternalFile(Context context, File bitmapFileName)
    {
        if (bitmapFileName.exists())
        {
            try
            {
                return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(bitmapFileName)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        MyLog.d("MyFileOperateUtils","文件不存在:" + bitmapFileName,false);
        return null;
    }


    public static Bitmap readBitmapFromExternalFile(Context context,String bitmapFileNameStr)
    {
        if (bitmapFileNameStr != null && !"".equals(bitmapFileNameStr))
        {
            File file = getReadableExternalFile(context,bitmapFileNameStr);
            if (file != null)
            {
                try
                {
                    return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(file)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        MyLog.d("MyFileOperateUtils","图像文件名为空",false);
        return null;
    }

    private static boolean isExternalFileExists(Context context,String fileName)
    {
        if (getReadableExternalFile(context,fileName) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Uri getUri(Context context, String fileDir)
    {
        File file = getWritableExternalFile(context,fileDir);
        return Uri.fromFile(file);
    }


    public static Bitmap getBitmapForBookImage(Context context, String bookImageName)
    {
        Bitmap bitmap = readBitmapFromExternalFile(context,bookImageName);
        if (bitmap == null)
        {
            //MyLog.d("MyFileOperateUtils","bitmap == null",false);
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_book_image);
        }
        return bitmap;
    }

    public static Bitmap getBitmapForAccountImage(Context context,String accountImageName)
    {
        Bitmap bitmap = readBitmapFromExternalFile(context,accountImageName);
        if (bitmap == null)
        {
            //MyLog.d("MyFileOperateUtils","bitmap == null",false);
            return BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        }
        return bitmap;
    }

    public static boolean saveStringDate(Context context, String saveContent, String fileName, boolean append)
    {
        if (context == null || saveContent == null || fileName == null)
        {
            return false;
        }
        File file = getWritableExternalFile(context,fileName);
        if (file == null)
        {
            return false;
        }
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(file,append);
            outputStream.write(saveContent.getBytes());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (outputStream !=null )
                {
                    outputStream.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String readStringData(Context context,String fileName)
    {
        if (context == null || fileName == null)
        {
            //MyLog.d("SearchBookActivity","context|fileName == null",false);
            return null;
        }
        File file = getReadableExternalFile(context,fileName);
        if (file == null)
        {
            //MyLog.d("SearchBookActivity","file == null",false);
            return null;
        }
        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            MyLog.d("SearchBookActivity","bytes length =" + String.valueOf(bytes.length),false);
            if (inputStream.read(bytes) != -1)
            {
                //MyLog.d("MyFileOperateUtils","s = " + s,false);
                return new String(bytes);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static boolean saveHistory(Context context,String saveContent,String fileName)
//    {
//        File file = getWritableExternalFile(context,fileName);
//        if (file == null)
//        {
//            return false;
//        }
//        FileOutputStream outputStream = null;
//        try
//        {
//            outputStream = new FileOutputStream(file);
//            outputStream.write(saveContent.getBytes());
//            outputStream.close();
//            return true;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
