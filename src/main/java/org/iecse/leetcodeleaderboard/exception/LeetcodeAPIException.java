package org.iecse.leetcodeleaderboard.exception;

public class LeetcodeAPIException extends RuntimeException{

    public LeetcodeAPIException(String message) {
        super(message);
    }

    public LeetcodeAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
