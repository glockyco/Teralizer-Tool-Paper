package bank;

public class LoanPolicy {
  static final int FULL = 60, PARTIAL = 5;

  public static int approvedAmount(int score, int amount) {
    if (amount <= 0) throw new IllegalArgumentException();
    int limit = score > 80 ? FULL : score >= 50 ? PARTIAL : 0;
    return Math.min(amount, limit);
  }
}
