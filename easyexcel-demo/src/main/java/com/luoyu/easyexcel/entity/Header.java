package com.luoyu.easyexcel.entity;

import com.alibaba.excel.annotation.ExcelProperty;

public class Header {
    @ExcelProperty("列1")
    private String title1;

    @ExcelProperty("列2")
    private String title2;

    @ExcelProperty("列3")
    private String title3;

    @ExcelProperty("列4")
    private String title4;

    @ExcelProperty("列5")
    private String title5;

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getTitle4() {
        return title4;
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }

    public String getTitle5() {
        return title5;
    }

    public void setTitle5(String title5) {
        this.title5 = title5;
    }
}
