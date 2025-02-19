package com.ruyiruyi.rylibrary.db;

import com.ruyiruyi.rylibrary.request.RequestUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "requestaddress")
public class Requestaddress {
    @Column(name = "id",isId = true,autoGen = true)
    private String id;
    @Column(name = "requsturl")
    private String requstUrl = RequestUtils.REQUEST_URL_BASE();
    @Column(name = "ifinternet")
    private boolean ifinternet = true;

    public String getRequstUrl() {
        return requstUrl;
    }

    public void setRequstUrl(String requstUrl) {
        this.requstUrl = requstUrl;
    }

    public boolean isIfinternet() {
        return ifinternet;
    }

    public void setIfinternet(boolean ifinternet) {
        this.ifinternet = ifinternet;
    }
}
