package com.andzj.mylibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentInformation implements Parcelable{
	private Integer commentId;
	private String bookIsbn;
	private String commentAccountName;
	private String commentContent;
	private String commentTime;
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	public String getBookIsbn() {
		return bookIsbn;
	}
	public void setBookIsbn(String bookIsbn) {
		this.bookIsbn = bookIsbn;
	}
	public String getCommentAccountName() {
		return commentAccountName;
	}
	public void setCommentAccountName(String commentAccountName) {
		this.commentAccountName = commentAccountName;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public String getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	//parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(commentId);
		dest.writeString(bookIsbn);
		dest.writeString(commentAccountName);
		dest.writeString(commentContent);
		dest.writeString(commentTime);
	}

	public static final Parcelable.Creator<CommentInformation> CREATOR = new Parcelable.Creator<CommentInformation>()
	{
		@Override
		public CommentInformation createFromParcel(Parcel source) {
			CommentInformation commentInformation = new CommentInformation();
			commentInformation.commentId = source.readInt();
			commentInformation.bookIsbn = source.readString();
			commentInformation.commentAccountName = source.readString();
			commentInformation.commentContent = source.readString();
			commentInformation.commentTime = source.readString();

			return commentInformation;
		}

		@Override
		public CommentInformation[] newArray(int size) {
			return new CommentInformation[size];
		}
	};

}
