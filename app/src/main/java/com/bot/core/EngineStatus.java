package com.bot.core;

public final class EngineStatus {
    public static final byte Ready = 0;
    public static final byte Busy = 1;
    public static final byte Finished = 2;

    private EngineStatus() {} // prevent instantiation
}
