package ao.ai.cfr.user;


import ao.ai.cfr.node.Terminal;

public interface GameView<TournamentState, GameState, InformationSet, Action>
{
    void matchStarted(TournamentState tournamentState);


    void userToAct();

    void userActed(Action action, GameState gameState);


    void botToAct();

    void botActed(Action action, GameState gameState);


    void chanceToAct();

    void chanceActed();

    void matchEnded(double userPayoff, Terminal<InformationSet, Action> informationSetActionTerminal);
}
