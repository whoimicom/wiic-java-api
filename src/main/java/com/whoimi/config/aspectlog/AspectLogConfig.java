package com.whoimi.config.aspectlog;

/**
 * print config
 */
public class AspectLogConfig {
    boolean pretty = true;
    boolean printReqRunner = true;
    boolean printResRunner = true;
    String keyword;

    public boolean isPretty() {
        return pretty;
    }

    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }

    public boolean isPrintReqRunner() {
        return printReqRunner;
    }

    public void setPrintReqRunner(boolean printReqRunner) {
        this.printReqRunner = printReqRunner;
    }

    public boolean isPrintResRunner() {
        return printResRunner;
    }

    public void setPrintResRunner(boolean printResRunner) {
        this.printResRunner = printResRunner;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}