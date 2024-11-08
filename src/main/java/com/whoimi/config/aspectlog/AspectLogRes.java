package com.whoimi.config.aspectlog;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AspectLogRes {
//    String ipaddr = "";
    String sn = "";
    String url = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String keyword ;
    String method = "";
    Object response = "";
    String responseTime = "";
    //RT ms
    Long rt = 0L;
//    String authorization = "";

//    public String getIpaddr() {
//        return ipaddr;
//    }
//
//    public void setIpaddr(String ipaddr) {
//        this.ipaddr = ipaddr;
//    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Long getRt() {
        return rt;
    }

    public void setRt(Long rt) {
        this.rt = rt;
    }

//    public String getAuthorization() {
//        return authorization;
//    }
//
//    public void setAuthorization(String authorization) {
//        this.authorization = authorization;
//    }


    @Override
    public String toString() {
        return "AspectLogRes{" +
               "sn='" + sn + '\'' +
               ", url='" + url + '\'' +
               ", keyword='" + keyword + '\'' +
               ", method='" + method + '\'' +
               ", response=" + response +
               ", responseTime='" + responseTime + '\'' +
               ", rt=" + rt +
               '}';
    }
}