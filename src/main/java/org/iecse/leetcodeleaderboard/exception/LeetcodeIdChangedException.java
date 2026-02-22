package org.iecse.leetcodeleaderboard.exception;

public class LeetcodeIdChangedException extends RuntimeException{
    public LeetcodeIdChangedException(){
        super("User's leet-code account details not fetched for 2 days. User likely changed their leetcodeId");
    }
    public LeetcodeIdChangedException(String message) {
        super(message);
    }

    public LeetcodeIdChangedException(String message, Throwable cause) {
        super(message, cause);
    }
}
