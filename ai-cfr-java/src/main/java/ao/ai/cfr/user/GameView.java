package ao.ai.cfr.user;


public interface GameView<TournamentState, GameState, InformationSet, Action>
{
    void matchStarted(TournamentState tournamentState);


    void userToAct();

    void userActed();


    void botToAct();

    void botActed();


    void chanceToAct();

    void chanceActed();


    void matchEnded(double payoff);
}
