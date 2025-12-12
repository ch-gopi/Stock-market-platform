package com.market.quotesservice.service;

public interface FinnhubWebSocketClientInterface {
    void onClose(int code, String reason, boolean remote);
}