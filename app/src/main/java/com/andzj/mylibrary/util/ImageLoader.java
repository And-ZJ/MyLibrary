package com.andzj.mylibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.andzj.mylibrary.R;
import com.andzj.mylibrary.net.MyNetwork;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import llibcore.io.DiskLruCache;

/**
 * Created by zj on 2016/11/28.
 */

public class ImageLoader {
    private static Map<String,String> uriKeyMap = new HashMap<>();

    private static String getUriKey(String uri)
    {
        if (uriKeyMap.size() >= 200)
        {
            uriKeyMap.clear();
        }
        if (uriKeyMap.containsKey(uri))
        {
            return uriKeyMap.get(uri);
        }
        else
        {
            String uriKey = hashKeyFromUrl(uri);
            uriKeyMap.put(uri,uriKey);
            return uriKey;
        }
    }

    private static final String TAG = "ImageLoader";

    public static final int MESSAGE_POST_RESULT = 1;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT*2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int TAG_KEY_URI = R.id.book_image_view;
    private static final long DISK_CACHE_SIZE = 1024*1024*20;
    private static final int IO_BUFFER_SIZE = 10*1024;
    private static final int DISK_CACHE_INDEX = 0;
    private boolean mIsDiskLruCacheCreated = false;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            //System.out.println("uri:        " + uri);
            //System.out.println("result_uri: " + result.uri);
            if (uri.equals(result.uri))
            {
                if (result.bitmap != null)
                {
                    imageView.setImageBitmap(result.bitmap);
                }
            }
            else
            {
                //MyLog.d(TAG,"set image bitmap,but url has changed,ignored!");
            }

        }
    };

    private Context mContext;
    private ImageResize mImageResize = new ImageResize();
    private LruCache<String,Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ImageLoader(Context context)
    {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory())/1024;
        int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

        File diskCacheDir = getDiskCacheDir(mContext,"bitmap");
        if (!diskCacheDir.exists())
        {
            diskCacheDir.mkdirs();
        }

        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE)
        {
            try
            {
                mDiskLruCache = DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    public static ImageLoader builder(Context context)
    {
        return new ImageLoader(context);
    }

    private void addBitmapToMemoryCache(String key,Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null)
        {
            mMemoryCache.put(key,bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key)
    {
        return mMemoryCache.get(key);
    }

    public void bindBitmap(final String uri,final ImageView imageView)
    {
        bindBitmap(uri,imageView,0,0);
    }

    //传递过来的uri是一个图片地址,并且地址后面携带时间参数,用于更新图片
    public void bindBitmap(final String uri, final ImageView imageView,final int reqWidth,final int reqHeight)
    {
        imageView.setTag(TAG_KEY_URI,uri);//使用url作tag
        Bitmap bitmap = loadBitmapFromMemCache(uri);//使用hash值存储图片
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);//在内存缓存中找到了图片,直接设置图片
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri,reqWidth,reqHeight);//在内存缓存中没有找到图片,使用线程从内存缓存 磁盘缓存或网络获取图片
                if (bitmap != null)
                {
                    LoaderResult result = new LoaderResult(imageView,uri,bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                }

            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    public Bitmap loadBitmap(String uri,int reqWidth,int reqHeight)
    {
        Bitmap bitmap = loadBitmapFromMemCache(uri);//从内存缓存中读取
        if (bitmap != null)
        {
            MyLog.d(TAG,"LoadBitmapFromMemCache,url:" + uri);
            return bitmap;
        }

        try
        {
            bitmap = loadBitmapFromDiskCache(uri,reqWidth,reqHeight);//从磁盘缓存中读取
            if (bitmap != null)
            {
                MyLog.d(TAG,"loadBitmapFromDisk,url:" + uri);
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(uri,reqWidth,reqHeight);//从网络下载并存到磁盘缓存

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (bitmap == null && !mIsDiskLruCacheCreated)//如果下载失败 并且没有磁盘缓存
        {
            MyLog.w(TAG,"encounter error,DiskLruCache is not created.");
            bitmap = downloadBitmapFromUrl(uri);
        }
        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String url)
    {
        return  getBitmapFromMemCache(getUriKey(url));
//        final String key = hashKeyFromUrl(url);
//        Bitmap bitmap = getBitmapFromMemCache(key);
//        return bitmap;
    }

    private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws IOException
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            throw new RuntimeException("can not visit network from UI Thread.");
        }

        if (mDiskLruCache == null)
        {
            return null;
        }

        //String key = hashKeyFromUrl(url);
        //DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        DiskLruCache.Editor editor = mDiskLruCache.edit(getUriKey(url));
        if (editor != null)
        {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url,outputStream))//从网络下载后,存储到磁盘缓存区,再返回磁盘缓存区的图片
            {
                MyLog.d(TAG,"loadBitmapFromHttp,url:" + url );
                editor.commit();
            }
            else
            {
                editor.abort();
            }
            mDiskLruCache.flush();
            return loadBitmapFromDiskCache(url,reqWidth,reqHeight);
        }
        return null;
    }

    private Bitmap loadBitmapFromDiskCache(String url,int reqWidth,int reqHeight) throws IOException
    {
        if (mDiskLruCache == null)//没有开辟磁盘缓存
        {
            return null;
        }
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            MyLog.w(TAG,"load bitmap from UI Thread,it's not recommended!");
        }

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getUriKey(url));
        //String key = hashKeyFromUrl(url);
        //DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null)
        {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mImageResize.decodeSampledBitmapFromFileDescriptor(fileDescriptor,reqWidth,reqHeight);
            if (bitmap != null)
            {
                //addBitmapToMemoryCache(key,bitmap);
                addBitmapToMemoryCache(getUriKey(url),bitmap);//从磁盘缓存加载后,保存到内存缓存中
            }

        }
        return bitmap;
    }


    private boolean downloadUrlToStream(String urlString,OutputStream outputStream)
    {
        if (MyNetwork.checkMobileNetWork() && !SystemSet.isAutoReceivedPictureSwitch())
        {
            MyLog.w(TAG,"Mobile Type Forbid Download");
            return false;
        }
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try
        {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream,IO_BUFFER_SIZE);
            int b;
            while ((b=in.read()) != -1)
            {
                out.write(b);
            }
            return true;
        }
        catch (IOException e)
        {
            MyLog.e(TAG,"downloadBitmap failed." + e);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
//            MyUtils.close(out);
//            MyUtils.close(in);
        }
        return false;
    }


    public Bitmap downloadBitmapFromUrl(String urlString)
    {
        if (MyNetwork.checkMobileNetWork() && !SystemSet.isAutoReceivedPictureSwitch())
        {
            MyLog.w(TAG,"Mobile Type Forbid Download");
            return null;
        }
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try
        {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);//直接下载图片
            if (bitmap != null)
            {
                //addBitmapToMemoryCache(key,bitmap);
                addBitmapToMemoryCache(getUriKey(urlString),bitmap);//从磁盘缓存加载后,保存到内存缓存中
            }
        }
        catch (final IOException e)
        {
            MyLog.e(TAG,"Error in downloadBitmap:" + e);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //MyUtils.close(in);
        }
        return bitmap;
    }

    private static String hashKeyFromUrl(String url)
    {
        String cacheKey;
        try
        {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;


    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<bytes.length;i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
            {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private File getDiskCacheDir(Context context,String uniqueName)
    {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable)
        {
            cachePath = context.getExternalCacheDir().getPath();
        }
        else
        {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    //@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path)
    {
        return path.getUsableSpace();
//        final StatFs stats = new StatFs(path.getPath());
//        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    private static class LoaderResult{
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView,String uri,Bitmap bitmap)
        {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }

    private static StringBuffer urlBuffer = new StringBuffer();

    public static String getDownloadUrlWithTime(String uri,String time)
    {
        urlBuffer.delete(0,urlBuffer.length());
        urlBuffer.append(uri);
        try
        {
            urlBuffer.append("?time=").append(URLEncoder.encode(time,"UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return urlBuffer.toString();
    }

}
