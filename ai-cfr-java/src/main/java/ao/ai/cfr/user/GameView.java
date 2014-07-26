package ao.ai.cfr.user;


public interface GameView
{
    void matchStarted();


    void userToAct();

    void userActed();


    void botToAct();

    void botActed();


    void chanceToAct();

    void chanceActed();


    void matchEnded(double payoff);
}
