package com.whoimi.config.aspectlog;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;


public class AspectLogReq {
    String ipaddr = "";
    String sn = "";
    String url = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String keyword;
    String method = "";
    Object[] request;
    String requestTime = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String authorization;

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

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

    public Object[] getRequest() {
        return request;
    }

    public void setRequest(Object[] request) {
        this.request = request;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }


    @Override
    public String toString() {
        return "PrintReqInfo{" +
               "ipaddr='" + ipaddr + '\'' +
               ", sn='" + sn + '\'' +
               ", url='" + url + '\'' +
               ", keyword='" + keyword + '\'' +
               ", method='" + method + '\'' +
               ", request=" + Arrays.toString(request) +
               ", requestTime='" + requestTime + '\'' +
               ", authorization='" + authorization + '\'' +
               '}';
    }
}