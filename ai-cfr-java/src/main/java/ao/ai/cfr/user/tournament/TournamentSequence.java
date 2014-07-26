package ao.ai.cfr.user.tournament;


public interface TournamentSequence<TournamentState, GameState>
{
    TournamentState tournamentState();

    void transition(GameState terminalState, double[] payoffs);
}
