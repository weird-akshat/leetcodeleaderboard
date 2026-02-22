package org.iecse.leetcodeleaderboard.exception;

public class LeetcodeIdNotVerifiedException extends RuntimeException{
    public LeetcodeIdNotVerifiedException(){
        super("Leet-Code ID not verified, please enter the username part of your emailId in your leetCode account bio");
    }
    public LeetcodeIdNotVerifiedException(String message) {
        super(message);
    }

    public LeetcodeIdNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
