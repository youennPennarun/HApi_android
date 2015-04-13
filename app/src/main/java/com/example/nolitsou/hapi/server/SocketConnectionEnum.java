package com.example.nolitsou.hapi.server;

public enum SocketConnectionEnum {
    CONNECTED(100, "connected"),
    DISCONNECTED(101, "disconnected"),
    UNKNOWN_ERROR(102, "connection error"),
    INVALID_HOST(103, "Invalid host"),
    INVALID_CREDENTIALS(104, "Invalid credentials");

    private int code;
    private String msg;

    SocketConnectionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "(" +code + "): " + msg;
    }

}
