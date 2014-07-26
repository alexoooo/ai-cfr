package ao.ai.cfr.user.tournament.impl.immutable;


import ao.ai.cfr.user.tournament.UserTournament;
import ao.ai.cfr.user.tournament.TournamentSequence;

public class CopyingTournamentSequence<TournamentState, GameState>
        implements TournamentSequence<TournamentState, GameState>
{
    private final UserTournament<TournamentState, GameState> tournament;

    private TournamentState tournamentState;


    public CopyingTournamentSequence(UserTournament<TournamentState, GameState> tournament) {
        this.tournament = tournament;

        tournamentState = tournament.initialTournamentState();
    }

    @Override
    public TournamentState tournamentState() {
        return tournamentState;
    }

    @Override
    public void transition(GameState terminalState, double[] payoffs) {
        tournamentState = tournament.transition(tournamentState, terminalState, payoffs);
    }
}
