package com.andzj.mylibrary.bean;

import java.util.List;

/**
 * Created by zj on 2016/11/18.
 */

public class BorrowResult {
    private List<BorrowInformation> data;
    private String info;
    private Integer size;

    public List<BorrowInformation> getData() {
        return data;
    }

    public void setData(List<BorrowInformation> data) {
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
