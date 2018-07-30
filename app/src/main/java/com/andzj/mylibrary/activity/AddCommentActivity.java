package com.andzj.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.bean.BookInformation;
import com.andzj.mylibrary.bean.BookResult;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.ImageLoader;
import com.andzj.mylibrary.util.MyLog;

/**
 * Created by zj on 2016/11/23.
 */

public class AddCommentActivity extends BaseActivity implements View.OnClickListener{

    private ImageView bookImageView;
    private TextView bookIsbnView;
    private TextView bookNameView;
    private TextView bookAuthorView;
    private RatingBar bookScoreBar;
    private TextView bookScoreNumberView;
    private RatingBar commentScoreBar;
    private EditText commentEdit;
    private Button submitBtn;

    private String bookIsbn;
    private UserAccount userAccount;
    private ProgressDialog progressDialog;

    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_comment);
        imageLoader = MainActivity.getImageLoader(AddCommentActivity.this);
        bookImageView =         (ImageView) findViewById(R.id.book_image_view);
        bookIsbnView =          (TextView) findViewById(R.id.book_isbn_view);
        bookNameView =          (TextView) findViewById(R.id.book_name_view);
        bookAuthorView =        (TextView) findViewById(R.id.book_author_view);
        bookScoreBar =          (RatingBar) findViewById(R.id.book_score_bar);
        bookScoreNumberView =   (TextView) findViewById(R.id.book_score_number_view);
        commentScoreBar =       (RatingBar) findViewById(R.id.comment_score_bar);
        commentEdit =           (EditText) findViewById(R.id.comment_edit);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);


        bookIsbn = getIntent().getStringExtra("book_isbn");
        if (bookIsbn != null)
        {
            MyNetwork.createHttpConnect(MyNetwork.Address_Search_Book,"mode=isbn&search_words="+bookIsbn,MyNetwork.NET_SEARCH_BOOK,handler);
        }
        else
        {
            BookInformation bookInformation = getIntent().getParcelableExtra("book");
            if (bookInformation != null)
            {
                bookIsbn = bookInformation.getBookIsbn();
                showBookDetail(bookInformation);
            }
            else
            {
                Toast.makeText(AddCommentActivity.this,"程序出错",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        userAccount = MainActivity.getUserAccount(AddCommentActivity.this);
        if (userAccount == null)
        {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_btn:
            {
                String commentContentStr = commentEdit.getText().toString();
                if ("".equals(commentContentStr))
                {
                    Toast.makeText(AddCommentActivity.this,"请输入评论内容",Toast.LENGTH_SHORT).show();
                    break;
                }
                else if (commentContentStr.length() > 200)
                {
                    Toast.makeText(AddCommentActivity.this,"评论字数超过200字,请精简后提交",Toast.LENGTH_SHORT).show();
                    break;
                }
                String s = "book_isbn=" + bookIsbn + "&comment_account_name=" + userAccount.getAccountName() + "&comment_content=" + commentContentStr + "&score=" + String.valueOf(commentScoreBar.getRating());
                progressDialog = new ProgressDialog(AddCommentActivity.this);
                progressDialog.setTitle("正在提交您的精彩评论");
                progressDialog.setMessage("请稍候...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                MyNetwork.createHttpConnect(MyNetwork.Address_Comment_Book,s,MyNetwork.NET_COMMENT_BOOK,handler);

                break;
            }
            default:
                break;
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_SEARCH_BOOK:
                {
                    BookResult bookResult = JSON.parseObject((String)message.obj, BookResult.class);
                    String info = bookResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_BOOK) + info,false);
                    if ("Match".equals(info))
                    {
                        BookInformation bookInformation = bookResult.getData().get(0);
                        showBookDetail(bookInformation);
                    }
                    else if ("NotMatch".equals(info))
                    {
                        Toast.makeText(AddCommentActivity.this,"没有找到这本书",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AddCommentActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_COMMENT_BOOK:
                {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info1 = jsonObject.getString("info1");
                    if ("CommentAddSuccess".equals(info1))
                    {
                        String info2 = jsonObject.getString("info2");
                        if ("ScoreUpdateSuccess".equals(info2) || "ScoreAddSuccess".equals(info2))
                        {
                            Toast.makeText(AddCommentActivity.this,"评论提交成功",Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                    }
					else
					{
						String info = jsonObject.getString("info");
						if ("BookIsbnNotExisted".equals(info))
						{
							Toast.makeText(AddCommentActivity.this,"此书已不存在,无法提交评价",Toast.LENGTH_SHORT).show();
						}
					}

                    Toast.makeText(AddCommentActivity.this,"评论失败",Toast.LENGTH_SHORT).show();

                    break;
                }
                case MyNetwork.NET_ERROR:
                {
                    String e = (String) message.obj;
                    MyLog.e("NET_Error", e, false);
                    Toast.makeText(AddCommentActivity.this, "网络错误:" + e, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private void showBookDetail(BookInformation bookInformation)
    {
        //bookImageView

        String uri = bookInformation.getBookImageAddress();
        if (uri != null)
        {
            imageLoader.bindBitmap(ImageLoader.getDownloadUrlWithTime(MyNetwork.Address_Access_File + uri,bookInformation.getOperateTime()),bookImageView);
        }
        bookIsbnView.setText(bookInformation.getBookIsbn());
        bookNameView.setText(bookInformation.getBookName());
        bookAuthorView.setText(bookInformation.getBookAuthor());
        bookScoreBar.setRating(bookInformation.getBookAverageScore().floatValue());
        bookScoreNumberView.setText(String.valueOf(bookInformation.getBookScoreNumber()));

    }

    public static void actionStart(Context context, String bookIsbn)
    {
        Intent intent = new Intent(context,AddCommentActivity.class);
        intent.putExtra("book_isbn",bookIsbn);
        context.startActivity(intent);
    }

    public static void actionStart(Context context,BookInformation bookInformation)
    {
        Intent intent = new Intent(context,AddCommentActivity.class);
        intent.putExtra("book",bookInformation);
        context.startActivity(intent);
    }
}
