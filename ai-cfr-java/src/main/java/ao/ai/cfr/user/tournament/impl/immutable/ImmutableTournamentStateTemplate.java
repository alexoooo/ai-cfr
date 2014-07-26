package ao.ai.cfr.user.tournament.impl.immutable;


import ao.ai.cfr.user.tournament.impl.TournamentStateTemplate;

public interface ImmutableTournamentStateTemplate<GameState, T extends ImmutableTournamentStateTemplate<GameState, T>>
    extends TournamentStateTemplate
{
    T transition(GameState terminalState, double[] payoffs);
}
