package com.andzj.mylibrary.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class BookInformation implements Parcelable
{
	private Integer bookId;
	private String bookIsbn;
	private String bookImageAddress;
	private String bookName;
	private String bookAuthor;
	private Double bookPrice;
	private String bookPublishCompany;
	private String bookPublishTime;
	private String bookSummary;
	
	private Integer bookTotalNumber;
	private Integer bookRemainNumber;
	private Double bookAverageScore;
	private Integer bookScoreNumber;
	private String bookPosition;
	private String bookKeyWords;
	private String bookNotes;
	private String operateAccountName;
	private String operateTime;

    public BookInformation(){}

    public BookInformation(Integer bookId, String bookIsbn, String bookImageAddress,
                           String bookName, String bookAuthor,Double bookPrice,
                           String bookPublishCompany,String bookPublishTime,String bookSummary,
                           Integer bookTotalNumber,Integer bookRemainNumber,
                           Double bookAverageScore,Integer bookScoreNumber,
                           String bookPosition,String bookKeyWords,String bookNotes,
                           String operateAccountName,String operateTime)
    {
        this.bookId = bookId;
        this.bookIsbn = bookIsbn;
        this.bookImageAddress = bookImageAddress;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookPrice = bookPrice;
        this.bookPublishCompany = bookPublishCompany;
        this.bookPublishTime = bookPublishTime;
        this.bookTotalNumber = bookTotalNumber;
        this.bookRemainNumber = bookRemainNumber;
        this.bookAverageScore = bookAverageScore;
        this.bookScoreNumber = bookScoreNumber;
        this.bookPosition = bookPosition;
        this.bookKeyWords = bookKeyWords;
        this.bookNotes = bookNotes;
        this.operateAccountName = operateAccountName;
        this.operateTime = operateTime;
    }
	
	public Integer getBookId() {
		return bookId;
	}
	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}
	public String getBookIsbn() {
		return bookIsbn;
	}
	public void setBookIsbn(String bookIsbn) {
		this.bookIsbn = bookIsbn;
	}
	public String getBookImageAddress() {
		return bookImageAddress;
	}
	public void setBookImageAddress(String bookImageAddress) {
		this.bookImageAddress = bookImageAddress;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookAuthor() {
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}
	public Double getBookPrice() {
		return bookPrice;
	}
	public void setBookPrice(Double bookPrice) {
		this.bookPrice = bookPrice;
	}
	public String getBookPublishCompany() {
		return bookPublishCompany;
	}
	public void setBookPublishCompany(String bookPublishCompany) {
		this.bookPublishCompany = bookPublishCompany;
	}
	public String getBookPublishTime() {
		return bookPublishTime;
	}
	public void setBookPublishTime(String bookPublishTime) {
		this.bookPublishTime = bookPublishTime;
	}

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public Integer getBookTotalNumber() {
		return bookTotalNumber;
	}
	public void setBookTotalNumber(Integer bookTotalNumber) {
		this.bookTotalNumber = bookTotalNumber;
	}
	public Integer getBookRemainNumber() {
		return bookRemainNumber;
	}
	public void setBookRemainNumber(Integer bookRemainNumber) {
		this.bookRemainNumber = bookRemainNumber;
	}
	public Double getBookAverageScore() {
		return bookAverageScore;
	}
	public void setBookAverageScore(Double bookAverageScore) {
		this.bookAverageScore = bookAverageScore;
	}
	public Integer getBookScoreNumber() {
		return bookScoreNumber;
	}
	public void setBookScoreNumber(Integer bookScoreNumber) {
		this.bookScoreNumber = bookScoreNumber;
	}
	public String getBookPosition() {
		return bookPosition;
	}
	public void setBookPosition(String bookPosition) {
		this.bookPosition = bookPosition;
	}
	public String getBookKeyWords() {
		return bookKeyWords;
	}
	public void setBookKeyWords(String bookKeyWords) {
		this.bookKeyWords = bookKeyWords;
	}
	public String getBookNotes() {
		return bookNotes;
	}
	public void setBookNotes(String bookNotes) {
		this.bookNotes = bookNotes;
	}
	public String getOperateAccountName() {
		return operateAccountName;
	}
	public void setOperateAccountName(String operateAccountName) {
		this.operateAccountName = operateAccountName;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	//重载toString函数,输入一本书的信息
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id:").append(bookId).append("  ");
        builder.append("书名:<").append(bookName).append(">  ");
        builder.append("ISBN:").append(bookIsbn).append("  ");
        builder.append("作者:").append(bookAuthor).append("  ");
        builder.append("价格:").append(bookPrice).append("  ");
        builder.append("出版社:").append(bookPublishCompany).append("  ");
        builder.append("出版时间:").append(bookPublishTime).append("  ");
        builder.append("简介:").append(bookSummary).append("  ");
        builder.append("总数量:").append(bookTotalNumber).append("  ");
        builder.append("不可借数量:").append(bookRemainNumber).append("  ");
        builder.append("评分:").append(bookAverageScore).append("  ");
        builder.append("评分人数:").append(bookScoreNumber).append("  ");
        builder.append("存放位置:").append(bookPosition).append("  ");
        builder.append("关键字:").append(bookKeyWords).append("  ");
        builder.append("备注:").append(bookNotes).append("  ");
        builder.append("管理员:").append(operateAccountName).append("  ");
        builder.append("录入时间:").append(operateTime).append("  ");
		builder.append("照片地址:").append(bookImageAddress).append("\n");

        return builder.toString();
    }

    //Parcelable序列化
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookIsbn);
        dest.writeString(bookImageAddress);
        dest.writeString(bookName);
        dest.writeString(bookAuthor);
        dest.writeDouble(bookPrice);
        dest.writeString(bookPublishCompany);
        dest.writeString(bookPublishTime);
        dest.writeString(bookSummary);

        dest.writeInt(bookTotalNumber);
        dest.writeInt(bookRemainNumber);
        dest.writeDouble(bookAverageScore);
        dest.writeInt(bookScoreNumber);
        dest.writeString(bookPosition);
        dest.writeString(bookKeyWords);
        dest.writeString(bookNotes);
        dest.writeString(operateAccountName);
        dest.writeString(operateTime);
    }
    public static final Parcelable.Creator<BookInformation> CREATOR = new Parcelable.Creator<BookInformation>()
    {
        @Override
        public BookInformation createFromParcel(Parcel result) {
            BookInformation bookInformation = new BookInformation();
            bookInformation.bookId = result.readInt();
            bookInformation.bookIsbn = result.readString();
            bookInformation.bookImageAddress = result.readString();
            bookInformation.bookName = result.readString();
            bookInformation.bookAuthor = result.readString();
            bookInformation.bookPrice = result.readDouble();
            bookInformation.bookPublishCompany = result.readString();
            bookInformation.bookPublishTime = result.readString();
            bookInformation.bookSummary = result.readString();

            bookInformation.bookTotalNumber = result.readInt();
            bookInformation.bookRemainNumber = result.readInt();
            bookInformation.bookAverageScore = result.readDouble();
            bookInformation.bookScoreNumber = result.readInt();
            bookInformation.bookPosition = result.readString();
            bookInformation.bookKeyWords = result.readString();
            bookInformation.bookNotes = result.readString();
            bookInformation.operateAccountName = result.readString();
            bookInformation.operateTime = result.readString();
            return bookInformation;
        }

        @Override
        public BookInformation[] newArray(int size) {
            return new BookInformation[size];
        }
    };

}
