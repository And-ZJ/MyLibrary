package com.andzj.mylibrary.bean;

import java.util.List;

/**
 * Created by zj on 2016/11/23.
 */

public class BorrowHistoryResult {
    private List<BorrowHistoryInformation> data;
    private String info;
    private Integer size;

    public List<BorrowHistoryInformation> getData() {
        return data;
    }

    public void setData(List<BorrowHistoryInformation> data) {
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
