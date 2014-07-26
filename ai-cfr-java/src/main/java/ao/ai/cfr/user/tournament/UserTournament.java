package ao.ai.cfr.user.tournament;


public interface UserTournament<TournamentState, GameState>
{
    TournamentState initialTournamentState();

    int userPlayer(TournamentState tournamentState);

    TournamentState transition(TournamentState tournamentState, GameState terminalState, double[] payoffs);

    TournamentSequence<TournamentState, GameState> newTournamentSequence();
}
