package com.andzj.mylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.StaticLayout;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.andzj.mylibrary.R;
import com.andzj.mylibrary.adapter.MyViewPagerAdapter;
import com.andzj.mylibrary.bean.UserAccount;
import com.andzj.mylibrary.db.AccountDatabaseHelper;
import com.andzj.mylibrary.net.MyNetwork;
import com.andzj.mylibrary.util.Base64Utils;
import com.andzj.mylibrary.util.Encrypter;
import com.andzj.mylibrary.util.ImageLoader;
import com.andzj.mylibrary.util.MyLog;

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.andzj.mylibrary.util.RSAUtils;
import com.andzj.mylibrary.util.SystemSet;

public class MainActivity extends BaseActivity implements OnClickListener  //AppCompatActivity
{

    //登录相关
    private static UserAccount userAccount;
    private static boolean isLogin = false;
    private static boolean autoLogin = false;
    private static boolean loaclLogin = true;

    public static boolean setUserAccount(UserAccount userAccount)
    {
        if (userAccount != null)
        {
            MainActivity.userAccount = userAccount;
            AccountDatabaseHelper.saveToDB(userAccount);
            return true;
        }
        else
        {
            return false;
        }

    }

    public static void login(UserAccount userAccount)
    {
        MainActivity.userAccount = userAccount;
    }

    public static void exitLogin()
    {
        MainActivity.userAccount = null;
    }

    public static void setLoginState(boolean is_login)
    {
        isLogin = is_login;
    }

    public static boolean isLogin()
    {
        return isLogin;
    }

    public static UserAccount getUserAccount(Context context ,boolean goLogin)
    {
        if (isLogin() && userAccount != null)
        {
            return userAccount;
        }
        Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
        if (goLogin)//跳转去登录标记
        {
            LoginActivity.actionStart(context);
        }
        return null;
    }

    public static UserAccount getUserAccount(Context context)
    {
        return getUserAccount(context,true);
    }

    //总布局
    InputMethodManager imm;
    private ViewPager mainViewPager;
    private List<View> mainLists = new ArrayList<View>();
    private MyViewPagerAdapter mainViewPagerAdapter;
    private Button firstPageBtn;
    private Button secondPageBtn;
    private Button thirdPageBtn;
    private int mainCurrentView = 0;
    public static int COLOR_BLUE = Color.parseColor("#00A2E8");

    //第一页
    private ImageButton scanBtn;
    private EditText searchBookEdit;

    //第二页
    private Button queryBorrowMsgBtn;
    private Button queryBorrowHistoryMsgBtn;
    private Button queryCommentMsgBtn;


    //第三页
    private Button loginBtn;
    private Button systemSetBtn;
    private Button exitAppBtn;


    private static ImageLoader imageLoader;

    public static ImageLoader getImageLoader(Context context){
        if (imageLoader != null)
        {
            MyLog.w("ImageLoader","MainActivity ImageLoader",false);
            return imageLoader;
        }
        else
        {
            MyLog.w("ImageLoader",context.getPackageName() + "ImageLoader",false);
            return ImageLoader.builder(context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        SystemSet systemSet = new SystemSet();
        systemSet.init();

        SharedPreferences evidencePref = getSharedPreferences("evidence",MODE_PRIVATE);
        autoLogin = evidencePref.getBoolean("auto_login",false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        createTopBar();
        createMainViewPager();
        createBottomBar();
        createBottomBarEffect(0);

        if (autoLogin)
        {
            new LoadUserAccountTask().execute();
        }
        imageLoader = ImageLoader.builder(MainActivity.this);
        //MyNetwork.checkMobileNetWork();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (isLogin)//是否在登录状态
        {
            if (userAccount == null)
            {
                if (autoLogin)
                {
                    new LoadUserAccountTask().execute();
                }
            }
            else
            {
                loginBtn.setText(userAccount.getAccountName());
            }
        }
        else
        {
            userAccount = null;
            loginBtn.setText("登录/注册");
        }

    }

    private void createTopBar(){}


    private void createMainViewPager()
    {
        mainLists.add(getLayoutInflater().inflate(R.layout.lay_first_page,null));
        mainLists.add(getLayoutInflater().inflate(R.layout.lay_second_page,null));
        mainLists.add(getLayoutInflater().inflate(R.layout.lay_third_page,null));

        //第一页
        scanBtn = (ImageButton) mainLists.get(0).findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(this);
        searchBookEdit = (EditText) mainLists.get(0).findViewById(R.id.search_book_edit);
        searchBookEdit.setKeyListener(null);
        searchBookEdit.setOnClickListener(this);

        //第二页
        queryBorrowMsgBtn = (Button) mainLists.get(1).findViewById(R.id.query_borrow_msg_btn);
        queryBorrowMsgBtn.setOnClickListener(this);
        queryBorrowHistoryMsgBtn = (Button) mainLists.get(1).findViewById(R.id.query_borrow_history_msg_btn);
        queryBorrowHistoryMsgBtn.setOnClickListener(this);
        queryCommentMsgBtn = (Button) mainLists.get(1).findViewById(R.id.query_comment_msg_btn);
        queryCommentMsgBtn.setOnClickListener(this);

        //第三页
        loginBtn = (Button) mainLists.get(2).findViewById(R.id.login_register_btn);
        loginBtn.setOnClickListener(this);
        systemSetBtn = (Button) mainLists.get(2).findViewById(R.id.system_set_btn);
        systemSetBtn.setOnClickListener(this);
        exitAppBtn = (Button) mainLists.get(2).findViewById(R.id.exit_app_btn);
        exitAppBtn.setOnClickListener(this);

        mainViewPagerAdapter = new MyViewPagerAdapter(mainLists);
        mainViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position)
            {
                MyLog.d("ViewPager","Position = " + String.valueOf(position),false);
                switch (position)
                {
                    case 0:
                        if (imm != null)
                        {
                            searchBookEdit.requestFocus();
                        }
                        break;
                }
                if (mainCurrentView != position)
                {
                    createBottomBarEffect(position);
                    cancelBottomBarEffect(mainCurrentView);
                }
                mainCurrentView = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void createBottomBarEffect(int index)
    {
        switch (index)
        {
            case 0:firstPageBtn.setTextColor(COLOR_BLUE); break;
            case 1:secondPageBtn.setTextColor(COLOR_BLUE);break;
            case 2:thirdPageBtn.setTextColor(COLOR_BLUE); break;
            default : MyLog.e("BottomBar","Error:" + String.valueOf(index));break;
        }
    }

    private void cancelBottomBarEffect(int index)
    {
        switch (index)
        {
            case 0:firstPageBtn.setTextColor(Color.BLACK); break;
            case 1:secondPageBtn.setTextColor(Color.BLACK);break;
            case 2:thirdPageBtn.setTextColor(Color.BLACK); break;
            default : MyLog.e("BottomBar","Error:" + String.valueOf(index));break;
        }
    }

    private void createBottomBar()
    {
        firstPageBtn  = (Button) this.findViewById(R.id.first_page_btn);
        secondPageBtn = (Button) this.findViewById(R.id.second_page_btn);
        thirdPageBtn  = (Button) this.findViewById(R.id.third_page_btn);

        firstPageBtn.setOnClickListener(this);
        secondPageBtn.setOnClickListener(this);
        thirdPageBtn.setOnClickListener(this);
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MyNetwork.NET_LOGIN:
                    JSONObject jsonObject = JSON.parseObject((String)message.obj);
                    String info = jsonObject.getString("info");
                    MyLog.i("NET_info", String.valueOf(MyNetwork.NET_LOGIN) + info,false);
                    if ("LoginSuccess".equals(info))
                    {
                        UserAccount userAccount = jsonObject.getObject("data",UserAccount.class);
                        if (userAccount != null)
                        {
                            MainActivity.setUserAccount(userAccount);
                            MainActivity.setLoginState(true);
                            loginBtn.setText(userAccount.getAccountName());
                            loaclLogin = false;
                            MyLog.d("AutoLogin","联网登录完成",false);
                        }
                    }
                    else if ("PasswordWrong".equals(info))
                    {
                        Toast.makeText(MainActivity.this, "密码错误,无法自动登录", Toast.LENGTH_SHORT).show();
                        MyLog.d("AutoLogin","密码错误",false);
                        userAccount = null;
                        setLoginState(false);
                        loginBtn.setText("登录/注册");
                    }
                    else if ("AccountNotExisted".equals(info))
                    {
                        Toast.makeText(MainActivity.this, "没有此帐号,无法自动登录,可能需要联系管理员", Toast.LENGTH_SHORT).show();
                        MyLog.d("AutoLogin","帐号错误",false);
                        userAccount = null;
                        setLoginState(false);
                        loginBtn.setText("登录/注册");
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"返回数据出错(请联系管理员解决此问题):" + info,Toast.LENGTH_SHORT).show();
                        MyLog.d("AutoLogin","返回数据出错" + info,false);
                        userAccount = null;
                        setLoginState(false);
                        loginBtn.setText("登录/注册");
                    }
                    break;
                case MyNetwork.NET_ERROR:
                    String e = (String) message.obj;
                    MyLog.e("NET_Error",e,false);
                    MyLog.d("AutoLogin", "网络错误" + e, false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.first_page_btn:
                mainViewPager.setCurrentItem(0);
                break;
            case R.id.second_page_btn:
                mainViewPager.setCurrentItem(1);
                break;
            case R.id.third_page_btn:
                mainViewPager.setCurrentItem(2);
                break;
            case R.id.scan_btn:
                Intent intent = new Intent(MainActivity.this,ScanBarCodeActivity.class);
                intent.putExtra("mode",ScanBarCodeActivity.MODE_SEARCH_DATABASE);
                startActivity(intent);
            break;
            case R.id.search_book_edit:
                SearchBookActivity.actionStart(MainActivity.this);
                break;
            case R.id.query_borrow_msg_btn:
                if (userAccount != null)
                {
                    BookDetailActivity.actionStart(MainActivity.this,BookDetailActivity.MODE_BorrowMsg,null,userAccount);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"您还没有登录,请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.query_borrow_history_msg_btn:
                if (userAccount != null)
                {
                    BorrowHistoryActivity.actionStart(MainActivity.this,userAccount.getAccountName());
                }
                else
                {
                    Toast.makeText(MainActivity.this,"您还没有登录,请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.query_comment_msg_btn:
                if (userAccount != null)
                {
                    CommentActivity.actionStart(MainActivity.this,userAccount.getAccountName());
                }
                else
                {
                    Toast.makeText(MainActivity.this,"您还没有登录,请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_register_btn:
                if (isLogin)
                {
                    AccountMsgActivity.actionStart(MainActivity.this,userAccount);
                }
                else
                {
                    LoginActivity.actionStart(MainActivity.this);
                }
                break;
            case R.id.system_set_btn:
                SystemSetActivity.actionStart(MainActivity.this);
                break;
            case R.id.exit_app_btn:
                ActivityCollector.finishAll();
                break;
            default:
                break;
        }
    }

    private void autoLogin(String accountName,String passwordMD5)
    {
        String s = "account_name=" + accountName + "&password_md5=" + passwordMD5;
        MyNetwork.createHttpConnect(MyNetwork.Address_Login, s,MyNetwork.NET_LOGIN,handler);
    }

    private String accountName = "";
    //加载用户名及其对象
    private class LoadUserAccountTask extends AsyncTask<Void,Void,Boolean>
    {

        String passwordMD5 = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setLoginState(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try
            {
                SharedPreferences evidencePref = getSharedPreferences("evidence",MODE_PRIVATE);

                Boolean autoLogin = evidencePref.getBoolean("auto_login",false);
                //Boolean loginState = evidencePref.getBoolean("login_state",false);
                if (autoLogin)
                {
                    String savedAccount = evidencePref.getString("saved_account","");
                    if (!"".equals(savedAccount))
                    {
                        String savedPassword = evidencePref.getString("saved_password","");
                        InputStream inPrivate = getResources().getAssets().open("pkcs8_rsa_private_key.pem");
                        PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
                        byte[] accountDecryptByte = RSAUtils.decryptData(Base64Utils.decode(savedAccount), privateKey);
                        if (accountDecryptByte != null && accountDecryptByte.length > 0)
                        {
                            accountName = new String(accountDecryptByte);
                        }

                        byte[] passwordDecryptByte = RSAUtils.decryptData(Base64Utils.decode(savedPassword), privateKey);
                        if (passwordDecryptByte != null && passwordDecryptByte.length > 0)
                        {
                            String password = new String(passwordDecryptByte);
                            passwordMD5 = Encrypter.GetMD5Code(password);
                        }
                        return true;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
            {
                if (!"".equals(accountName) &&!"".equals(passwordMD5))
                {
                    //先从本地加载
                    UserAccount account = AccountDatabaseHelper.getAccount(accountName);
                    if (account != null)
                    {
                        MyLog.d("AutoLogin","从本地数据库加载",false);
                        MainActivity.login(account);
                        MainActivity.setLoginState(true);
                        loginBtn.setText(account.getAccountName());
                        loaclLogin = true;
                    }
                    if (MyNetwork.checkNetworkAvailable())
                    {
                        autoLogin(accountName, passwordMD5);
                    }
                }
                else
                {
                    MyLog.d("AutoLogin","没有保存密码",false);
                    userAccount = null;
                    setLoginState(false);
                }

            }
            else
            {
                MyLog.d("AutoLogin","没有保存账号",false);
                userAccount = null;
                setLoginState(false);
                //loginBtn.setText("登录/注册");
            }
        }
    }

}
