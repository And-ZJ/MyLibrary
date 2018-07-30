package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.adapter.BookItemListAdapter;
import com.andzj.mylibrary.adapter.SearchHistoryListAdapter;
import com.andzj.mylibrary.bean.BookInformation;
import com.andzj.mylibrary.bean.BookResult;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.ImageLoader;
import com.andzj.mylibrary.util.MyFileOperateUtils;
import com.andzj.mylibrary.util.MyLog;
import com.andzj.mylibrary.util.SystemSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2016/9/22.
 */

public class SearchBookActivity extends BaseActivity implements View.OnClickListener
{
    public static final int MODE_MOST            = 60;
    public static final int MODE_ISBN            = 61;
    public static final int MODE_NAME            = 62;
    public static final int MODE_AUTHOR          = 63;
    public static final int MODE_PUBLISH_COMPANY = 64;
    public static final int MODE_KEY_WORDS       = 65;

    private int currentMode = MODE_MOST;

    private boolean isSearching = false;

    public static final String HISTORY_DIR = "/data/history_record/history.txt";
    public static final char DIVIDE_CHAR = 0;
    public static final String DIVIDE_STRING = String.valueOf(DIVIDE_CHAR);

    InputMethodManager imm;
    private ImageButton scanBtn;
    private EditText searchBookEdit;
    private ImageButton deleteSearchEditBtn;

    private ScrollView searchChooseScroll;
    private LinearLayout searchAllLayout;
    private TextView searchAllView;
    private LinearLayout searchNameLayout;
    private TextView searchNameView;
    private LinearLayout searchAuthorLayout;
    private TextView searchAuthorView;
    private LinearLayout searchIsbnLayout;
    private TextView searchIsbnView;
    private LinearLayout searchKeyWordsLayout;
    private TextView searchKeyWordsView;
    private LinearLayout searchPublishCompanyLayout;
    private TextView searchPublishCompanyView;

    private LinearLayout searchResultLayout;
    private TextView searchingHintView;
    private ListView searchResultListView;

    private LinearLayout searchHistoryLayout;
    private TextView closeHistoryListView;
    private TextView closeHistoryRecordView;
    private ListView searchHistoryListView;
    private TextView deleteHistoryRecordView;
    private ArrayList<String> searchHistoryList = new ArrayList<>();
    private SearchHistoryListAdapter searchHistoryListAdapter;

    private List<BookInformation> bookInformationList = new ArrayList<>();
    private BookItemListAdapter bookItemListAdapter;
    private ImageLoader imageLoader;
    public static boolean isScroll = false;


    private String searchWords="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_search_book);
        imageLoader = MainActivity.getImageLoader(SearchBookActivity.this);
        Intent intent = getIntent();
        currentMode = intent.getIntExtra("mode", MODE_MOST);

        scanBtn = (ImageButton) findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(this);
        searchBookEdit = (EditText) findViewById(R.id.search_book_edit);
        searchBookEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                searchResultLayout.setVisibility(View.GONE);
                if (!"".equals(searchBookEdit.getText().toString()))
                {
                    searchChooseScroll.setVisibility(View.VISIBLE);
                    searchHistoryLayout.setVisibility(View.GONE);
                }
                else
                {
                    searchChooseScroll.setVisibility(View.GONE);
                    if (SystemSet.isHistoryRecordOpen() && searchHistoryList.size() > 0)
                    {
                        searchHistoryLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        searchResultLayout.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        searchBookEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBookEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if ("".equals(v.getText().toString()))
                    {
                        if (imm != null)
                        {
                            imm.hideSoftInputFromWindow(searchBookEdit.getWindowToken(), 0);
                        }
                    }
                    else
                    {
                        //MyLog.d("SearchBookActivity","Enter 事件",false);
                        doSearchOperate(currentMode);
                    }
                }
                return false;
            }
        });
        searchBookEdit.setSingleLine();
        searchBookEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString()))
                {
                    deleteSearchEditBtn.setVisibility(View.GONE);
                    searchChooseScroll.setVisibility(View.GONE);
                    //显示历史记录
                    if (SystemSet.isHistoryRecordOpen() && searchHistoryList.size() > 0)
                    {
                        searchHistoryLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        searchResultLayout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    searchResultLayout.setVisibility(View.GONE);
                    String str = s.toString();
                    searchAllView.setText(str);
                    searchNameView.setText(str);
                    searchAuthorView.setText(str);
                    searchIsbnView.setText(str);
                    searchKeyWordsView.setText(str);
                    searchPublishCompanyView.setText(str);
                    searchHistoryLayout.setVisibility(View.GONE);//关闭历史记录
                    deleteSearchEditBtn.setVisibility(View.VISIBLE);
                    searchChooseScroll.setVisibility(View.VISIBLE);
                }
            }
        });
        deleteSearchEditBtn = (ImageButton) findViewById(R.id.delete_search_edit_btn);
        deleteSearchEditBtn.setOnClickListener(this);

        searchChooseScroll = (ScrollView) findViewById(R.id.search_choose_scroll);
        searchAllLayout = (LinearLayout) findViewById(R.id.search_all_layout);
        searchAllLayout.setOnClickListener(this);
        searchAllView = (TextView) findViewById(R.id.search_all_view);
        searchNameLayout = (LinearLayout) findViewById(R.id.search_name_layout);
        searchNameLayout.setOnClickListener(this);
        searchNameView = (TextView) findViewById(R.id.search_name_view);
        searchAuthorLayout = (LinearLayout) findViewById(R.id.search_author_layout);
        searchAuthorLayout.setOnClickListener(this);
        searchAuthorView = (TextView) findViewById(R.id.search_author_view);
        searchIsbnLayout = (LinearLayout) findViewById(R.id.search_isbn_layout);
        searchIsbnLayout.setOnClickListener(this);
        searchIsbnView = (TextView) findViewById(R.id.search_isbn_view);
        searchKeyWordsLayout = (LinearLayout) findViewById(R.id.search_key_words_layout);
        searchKeyWordsLayout.setOnClickListener(this);
        searchKeyWordsView = (TextView) findViewById(R.id.search_key_words_view);
        searchPublishCompanyLayout = (LinearLayout) findViewById(R.id.search_publish_company_layout);
        searchPublishCompanyLayout.setOnClickListener(this);
        searchPublishCompanyView = (TextView) findViewById(R.id.search_publish_company_view);

        searchHistoryLayout = (LinearLayout) findViewById(R.id.search_history_layout);
        closeHistoryListView = (TextView) findViewById(R.id.close_history_list_view);
        closeHistoryListView.setOnClickListener(this);
        closeHistoryRecordView = (TextView) findViewById(R.id.close_history_record_view);
        closeHistoryRecordView.setOnClickListener(this);
        deleteHistoryRecordView = (TextView) findViewById(R.id.delete_history_record_view);
        deleteHistoryRecordView.setOnClickListener(this);
        searchHistoryListView = (ListView) findViewById(R.id.history_list_view);
        searchHistoryListAdapter = new SearchHistoryListAdapter(SearchBookActivity.this,R.layout.lay_search_history_item,searchHistoryList);
        searchHistoryListView.setAdapter(searchHistoryListAdapter);
        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = searchHistoryList.get(position);
                searchBookEdit.setText(string);
                searchBookEdit.setSelection(searchBookEdit.getText().length());
            }
        });


        searchResultLayout = (LinearLayout) findViewById(R.id.search_result_layout);
        searchingHintView = (TextView) findViewById(R.id.searching_hint_view);
        searchResultListView = (ListView) findViewById(R.id.search_result_list);



        bookItemListAdapter = new BookItemListAdapter(this,R.layout.lay_book_item,bookInformationList,imageLoader);
        searchResultListView = (ListView) findViewById(R.id.search_result_list);
        searchHistoryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                {
                    isScroll = false;
                }
                else
                {
                    isScroll = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        searchResultListView.setAdapter(bookItemListAdapter);
            searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    //BookItem bookItem = bookItemList.get(position);
                    BookInformation  bookInformation = bookInformationList.get(position);
                    BookDetailActivity.actionStart(SearchBookActivity.this,BookDetailActivity.MODE_AllMsg,bookInformation,null);
                    //BookDetailActivity.actionStart(SearchBookActivity.this, BookDetailActivity.MODE_BookMsg,bookItem);
                }
            });


        if (SystemSet.isHistoryRecordOpen())
        {
            new LoadSearchHistoryTask().execute();
        }
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //延时显示软键盘
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imm != null)
                {
                    searchBookEdit.requestFocus();
                    imm.showSoftInput(searchBookEdit, 0);
                }
            }
        },100);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.scan_btn:
                Intent intent = new Intent(SearchBookActivity.this,ScanBarCodeActivity.class);
                intent.putExtra("mode",ScanBarCodeActivity.MODE_SEARCH_DATABASE);
                startActivity(intent);
                break;
            case R.id.delete_search_edit_btn:
                searchBookEdit.setText("");
                searchResultLayout.setVisibility(View.GONE);
                if (imm != null)
                {
                    searchBookEdit.requestFocus();
                    imm.showSoftInput(searchBookEdit, 0);
                }
                break;
            case R.id.delete_history_record_view:
                searchResultLayout.setVisibility(View.VISIBLE);
                cleanSearchHistory();
                searchHistoryLayout.setVisibility(View.GONE);
                searchHistoryList.clear();
                searchHistoryListAdapter.notifyDataSetChanged();
                break;
            case R.id.close_history_list_view:
                searchHistoryLayout.setVisibility(View.GONE);
                searchResultLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.close_history_record_view:
                SystemSet.setHistoryRecordOpen(false);
                searchHistoryLayout.setVisibility(View.GONE);
                searchResultLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.search_all_layout:
                doSearchOperate(MODE_MOST);
                break;
            case R.id.search_name_layout:
                doSearchOperate(MODE_NAME);
                break;
            case R.id.search_author_layout:
                doSearchOperate(MODE_AUTHOR);
                break;
            case R.id.search_isbn_layout:
                doSearchOperate(MODE_ISBN);
                break;
            case R.id.search_key_words_layout:
                doSearchOperate(MODE_KEY_WORDS);
                break;
            case R.id.search_publish_company_layout:
                doSearchOperate(MODE_PUBLISH_COMPANY);
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    public static void actionStart(Context context ,String titleStr,String searchWords,int mode)
    {
        Intent intent = new Intent(context, SearchBookActivity.class);
        intent.putExtra("title",titleStr);
        intent.putExtra("search_words",searchWords);
        intent.putExtra("mode",mode);
        context.startActivity(intent);
    }

    public static void actionStart(Context context)
    {
        actionStart(context,null,null, MODE_MOST);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what)
            {
                case MyNetwork.NET_SEARCH_BOOK:
                    //Result<BookInformation> result = JSON.parseObject((String)message.obj,Result.class);
                    BookResult bookResult = JSON.parseObject((String)message.obj, BookResult.class);
                    String info = bookResult.getInfo();
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_SEARCH_BOOK) + info,false);
                    if ("Match".equals(info))
                    {
//                        //可用解析Json代码
//                        JSONObject jsonObject = JSON.parseObject((String)message.obj);
//                        String info = jsonObject.getString("info");
//                        //方法一
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        List<BookInformation> list = new ArrayList<>();
//                        for (int i=0;i<jsonArray.size();i++)
//                        {
//                            list.add(jsonArray.getObject(i,BookInformation.class));
//                        }
//                        System.out.println("size:" + list.size());
//                        //方法二
//                        BookInformation[] bookInformation = jsonObject.getObject("data", BookInformation[].class);
//                        System.out.println("author:" + bookInformation[0].getBookAuthor());
                        if (imm != null)
                        {
                            imm.hideSoftInputFromWindow(searchBookEdit.getWindowToken(), 0);
                        }
                        searchingHintView.setVisibility(View.GONE);
                        //bookItemList.addAll(searchResultBooks);
                        bookInformationList.addAll(bookResult.getData());
                        bookItemListAdapter.notifyDataSetChanged();
                        isSearching = false;
                        break;
                    }
                    else if ("NotMatch".equals(info))
                    {
                        Toast.makeText(SearchBookActivity.this,"没有找到哎,换个词试试?",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(SearchBookActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                    }
                    if (imm != null)
                    {
                        searchBookEdit.requestFocus();
                        imm.showSoftInput(searchBookEdit, 0);
                    }
                    searchingHintView.setVisibility(View.GONE);
                    isSearching = false;
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    Toast.makeText(SearchBookActivity.this,"网络错误:" + e,Toast.LENGTH_SHORT).show();
                    searchingHintView.setVisibility(View.GONE);
                    isSearching = false;
                    break;
                default:
                    break;
            }
        }
    };


    private void doSearchOperate(int mode)
    {

        if (isSearching)
        {
            Toast.makeText(this,"请等待上一个操作完成",Toast.LENGTH_SHORT).show();
            return;
        }
        isSearching = true;
        if (!MyNetwork.checkNetworkAvailable())
        {
            Toast.makeText(SearchBookActivity.this,"无可用网络",Toast.LENGTH_SHORT).show();
            isSearching=false;
            return;
        }
        searchWords = searchBookEdit.getText().toString();
        if (!"".equals(searchWords))
        {
            currentMode = mode;
            String s;
            searchChooseScroll.setVisibility(View.GONE);
            searchHistoryLayout.setVisibility(View.GONE);
            searchResultLayout.setVisibility(View.VISIBLE);
            //bookItemList.clear();
            bookInformationList.clear();
            bookItemListAdapter.notifyDataSetChanged();
            searchingHintView.setVisibility(View.VISIBLE);
            searchingHintView.setText("搜索中...");
            switch (currentMode)
            {
                case MODE_MOST:s = "mode=most&search_words=" + searchWords;
                    break;
                case MODE_ISBN:s = "mode=isbn&search_words=" + searchWords;
                    break;
                case MODE_NAME:s = "mode=name&search_words=" + searchWords;
                    break;
                case MODE_AUTHOR:s = "mode=author&search_words=" + searchWords;
                    break;
                case MODE_PUBLISH_COMPANY:s = "mode=company&search_words=" + searchWords;
                    break;
                case MODE_KEY_WORDS:s = "mode=key&search_words=" + searchWords;
                    break;
                default:s = "";
                    break;
            }
            if (!"".equals(s))
            {
                MyNetwork.createHttpConnect(MyNetwork.Address_Search_Book,s,MyNetwork.NET_SEARCH_BOOK,handler);
            }
            else
            {
                Toast.makeText(SearchBookActivity.this,"模式错误(请联系管理员):" + currentMode,Toast.LENGTH_SHORT).show();
            }

            if (SystemSet.isHistoryRecordOpen())
            {
                int i = searchHistoryList.indexOf(searchWords);//先判断下是否重复
                if (i != -1)
                {
                    String str = searchHistoryList.remove(i);
                    searchHistoryList.add(0,str);
                    saveSearchHistoryList(searchHistoryList,false);
                }
                else
                {
                    searchHistoryList.add(0,searchWords);
                    saveSearchHistory(searchWords + DIVIDE_CHAR,true);
                }
                searchHistoryListAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean cleanSearchHistory()
    {
        if (MyFileOperateUtils.getWritableEmptyExternalFile(SearchBookActivity.this,HISTORY_DIR) != null)
        {
            MyLog.d("SearchBookActivity","History clean ok",false);
            return true;
        }
        return false;
    }

    public boolean saveSearchHistoryList(ArrayList<String> historyList, boolean append)
    {
        if (historyList == null || historyList.size() == 0)
        {
            return false;
        }
        StringBuilder historyStrBuilder = new StringBuilder(100);
        for (int i = historyList.size()-1;i>=0;--i)
        {
            String s = historyList.get(i);
            historyStrBuilder.append(s).append(DIVIDE_CHAR);
        }
        if (MyFileOperateUtils.saveStringDate(SearchBookActivity.this,historyStrBuilder.toString(),HISTORY_DIR,append))
        {
            return true;
        }
        MyLog.d("SearchBookActivity","History save failed",false);
        return false;
    }

    private boolean saveSearchHistory(String text, boolean append)
    {
        if (MyFileOperateUtils.saveStringDate(SearchBookActivity.this,text,HISTORY_DIR,append))
        {
             return true;
        }
        MyLog.d("SearchBookActivity","History save failed",false);
        return false;
    }

    private ArrayList<String> readSearchHistoryList()
    {
        String historyStr = MyFileOperateUtils.readStringData(SearchBookActivity.this,HISTORY_DIR);
        if (historyStr == null)
        {
            return null;
        }
        StringBuilder historyBuilder = new StringBuilder(historyStr);
        ArrayList<String> historyList = new ArrayList<>();
        String history;
        int i = historyBuilder.indexOf(DIVIDE_STRING);
        while (i != -1)
        {
            history =  historyBuilder.substring(0,i);
            if (history.length() == 0)
            {
                break;
            }
            historyList.add(0,history);
            historyBuilder.delete(0,i+1);
            i = historyBuilder.indexOf(DIVIDE_STRING);
        }
        return historyList;
    }

    private class LoadSearchHistoryTask extends AsyncTask<Void ,Void,Boolean>
    {
        ArrayList<String> historyList;
        @Override
        protected void onPreExecute() {
            searchHistoryList.clear();
            searchHistoryListAdapter.notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            historyList = readSearchHistoryList();
            return historyList != null && historyList.size() > 0;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //MyLog.d("SearchBookActivity","read size = " + String.valueOf(historyList.size()));
            if (result)
            {
                if (!"".equals(searchBookEdit.getText().toString()))
                {
                    searchHistoryLayout.setVisibility(View.GONE);
                }
                else
                {
                    searchHistoryLayout.setVisibility(View.VISIBLE);
                }
                searchHistoryList.addAll(historyList);
                searchHistoryListAdapter.notifyDataSetChanged();
            }
            else
            {
                searchHistoryLayout.setVisibility(View.GONE);
            }
        }
    }
}
