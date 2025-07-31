// WorkoutStatus ENUM
package com.comwith.fitlog.log.entity;

public enum WorkoutStatus {
    COMPLETE("O", 100),    // 완료(100점)
    PARTIAL("△", 50),      // 부분 수행(50점)
    INCOMPLETE("X", 0);    // 미수행(0점)

    private final String symbol;
    private final int score;

    WorkoutStatus(String symbol, int score) {
        this.symbol = symbol;
        this.score = score;
    }

    public String getSymbol() { return symbol; }
    public int getScore() { return score; }
}


