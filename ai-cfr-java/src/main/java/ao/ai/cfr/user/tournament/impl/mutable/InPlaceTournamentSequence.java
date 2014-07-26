package ao.ai.cfr.user.tournament.impl.mutable;


import ao.ai.cfr.user.tournament.TournamentSequence;

public class InPlaceTournamentSequence<GameState, TournamentState extends MutableTournamentStateTemplate<GameState, TournamentState>>
        implements TournamentSequence<TournamentState, GameState>
{
    private final TournamentState tournamentState;


    public InPlaceTournamentSequence(TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }


    @Override
    public TournamentState tournamentState() {
        return tournamentState;
    }


    @Override
    public void transition(GameState terminalState, double[] payoffs) {
        tournamentState.transition(terminalState, payoffs);
    }
}
