package com.whoimi.config.aspectlog;

/**
 * 运行数据
 */
public  class AspectLogRunner {
    private String sn;
    private Long start;
    private Long end;
    private Long rt;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getRt() {
        return rt;
    }

    public void setRt(Long rt) {
        this.rt = rt;
    }
}