package com.bot.strategy;

import com.bot.core.Conf;
import com.bot.core.Conf.GameConfig;
import com.bot.core.State.*;
import com.bot.core.Vec2;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StrategyMain {

    // These will be initialized 
    public static int realTeam; 
    public static GameConfig config;

    public static Strategy getStrategy() {
       /**
        * Change which strategy you use here:
        * return new DoNothing();
        *
        * To test two different strategies, use the realTeam variable:
        *
        * if (realTeam == 0) {
        *   return new MyStrategy();
        * } else {
        *   return new DoNothing();
        * }
        *
        */

        return new MyStrategy();
    }

    /**
     * This is the class where you define your bot's logic.
     */
    private static class MyStrategy implements Strategy {

        @Override
        public List<Vec2> onReset(Score score) {
            System.out.printf("Resetting strategy... Score: %d-%d%n", score.self, score.other);
            List<Vec2> initialPositions = new ArrayList<>();
            for (int i = 0; i < Conf.NUM_PLAYERS; i++) {
                initialPositions.add(new Vec2(0, 0));
            }
            return initialPositions;
        }

        @Override
        public List<PlayerAction> onTick(GameState gameState) {
            List<PlayerAction> actions = new ArrayList<>();
            
            // Example Logic: Every player tries to move towards the ball.
            Vec2 ballPos = gameState.ball.pos;

            BallPossession possession = gameState.getBallPossession();
            if (possession instanceof BallPossession.Possessed p) {
                 System.out.printf("Player %d has the ball!%n", p.owner());
            }

            for (PlayerState me : gameState.getSelfTeam()) {
                Vec2 moveDirection = ballPos.subtract(me.pos).normalize();
                actions.add(new PlayerAction(moveDirection, null));
            }
            return actions;
        }
    }

    /**
     * You can have multiple strategies for testing, just swap them out in getStrategy()
     */
    private static class DoNothing implements Strategy {
        @Override
        public List<Vec2> onReset(Score score) {
            List<Vec2> initialPositions = new ArrayList<>();
            for (int i = 0; i < Conf.NUM_PLAYERS; i++) {
                initialPositions.add(new Vec2(0, 0));
            }
            return initialPositions;
        }

        @Override
        public List<PlayerAction> onTick(GameState gameState) {
            List<PlayerAction> actions = new ArrayList<>();
            for (int i = 0; i < Conf.NUM_PLAYERS; i++) {
                actions.add(new PlayerAction(new Vec2(0, 0), null));
            }
            return actions;
        }
    }
}
