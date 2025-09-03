package com.kbsw.seasonthon.recommend.llm;

public interface LlmClient {
    String complete(String prompt);
}