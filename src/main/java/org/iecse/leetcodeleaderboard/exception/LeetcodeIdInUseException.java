package org.iecse.leetcodeleaderboard.exception;

public class LeetcodeIdInUseException extends RuntimeException{
    public LeetcodeIdInUseException(){
        super("Leet-Code ID already in use by another user");
    }
    public LeetcodeIdInUseException(String message) {
        super(message);
    }

    public LeetcodeIdInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
