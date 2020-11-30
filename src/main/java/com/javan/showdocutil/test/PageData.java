package com.javan.showdocutil.test;

import java.util.List;

public class PageData<T> {
    private Long total;

    private List<T> t;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public PageData(Long total, List<T> t) {
        this.total = total;
        this.t = t;
    }
}
