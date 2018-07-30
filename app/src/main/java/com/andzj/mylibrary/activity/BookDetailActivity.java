package com.andzj.mylibrary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.adapter.BorrowMsgListAdapter;
import com.andzj.mylibrary.adapter.CommentContentMsgListAdapter;
import com.andzj.mylibrary.adapter.MyViewPagerAdapter;
import com.andzj.mylibrary.adapter.ScoreMsgListAdapter;
import com.andzj.mylibrary.bean.BookInformation;
import com.andzj.mylibrary.bean.BookResult;
import com.andzj.mylibrary.bean.BorrowInformation;
import com.andzj.mylibrary.bean.BorrowResult;
import com.andzj.mylibrary.bean.CommentInformation;
import com.andzj.mylibrary.bean.CommentResult;
import com.andzj.mylibrary.bean.ScoreInformation;
import com.andzj.mylibrary.bean.ScoreResult;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.model.MainTitleLayout;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.ImageLoader;
import com.andzj.mylibrary.util.MyDialogUtils;
import com.andzj.mylibrary.util.MyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2016/11/18.
 */

public class BookDetailActivity extends BaseActivity implements View.OnClickListener{

    public static final int MODE_AllMsg = 50;
    public static final int MODE_BorrowMsg = 51;
    public static final int MODE_CommentMsg = 52;
    //public static final int MODE_BorrowHistoryMsg = 53;
    public int currentMode = MODE_AllMsg ;
    private int currentViewPager = 0;


    private MainTitleLayout titleLayout;
    private LinearLayout choosePageLayout;
    private Button bookMsgBtn;
    private Button borrowMsgBtn;
    private Button commentMsgBtn;
    private ViewPager msgViewPager;
    private LinearLayout operateBookLayout;
    private Button borrowBookBtn;
    private Button returnBookBtn;
    private Button shareBookBtn;
    private MyViewPagerAdapter msgViewPagerAdapter;
    private List<View> msgViewList = new ArrayList<>();

    private String uploadStr = "";

    private UserAccount userAccount;

    private ImageLoader imageLoader;

    private ProgressDialog progressDialog;
//    private boolean isAccessingNetwork = false;
//    private boolean isCancelAccessingNetwork = false;

    //第一页
    private ImageView bookImageView;
    private TextView bookIsbnView;
    private TextView bookNameView;
    private TextView bookAuthorView;
    private RatingBar bookScoreBar;
    private TextView bookScoreNumberView;
    private TextView bookSummary;
    private TextView bookTotalNumberView;
    private TextView bookRemainNumberView;
    private TextView bookPriceView;
    private TextView bookPublishCompanyView;
    private TextView bookPublishTimeView;
    private TextView bookPositionView;
    private TextView bookKeyWordsView;
    private TextView bookNotesView;
    private BookInformation bookInformation;

    //第二页
    private LinearLayout borrowNumberLayout;
    private TextView borrowHintWordsView;
    private TextView borrowNumberView;
    private TextView noBorrowMsgHintView;


    private ListView borrowMsgListView;
    private List<BorrowInformation> borrowMsgList = new ArrayList<>();
    private BorrowMsgListAdapter borrowMsgListAdapter;

    //第三页
    private TextView noCommentMsgHintView;
    private LinearLayout commentNumberLayout;
    private TextView commentNumberView;
    private ListView scoreMsgListView;
    private List<ScoreInformation> scoreMsgList = new ArrayList<>();
    private ScoreMsgListAdapter scoreMsgListAdapter;
    private Map<String,List<CommentInformation>> commentMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_book_detail);
        imageLoader = MainActivity.getImageLoader(BookDetailActivity.this);
        progressDialog = new ProgressDialog(BookDetailActivity.this);
        currentMode = getIntent().getIntExtra("mode",MODE_AllMsg);
        titleLayout = (MainTitleLayout) findViewById(R.id.title);
        choosePageLayout = (LinearLayout) findViewById(R.id.choose_page_layout);
        operateBookLayout = (LinearLayout) findViewById(R.id.operate_book_btn_layout);

        loadPage();
        msgViewPagerAdapter = new MyViewPagerAdapter(msgViewList);
        msgViewPager = (ViewPager) findViewById(R.id.msg_view_pager);
        msgViewPager.setAdapter(msgViewPagerAdapter);
        msgViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if (currentViewPager != position)
                {
                    cancelTopBarEffect(currentViewPager);
                    createTopBarEffect(position);
                }
                currentViewPager = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

    private void loadPage()
    {
        switch (currentMode)
        {
            case MODE_AllMsg:
                bookInformation = getIntent().getParcelableExtra("book");
                if (bookInformation != null)
                {
                    uploadStr = "mode=isbn&search_words=" + bookInformation.getBookIsbn();
                    setMODE_AllMsg();
                    break;
                }
                String bookIsbn = getIntent().getStringExtra("book_isbn");
                progressDialogShow("正在读取","请稍候...");
                MyNetwork.createHttpConnect(MyNetwork.Address_Search_Book,"mode=isbn&search_words="+bookIsbn,MyNetwork.NET_SEARCH_BOOK,handler);

                break;
            case MODE_BorrowMsg:
                choosePageLayout.setVisibility(View.GONE);
                userAccount = getIntent().getParcelableExtra("account");
                uploadStr = "mode=account&search_words=" + userAccount.getAccountName();
                setMODE_BorrowMsg();
                break;
            case MODE_CommentMsg:
                choosePageLayout.setVisibility(View.VISIBLE);
                userAccount = getIntent().getParcelableExtra("account");
                uploadStr = "mode=account&search_words=" + userAccount.getAccountName();
                setMODE_CommentMsg();
                break;
            default:
                break;
        }
    }

    private void setMODE_AllMsg()
    {

        choosePageLayout.setVisibility(View.VISIBLE);
        setTitle("图书详细信息");
        loadTopBar();
        loadBookMsgPage();
        loadBorrowMsgPage();
        loadCommentMsgPage();
        loadBottomBar();
        borrowHintWordsView.setText("可借数量:");
    }

    private void setMODE_BorrowMsg()
    {
        choosePageLayout.setVisibility(View.GONE);
        operateBookLayout.setVisibility(View.GONE);
        setTitle("我的借阅信息");
        loadBorrowMsgPage();
        borrowHintWordsView.setText("借阅数量:");
    }

    private void setMODE_CommentMsg()
    {
        choosePageLayout.setVisibility(View.GONE);
        operateBookLayout.setVisibility(View.GONE);
        setTitle("我的评论信息");
        loadCommentMsgPage();
    }

    private void setTitle(String titleStr)
    {
        titleLayout.setTitleText(titleStr);
    }

    private void loadTopBar()
    {
        bookMsgBtn = (Button) findViewById(R.id.book_msg_page_btn);
        bookMsgBtn.setOnClickListener(this);
        borrowMsgBtn = (Button) findViewById(R.id.borrow_msg_page_btn);
        borrowMsgBtn.setOnClickListener(this);
        commentMsgBtn = (Button) findViewById(R.id.comment_msg_page_btn);
        commentMsgBtn.setOnClickListener(this);
    }

    private void loadBottomBar()
    {
        operateBookLayout.setVisibility(View.VISIBLE);
        borrowBookBtn = (Button) findViewById(R.id.borrow_book_btn);
        borrowBookBtn.setOnClickListener(this);
        returnBookBtn = (Button) findViewById(R.id.return_book_btn);
        returnBookBtn.setOnClickListener(this);
        shareBookBtn = (Button )findViewById(R.id.share_book_btn);
        shareBookBtn.setOnClickListener(this);
    }

    private void loadBookMsgPage()
    {
        msgViewList.add(getLayoutInflater().inflate(R.layout.lay_book_msg,null));
        int position = msgViewList.size()-1;
        bookImageView =             (ImageView) msgViewList.get(position).findViewById(R.id.book_image_view);
        bookIsbnView =              (TextView) msgViewList.get(position).findViewById(R.id.book_isbn_view);
        bookNameView =              (TextView) msgViewList.get(position).findViewById(R.id.book_name_view);
        bookAuthorView =            (TextView) msgViewList.get(position).findViewById(R.id.book_author_view);
        bookScoreBar =              (RatingBar) msgViewList.get(position).findViewById(R.id.book_score_bar);
        bookScoreNumberView =       (TextView) msgViewList.get(position).findViewById(R.id.book_score_number_view);
        bookSummary =               (TextView) msgViewList.get(position).findViewById(R.id.book_summary);
        bookTotalNumberView =       (TextView) msgViewList.get(position).findViewById(R.id.book_total_number_view);
        bookRemainNumberView =      (TextView) msgViewList.get(position).findViewById(R.id.book_remain_number_view);
        bookPriceView =             (TextView) msgViewList.get(position).findViewById(R.id.book_price_view);
        bookPublishCompanyView =    (TextView) msgViewList.get(position).findViewById(R.id.book_publish_company_view);
        bookPublishTimeView =       (TextView) msgViewList.get(position).findViewById(R.id.book_publish_time_view);
        bookPositionView =          (TextView) msgViewList.get(position).findViewById(R.id.book_position_view);
        bookKeyWordsView =          (TextView) msgViewList.get(position).findViewById(R.id.book_key_words_view);
        bookNotesView =             (TextView) msgViewList.get(position).findViewById(R.id.book_notes_view);

        //System.out.println(bookInformation.toString());
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
        bookSummary.setText(bookInformation.getBookSummary());
        bookTotalNumberView.setText(String.valueOf(bookInformation.getBookTotalNumber()));
        bookRemainNumberView.setText(String.valueOf(bookInformation.getBookRemainNumber()));
        bookPriceView.setText(String.valueOf(bookInformation.getBookPrice()));
        bookPublishCompanyView.setText(bookInformation.getBookPublishCompany());
        bookPublishTimeView.setText(bookInformation.getBookPublishTime());
        bookPositionView.setText(bookInformation.getBookPosition());
        bookKeyWordsView.setText(bookInformation.getBookKeyWords());
        bookNotesView.setText(bookInformation.getBookNotes());
    }

    private void loadBorrowMsgPage()
    {
        msgViewList.add(getLayoutInflater().inflate(R.layout.lay_borrow_msg,null));
        int position = msgViewList.size()-1;
        borrowNumberLayout = (LinearLayout) msgViewList.get(position).findViewById(R.id.borrow_number_layout);
        borrowHintWordsView = (TextView) msgViewList.get(position).findViewById(R.id.borrow_hint_words_view);
        borrowNumberView = (TextView) msgViewList.get(position).findViewById(R.id.borrow_number);

        noBorrowMsgHintView = (TextView) msgViewList.get(position).findViewById(R.id.no_borrow_msg_hint_view);
        borrowMsgListView = (ListView) msgViewList.get(position).findViewById(R.id.borrow_msg_list_view);
        borrowMsgListView.setEmptyView(noBorrowMsgHintView);
        borrowMsgListAdapter = new BorrowMsgListAdapter(BookDetailActivity.this,R.layout.lay_borrow_msg_item, borrowMsgList,handler,progressDialog,currentMode);
        borrowMsgListView.setAdapter(borrowMsgListAdapter);
        borrowMsgListAdapter.notifyDataSetChanged();
        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Borrow,uploadStr,MyNetwork.NET_SEARCH_BORROW,handler);
    }

    private void loadCommentMsgPage()
    {
        msgViewList.add(getLayoutInflater().inflate(R.layout.lay_comment_msg,null));
        int position = msgViewList.size()-1;
        noCommentMsgHintView = (TextView) msgViewList.get(position).findViewById(R.id.no_comment_msg_hint_view);
        commentNumberLayout = (LinearLayout) msgViewList.get(position).findViewById(R.id.comment_number_layout);
        commentNumberView = (TextView) msgViewList.get(position).findViewById(R.id.comment_number_view);
        scoreMsgListView = (ListView) msgViewList.get(position).findViewById(R.id.score_msg_list_view);
        scoreMsgListView.setEmptyView(noCommentMsgHintView);
        //scoreMsgListView.设置上滑下滑
        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Comment,uploadStr,MyNetwork.NET_SEARCH_COMMENT,handler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_msg_page_btn:
                msgViewPager.setCurrentItem(0);
                break;
            case R.id.borrow_msg_page_btn:
                msgViewPager.setCurrentItem(1);
                break;
            case R.id.comment_msg_page_btn:
                msgViewPager.setCurrentItem(2);
                break;
            case R.id.borrow_book_btn:
            {
                //弹出dialog等待网络连接
                //弹出确认的dialog
                int borrowNumber = Integer.valueOf(borrowNumberView.getText().toString());
                if (borrowNumber > 0)
                {
                    final UserAccount userAccount = MainActivity.getUserAccount(BookDetailActivity.this,false);
                    if (userAccount != null)
                    {
                        MyDialogUtils.alertDialogShow(BookDetailActivity.this,"警告", "确认借阅吗?", "确认借阅", "取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialogShow("正在执行借阅操作","请稍候...");
                                        MyNetwork.createHttpConnect(MyNetwork.Address_Borrow_Book,"borrow_account_name="+userAccount.getAccountName() +"&book_isbn=" + bookInformation.getBookIsbn(),MyNetwork.NET_BORROW_BOOK,handler);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //取消
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this,"请先登录,然后才能借书",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(BookDetailActivity.this,"对不起,都借光了",Toast.LENGTH_SHORT).show();
                }


                break;
            }
            case R.id.return_book_btn:
            {
                //弹出确认的dialog
                UserAccount userAccount = MainActivity.getUserAccount(BookDetailActivity.this,false);
                if (userAccount != null)
                {
                    String accountName = userAccount.getAccountName();
                    for (final BorrowInformation borrowInformation:borrowMsgList)
                    {
                        if (accountName.equals(borrowInformation.getBorrowAccountName()))
                        {
                            MyDialogUtils.alertDialogShow(BookDetailActivity.this,"警告", "确认归还吗?", "确认归还", "取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //归还
                                            progressDialogShow("正在执行还书操作","请稍候...");
                                            MyNetwork.createHttpConnect(MyNetwork.Address_Return_Book,"borrow_id="+borrowInformation.getBorrowId(),MyNetwork.NET_RETURN_BOOK,handler);
                                        }
                                    },null);

                            return;
                        }
                    }
                    Toast.makeText(BookDetailActivity.this,"您没有借阅过此书,无须归还",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BookDetailActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.share_book_btn:
            {
                String url = MyNetwork.Address_Share_Book + "?book_isbn="+ bookInformation.getBookIsbn();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "向你推荐一本好书,点击这个链接就可以看到啦:" + url);
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
            }
            default:
                break;
        }
    }


    Map<String,CommentContentMsgListAdapter> adapterMap = new HashMap<>();
    List<CommentInformation> commentList = new ArrayList<>();

    public Handler handler = new Handler(){
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
                        bookInformation = bookResult.getData().get(0);
                        uploadStr = "mode=isbn&search_words=" + bookInformation.getBookIsbn();
                        setMODE_AllMsg();
                        msgViewPagerAdapter.notifyDataSetChanged();
                        progressDialogDismiss();
                    }
                    else if ("NotMatch".equals(info))
                    {
                        Toast.makeText(BookDetailActivity.this,"没有找到哎,换个词试试?",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_SEARCH_BORROW:
                {
                    BorrowResult borrowResult = JSON.parseObject((String) message.obj, BorrowResult.class);
                    String info1 = borrowResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_BORROW) + info1, false);
                    if ("Match".equals(info1))
                    {
                        int borrowCount = borrowResult.getData().size();
                        borrowMsgList.addAll(borrowResult.getData());
                        borrowMsgListAdapter.notifyDataSetChanged();
                        setBorrowCount(borrowCount);
                    }
                    else if ("NotMatch".equals(info1))
                    {
                        setBorrowCount(0);
                        borrowMsgList.clear();
                        borrowMsgListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this, "返回数据出错(请联系管理员解决此问题):" + info1, Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                case MyNetwork.NET_SEARCH_COMMENT:
                {
                    CommentResult commentResult = JSON.parseObject((String) message.obj, CommentResult.class);
                    String info2 = commentResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_COMMENT) + info2, false);
                    if ("Match".equals(info2))
                    {
                        commentList.addAll(commentResult.getData());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (CommentInformation commentInformation : commentList)
                                {

                                    if (commentMap.get(commentInformation.getCommentAccountName()) == null)
                                    {
                                        List<CommentInformation> newCommentList = new ArrayList<>();
                                        newCommentList.add(commentInformation);
                                        commentMap.put(commentInformation.getCommentAccountName(), newCommentList);

                                        CommentContentMsgListAdapter commentContentMsgListAdapter = new CommentContentMsgListAdapter(BookDetailActivity.this, R.layout.lay_comment_content_msg_item, newCommentList, 0);
                                        adapterMap.put(commentInformation.getCommentAccountName(), commentContentMsgListAdapter);
                                    }
                                    else
                                    {
                                        commentMap.get(commentInformation.getCommentAccountName()).add(commentInformation);
                                        adapterMap.get(commentInformation.getCommentAccountName()).notifyDataSetChanged();
                                    }
                                }
                                Message message1 = new Message();
                                message1.what = 100000;
                                handler.sendMessage(message1);
                            }
                        }).start();
                    }
                    else if ("NotMatch".equals(info2))
                    {
                        commentMap.clear();
                        scoreMsgListAdapter = new ScoreMsgListAdapter(BookDetailActivity.this, R.layout.lay_score_msg_item, scoreMsgList, 0, adapterMap);
                        scoreMsgListView.setAdapter(scoreMsgListAdapter);
                        MyNetwork.createHttpConnect(MyNetwork.Address_Search_Score, uploadStr, MyNetwork.NET_SEARCH_SCORE, handler);
                        scoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this, "返回数据出错(请联系管理员解决此问题):" + info2, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_SEARCH_SCORE:
                {
                    ScoreResult scoreResult = JSON.parseObject((String) message.obj, ScoreResult.class);
                    String info3 = scoreResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_SCORE) + info3, false);
                    if ("Match".equals(info3))
                    {
                        commentNumberView.setText(String.valueOf(scoreResult.getData().size()));
                        scoreMsgList.addAll(scoreResult.getData());
                        scoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else if ("NotMatch".equals(info3))
                    {
                        scoreMsgList.clear();
                        scoreMsgListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this, "返回数据出错(请联系管理员解决此问题):" + info3, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_ERROR:
                {
                    String e = (String) message.obj;
                    MyLog.e("NET_Error", e, false);
                    Toast.makeText(BookDetailActivity.this, "网络错误:" + e, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 100000:
                {
                    scoreMsgListAdapter = new ScoreMsgListAdapter(BookDetailActivity.this, R.layout.lay_score_msg_item, scoreMsgList, 0, adapterMap);
                    scoreMsgListView.setAdapter(scoreMsgListAdapter);
                    MyNetwork.createHttpConnect(MyNetwork.Address_Search_Score, uploadStr, MyNetwork.NET_SEARCH_SCORE, handler);
                    scoreMsgListAdapter.notifyDataSetChanged();
                    break;
                }
                case MyNetwork.NET_BORROW_BOOK:
                {
                    progressDialogDismiss();
                    JSONObject jsonObject = JSON.parseObject((String) message.obj);
                    String info4 = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_BORROW_BOOK) + info4, false);
                    if ("AddSuccess".equals(info4))
                    {
                        BorrowInformation borrowInformation = jsonObject.getObject("add_data",BorrowInformation.class);
                        borrowMsgList.add(borrowInformation);
						borrowMsgListAdapter.notifyDataSetChanged();
                        setBorrowCount(borrowMsgList.size());
                        //应在服务器端返回一个借阅信息类,以便添加到列表
                        //更新可借数量,封装成函数,便于调用
                        Toast.makeText(BookDetailActivity.this,"借阅成功",Toast.LENGTH_SHORT).show();
                    }
                    else if ("BorrowFull".equals(info4))
                    {
                        Toast.makeText(BookDetailActivity.this,"对不起,此书已借完",Toast.LENGTH_SHORT).show();
                    }
                    else if ("BorrowExisted".equals(info4))
                    {
                        Toast.makeText(BookDetailActivity.this,"对不起,相同的书只能借一本",Toast.LENGTH_SHORT).show();
                    }
                    else if ("BookIsbnNotExisted".equals(info4))
                    {
                        Toast.makeText(BookDetailActivity.this,"对不起,此书不存在(您可能需要咨询管理员)",Toast.LENGTH_SHORT).show();
                    }
                    else if ("AccountNotExisted".equals(info4))
                    {
                        Toast.makeText(BookDetailActivity.this,"对不起,没有找到您的账户,请咨询管理员",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this,"返回数据出错(请联系管理员解决此问题):" + info4,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MyNetwork.NET_RETURN_BOOK:
                {
                    progressDialogDismiss();
                    JSONObject jsonObject = JSON.parseObject((String) message.obj);
                    String info5 = jsonObject.getString("info");
                    if ("DeleteSuccess".equals(info5))
                    {
                        BorrowInformation borrowInformation = jsonObject.getObject("delete_data",BorrowInformation.class);
                        for (BorrowInformation borrow : borrowMsgList)
                        {
                            if (borrow.getBorrowId().intValue() == borrowInformation.getBorrowId().intValue())
                            {
                                borrowMsgList.remove(borrow);
                                break;
                            }
                        }
                        borrowMsgListAdapter.notifyDataSetChanged();
                        Toast.makeText(BookDetailActivity.this,"归还成功",Toast.LENGTH_SHORT).show();
                        setBorrowCount(borrowMsgList.size());
                        if (currentMode == MODE_AllMsg)
                        {
                            AddCommentActivity.actionStart(BookDetailActivity.this,bookInformation);
                        }
                        else if (currentMode == MODE_BorrowMsg)
                        {
                            AddCommentActivity.actionStart(BookDetailActivity.this,borrowInformation.getBookIsbn());
                        }
                    }
                    else if ("DeleteError".equals(info5))
                    {
                        Toast.makeText(BookDetailActivity.this,"归还失败:" + info5,Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(BookDetailActivity.this,"返回数据出错(请联系管理员解决此问题):" + info5,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    //Mode_all模式,userAccount传递null值,必须传递bookInformation值
    //其他模式,bookInformation传递null值,但必须传递userAccount值,没有登录时,就直接在这个函数之前拦截下来
    public static void actionStart(Context context, int mode, BookInformation bookInformation, UserAccount userAccount)
    {
        Intent intent = new Intent(context,BookDetailActivity.class);
        intent.putExtra("mode",mode);
        if (bookInformation !=null)
        {
            intent.putExtra("book",bookInformation);
        }
        if (userAccount != null)
        {
            intent.putExtra("account",userAccount);
        }
        context.startActivity(intent);

    }

    public static void actionStart(Context context,String bookIsbn)
    {
        Intent intent = new Intent(context,BookDetailActivity.class);
        intent.putExtra("book_isbn",bookIsbn);
        context.startActivity(intent);
    }


    private void progressDialogShow(String title,String message)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);//借阅/评论等操作不可取消(因为数据库那边可能添加上了)
        progressDialog.show();
    }

    private void progressDialogDismiss()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    //private

    private void createTopBarEffect(int index)
    {
        switch (index)
        {
            case 0:bookMsgBtn.setTextColor(MainActivity.COLOR_BLUE);break;
            case 1:borrowMsgBtn.setTextColor(MainActivity.COLOR_BLUE);break;
            case 2:commentMsgBtn.setTextColor(MainActivity.COLOR_BLUE);break;
            default: MyLog.e("TopBar","Error:" + String.valueOf(index));break;
        }
    }

    private void cancelTopBarEffect(int index)
    {
        switch (index)
        {
            case 0:bookMsgBtn.setTextColor(Color.BLACK);break;
            case 1:borrowMsgBtn.setTextColor(Color.BLACK);break;
            case 2:commentMsgBtn.setTextColor(Color.BLACK);break;
            default:MyLog.e("TopBar", "Error:" + String.valueOf(index));break;
        }
    }

    private void setBorrowCount(int borrowCount)
    {
        if (currentMode == MODE_AllMsg)
        {
            int leftNumber = bookInformation.getBookTotalNumber() - bookInformation.getBookRemainNumber() - borrowCount;
            if (leftNumber > 0)
            {
                borrowNumberView.setText(String.valueOf(leftNumber));
            }
            else
            {
                borrowNumberView.setText("0");
            }
        }
        else if (currentMode == MODE_BorrowMsg)
        {
            borrowNumberView.setText(String.valueOf(borrowCount));
        }
    }

}
