package antifraud.Transaction;

public enum Result {
    ALLOWED(200),
    MANUAL_PROCESSING(1500),
    PROHIBITED(Long.MAX_VALUE);

    private long maxValue;

    Result(long maxValue) {
        this.maxValue = maxValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    public static void increaseMaxValue(String resultName, long amount) {
        long newLimit;
        for (Result value : values()) {
            if (value.name().equals(resultName)) {
                newLimit = (long) Math.ceil(0.8 * value.getMaxValue() + 0.2 * amount);
                        value.setMaxValue(newLimit);
            }
        }
    }

    public static void decreaseMaxValue(String resultName, long amount) {
        long newLimit;
        for (Result value : values()) {
            if (value.name().equals(resultName)) {
                newLimit = (long) Math.ceil(0.8 * value.getMaxValue() - 0.2 * amount);
                value.setMaxValue(newLimit);
            }
        }
    }


}
