package com.andzj.mylibrary.bean;


import java.util.List;

/**
 * Created by zj on 2016/11/18.
 */

public class CommentResult
{
    private List<CommentInformation> data;
    private String info;
    private Integer size;

    public List<CommentInformation> getData() {
        return data;
    }

    public void setData(List<CommentInformation> data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
