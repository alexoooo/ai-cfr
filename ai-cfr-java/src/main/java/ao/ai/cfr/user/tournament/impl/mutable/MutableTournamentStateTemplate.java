package ao.ai.cfr.user.tournament.impl.mutable;

import ao.ai.cfr.user.tournament.impl.TournamentStateTemplate;

public interface MutableTournamentStateTemplate<GameState, TournamentState extends MutableTournamentStateTemplate<GameState, TournamentState>>
        extends TournamentStateTemplate
{
    void transition(GameState terminalState, double[] payoffs);

    TournamentState copy();
}
