package com.andzj.mylibrary.bean;

import android.os.DeadObjectException;
import android.os.Parcel;
import android.os.Parcelable;

public class BorrowInformation implements  Parcelable{
	private Integer borrowId;
	private String bookIsbn;
	private String borrowAccountName;
	private String borrowTime;
	private String returnTime;
	private String borrowState;

	public BorrowInformation(){}

    public BorrowInformation(Integer borrowId,String bookIsbn,  String borrowAccountName, String borrowTime, String returnTime, String borrowState) {
        this.borrowId = borrowId;
        this.bookIsbn = bookIsbn;
        this.borrowAccountName = borrowAccountName;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.borrowState = borrowState;
    }

    public Integer getBorrowId() {
		return borrowId;
	}
	public void setBorrowId(Integer borrowId) {
		this.borrowId = borrowId;
	}
	public String getBookIsbn() {
		return bookIsbn;
	}
	public void setBookIsbn(String bookIsbn) {
		this.bookIsbn = bookIsbn;
	}
	public String getBorrowAccountName() {
		return borrowAccountName;
	}
	public void setBorrowAccountName(String borrowAccountName) {
		this.borrowAccountName = borrowAccountName;
	}
	public String getBorrowTime() {
		return borrowTime;
	}
	public void setBorrowTime(String borrowTime) {
		this.borrowTime = borrowTime;
	}
	public String getReturnTime() {
		return returnTime;
	}
	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}
	public String getBorrowState() {
		return borrowState;
	}
	public void setBorrowState(String borrowState) {
		this.borrowState = borrowState;
	}


    //parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(borrowId);
        dest.writeString(bookIsbn);
        dest.writeString(borrowAccountName);
        dest.writeString(borrowTime);
        dest.writeString(returnTime);
        dest.writeString(borrowState);
    }

    public static final Parcelable.Creator<BorrowInformation> CREATOR = new Parcelable.Creator<BorrowInformation>()
    {
        @Override
        public BorrowInformation createFromParcel(Parcel source) {
            BorrowInformation borrowInformation = new BorrowInformation();
            borrowInformation.borrowId = source.readInt();
            borrowInformation.bookIsbn = source.readString();
            borrowInformation.borrowAccountName = source.readString();
            borrowInformation.borrowTime = source.readString();
            borrowInformation.returnTime = source.readString();
            borrowInformation.borrowState = source.readString();

            return borrowInformation;
        }

        @Override
        public BorrowInformation[] newArray(int size) {
            return new BorrowInformation[size];
        }
    };
	
}
