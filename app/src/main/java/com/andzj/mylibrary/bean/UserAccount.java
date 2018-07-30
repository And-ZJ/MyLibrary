package com.andzj.mylibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccount implements Parcelable
{
	private Integer accountId;
	private String accountName;
	private String passwordMD5;
	private String registerTime;
	private String bindStudentAccount;

	private String userImageStr;
	private String userNickname;
	private String userSex;
	private String userDescribeWords;
    private String userUpdateTime;

	public UserAccount(){}

    public UserAccount(Integer accountId,String accountName,String passwordMD5,
                       String registerTime, String bindStudentAccount,
                       String userImageStr,String userNickname,String userSex,
                       String userDescribeWords,String userUpdateTime)
    {
        this.accountId = accountId;
        this.accountName = accountName;
        this.passwordMD5 = passwordMD5;
        this.registerTime = registerTime;
        this.bindStudentAccount = bindStudentAccount;
        this.userImageStr = userImageStr;
        this.userNickname = userNickname;
        this.userSex = userSex;
        this.userDescribeWords = userDescribeWords;
        this.userUpdateTime = userUpdateTime;
    }

    public UserAccount(Integer accountId,String accountName, String registerTime, String bindStudentAccount,
                       String userImageStr,String userNickname,String userSex,String userDescribeWords,String userUpdateTime)
    {
        this(accountId,accountName,"",registerTime,bindStudentAccount,userImageStr,userNickname,userSex,userDescribeWords,userUpdateTime);
    }

    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getPasswordMD5() {
		return passwordMD5;
	}
	public void setPasswordMD5(String passwordMD5) {
		this.passwordMD5 = passwordMD5;
	}
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	public String getBindStudentAccount() {
		return bindStudentAccount;
	}
	public void setBindStudentAccount(String bindStudentAccount) {
		this.bindStudentAccount = bindStudentAccount;
	}
	public String getUserImageStr() {
		return userImageStr;
	}
	public void setUserImageStr(String userImageStr) {
		this.userImageStr = userImageStr;
	}
	public String getUserNickname() {
		return userNickname;
	}
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public String getUserDescribeWords() {
		return userDescribeWords;
	}
	public void setUserDescribeWords(String userDescribeWords) {
		this.userDescribeWords = userDescribeWords;
	}
    public String getUserUpdateTime() {
        return userUpdateTime;
    }
    public void setUserUpdateTime(String userUpdateTime) {
        this.userUpdateTime = userUpdateTime;
    }

    @Override
    public String toString() {
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("ID:").append(accountId).append(",   ");
        stringBuilder.append("账号名:").append(accountName).append(",   ");
        stringBuilder.append("密码:").append(passwordMD5).append(",   ");
        stringBuilder.append("注册时间:").append(registerTime).append(",   ");
        stringBuilder.append("图片地址:").append(userImageStr).append(",   ");
        stringBuilder.append("用户昵称").append(userNickname).append(",   ");
        stringBuilder.append("性别:").append(userSex).append(",   ");
        stringBuilder.append("个性签名:").append(userDescribeWords).append(",   ");
        stringBuilder.append("更新时间:").append(userUpdateTime).append("\n");
        return stringBuilder.toString();
    }

    //Parcelable序列化
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(accountId);
        dest.writeString(accountName);
        dest.writeString(passwordMD5);
        dest.writeString(registerTime);
        dest.writeString(bindStudentAccount);
        dest.writeString(userImageStr);
        dest.writeString(userNickname);
        dest.writeString(userSex);
        dest.writeString(userDescribeWords);
        dest.writeString(userUpdateTime);
    }

    public static final Parcelable.Creator<UserAccount> CREATOR = new Parcelable.Creator<UserAccount>()
    {
        @Override
        public UserAccount createFromParcel(Parcel source)
        {
            UserAccount userAccount = new UserAccount();
            userAccount.accountId = source.readInt();
            userAccount.accountName = source.readString();
            userAccount.passwordMD5 = source.readString();
            userAccount.registerTime = source.readString();
            userAccount.bindStudentAccount = source.readString();
            userAccount.userImageStr = source.readString();
            userAccount.userNickname = source.readString();
            userAccount.userSex = source.readString();
            userAccount.userDescribeWords = source.readString();
            userAccount.userUpdateTime = source.readString();
            return userAccount;
        }

        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };
}
