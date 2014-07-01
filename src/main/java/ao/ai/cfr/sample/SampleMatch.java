package ao.ai.cfr.sample;


import ao.ai.cfr.ExtensiveGame;
import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.ExtensivePlayer;
import ao.ai.cfr.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleMatch
{
    public static void main(String[] args) {
        playout(new RpsGame());
    }

    private static <S, I, A> void playout(ExtensiveGame<S, I, A> game) {
        Random random = new Random();

        List<ExtensivePlayer<I, A>> players = new ArrayList<ExtensivePlayer<I, A>>();
        for (int i = 0; i < game.playerCount(); i++) {
            players.add(new RandomPlayer<I, A>(random));
        }

        ExtensiveMatch<S, I, A> match = game.newMatch();

        while (match.node().type() != NodeType.TERMINAL) {
            A action;

            switch (match.node().type()) {
                case DECISION: {
                    Decision<I, A> decision = match.node().asDecision();

                    ExtensivePlayer<I, A> player = players.get(decision.player());
                    action = player.act(decision.informationSet(), decision.actions());

                    System.out.println(decision.player() + "\t" + action);
                    break;
                }

                case CHANCE: {
                    Chance<I, A> chance = match.node().asChance();
                    action = chance.outcomes().sample(random);

                    System.out.println("chance\t" + action);
                    break;
                }

                default: throw new Error();
            }

            match.transition(action);
        }

        System.out.println("payoffs\t" + match.node());
    }
}
