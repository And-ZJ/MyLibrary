package com.andzj.mylibrary.bean;

import java.util.List;

/**
 * Created by zj on 2016/11/17.
 */

public class BookResult {
    private List<BookInformation> data;
    private String info;
    private Integer size;

    public List<BookInformation> getData() {
        return data;
    }

    public void setData(List<BookInformation> data) {
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
