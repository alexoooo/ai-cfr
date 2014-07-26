package ao.ai.cfr.user.tournament.impl.concrete;


public class UserPayoffTournamentState
{
    public static UserPayoffTournamentState createEmpty(int playerCount) {
        return new UserPayoffTournamentState(0, new double[playerCount], 0);
    }


    private final int userPlayer;
    private final double[] payoffSum;
    private final int matchCount;


    protected UserPayoffTournamentState(int userPlayer, double[] payoffSum, int matchCount)
    {
        this.userPlayer = userPlayer;
        this.payoffSum = payoffSum;
        this.matchCount = matchCount;
    }


    public int userPlayer() {
        return userPlayer;
    }

    public double payoffSum(int user) {
        return payoffSum[user];
    }
    public double payoffMean(int user) {
        return payoffSum(user) / matchCount;
    }

    public int matchCount() {
        return matchCount;
    }


    public UserPayoffTournamentState transition(double[] payoffs) {
        int nextUserPlayer = userPlayer;

        double[] nextPayoffSum;
        if (payoffSum.length == payoffs.length) {
            nextPayoffSum = payoffSum.clone();
            for (int i = 0; i < payoffs.length; i++) {
                nextPayoffSum[i] += payoffs[i];
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "Expected %s payoffs, but got %s", payoffSum.length, payoffs.length));
        }

        int nextMatchCount = matchCount + 1;

        return new UserPayoffTournamentState(
                nextUserPlayer, nextPayoffSum, nextMatchCount);
    }

    public UserPayoffTournamentState withUserPlayer(int userPlayer) {
        return new UserPayoffTournamentState(
                userPlayer, payoffSum, matchCount);
    }
}
