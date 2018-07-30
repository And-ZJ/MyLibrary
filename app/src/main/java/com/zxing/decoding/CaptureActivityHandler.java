/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.decoding;

import com.alibaba.fastjson.JSON;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.activity.BookDetailActivity;
import com.andzj.mylibrary.activity.ScanBarCodeActivity;
import com.andzj.mylibrary.activity.SearchBookActivity;
import com.andzj.mylibrary.bean.BookResult;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.MyLog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zxing.camera.CameraManager;
import com.zxing.view.*;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

  @SuppressWarnings("unused")
private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final ScanBarCodeActivity activity;
  private final DecodeThread decodeThread;
  private State state;

    String str_result;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public CaptureActivityHandler(ScanBarCodeActivity activity, Vector<BarcodeFormat> decodeFormats,
                                String characterSet) {
    this.activity = activity;
    decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
        new ViewfinderResultPointCallback(activity.getViewfinderView()));
    decodeThread.start();
    state = State.SUCCESS;

    // Start ourselves capturing previews and decoding.
    CameraManager.get().startPreview();
    restartPreviewAndDecode();
  }

  @Override
  
  /*
   *	扫描结果的消息处理 
   */
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.auto_focus:
        if (state == State.PREVIEW) {
          CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
        break;
      case R.id.restart_preview:
        restartPreviewAndDecode();
        break;
      //扫描成功的消息
      case R.id.decode_succeeded:
        state = State.SUCCESS;
        
        Bundle bundle = message.getData();
        Bitmap barcode = bundle == null ? null :(Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
        
        str_result=((Result) message.obj).getText();
        activity.handleDecode((Result) message.obj, barcode);

          MyLog.d("Bar Code:",str_result,false);

          //new SearchTask().execute(str_result);
          //获取到ISBN码后返回到主Activity
    	//Intent intent=new Intent(activity, ScanResultActivity.class);
        //intent.putExtra("bar_code",str_result);
		//intent.putExtra("result", str_result);
		//activity.setResult(100,intent);
          // activity.startActivity(intent);
		//activity.finish();
          activity.onPause();
          if (!MyNetwork.checkNetworkAvailable())
          {
              Toast.makeText(activity,"无可用网络",Toast.LENGTH_SHORT).show();
              activity.finish();
              return;
          }
          progressDialog = new ProgressDialog(activity);
          progressDialog.setTitle("扫描结果:"+ str_result);
          progressDialog.setMessage("正在查询请稍等...");
          progressDialog.setCancelable(true);
          progressDialog.setCanceledOnTouchOutside(false);
          progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialog) {
                  cancelSearch = true;
                  activity.finish();
              }
          });
          progressDialog.show();
          String s = "mode=isbn&search_words=" + str_result;
          MyNetwork.createHttpConnect(MyNetwork.Address_Search_Book,s,MyNetwork.NET_SEARCH_BOOK,searchHandler);

        break;
        
      case R.id.decode_failed:
        Log.i("OUTPUT", "Got return scan result message");
        state = State.PREVIEW;
        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        break;
      case R.id.return_scan_result:
        Log.i("OUTPUT", "Got return scan result message");
    	//Intent intent2=new Intent(activity,MainActivity.class);
		//activity.startActivity(intent2);
        break;
    }
  }

    private ProgressDialog progressDialog;
    private boolean cancelSearch = false;

    public Handler searchHandler = new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_SEARCH_BOOK:
                    progressDialog.dismiss();
                    if (cancelSearch)
                        return;
                    BookResult bookResult = JSON.parseObject((String)message.obj, BookResult.class);
                    String info = bookResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_BOOK) + info,false);
                    if ("Match".equals(info))
                    {
                        MyLog.d("CaptureActivityHandler","Match",false);
                        BookDetailActivity.actionStart(activity, BookDetailActivity.MODE_AllMsg,bookResult.getData().get(0),null);
                        activity.finish();
                    }
                    else if ("NotMatch".equals(info))
                    {
                        Toast.makeText(activity,"没有找到哎,换个姿势试试?",Toast.LENGTH_SHORT).show();
                        activity.onResume();
                    }
                    else
                    {
                        Toast.makeText(activity,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    progressDialog.dismiss();
                    if (cancelSearch)
                        return;
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(activity,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    };


  public void quitSynchronously() {
    state = State.DONE;
    CameraManager.get().stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
    quit.sendToTarget();
    try {
      decodeThread.join();
    } catch (InterruptedException e) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded);
    //removeMessages(R.id.return_scan_result);
    removeMessages(R.id.decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
      CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
      CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
      activity.drawViewfinder();
    }
  }


//    private boolean searchRequest = false;
//
//    private class SearchTask extends AsyncTask<String,Void,Boolean>
//    {
//
//        Book book;
//        String bar_code;
//        //private ProgressDialog progressDialog;
//
//        private SearchProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            activity.onPause();
//            searchRequest = true;
//            progressDialog = new SearchProgressDialog(activity);
//            progressDialog.setTitle("扫描结果:"+ str_result);
//            progressDialog.setMessage("正在查询请稍等...");
//            progressDialog.setCancelable(true);
//            progressDialog.show();
////            progressDialog = new ProgressDialog(activity);
////            progressDialog.setTitle("扫描结果:"+ str_result);
////            progressDialog.setMessage("正在查询请等待...");
////            progressDialog.setCancelable(true);
////            progressDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            bar_code = params[0];
//            switch(ScanBarCodeActivity.currentMode)
//            {
//                case ScanBarCodeActivity.MODE_SEARCH_DATABASE:
//                    book = BookDataBaseHelper.queryBookByIsbn(bar_code);
//                    if (book != null)
//                    {
//                        return true;
//                    }
//                    break;
//                case ScanBarCodeActivity.MODE_SEARCH_INTERNET:
////                    Toast.makeText(activity,"联网查书功能暂未实现",Toast.LENGTH_SHORT).show();
//                    return true;
//            }
//            return false;
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(Boolean resultOk) {
//            progressDialog.dismiss();
//            if (searchRequest)
//            {
//                switch(ScanBarCodeActivity.currentMode)
//                {
//                    case ScanBarCodeActivity.MODE_SEARCH_DATABASE:
//                        if (resultOk)
//                        {
//                            searchRequest = false;
//                            BookDetailActivity.actionStart(activity, BookDetailActivity.MODE_BookMsg,book);
//                            activity.finish();
//                        }
//                        else
//                        {
//                            Toast.makeText(activity,"没有查询到,请重新扫描或向我们反馈",Toast.LENGTH_SHORT).show();
//                            searchRequest = false;
//                            activity.onResume();
//                        }
//                        break;
//                    case ScanBarCodeActivity.MODE_SEARCH_INTERNET:
//                        if (resultOk)
//                        {
//                            Toast.makeText(activity,"联网查书功能暂未实现,已自动补上ISBN号",Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent();
//                            intent.putExtra("bar_code",bar_code);
//                            activity.setResult(Activity.RESULT_OK,intent);
//                            searchRequest = false;
//                            activity.finish();
//                        }
//                        break;
//                    default:
//                        searchRequest = false;
//                        activity.finish();
//                        break;
//                }
//
//            }
//            else
//            {
//                searchRequest = false;
//                activity.onResume();
//            }
//        }
//    }
}
