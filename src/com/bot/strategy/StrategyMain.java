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

    // Config will be initialized 
    public static GameConfig config;

    /**
     * This function tells the engine what strategy you want your bot to use
     */
    public static Strategy getStrategy(int team) {
        // realTeam == 0 means I am on the left
        // realTeam == 1 means I am on the right
        
        if (team == 0) {
            System.out.println("Hello! I am team A (on the left)");
            return new MyStrategy();
        } else {
            System.out.println("Hello! I am team B (on the right)");
            return new DoNothing();
        }
        
        // NOTE when actually submitting your bot, you probably want to have the SAME strategy for both
        // sides.
    }

    /**
     * This is the class where you define your bot's logic.
     */
    private static class MyStrategy implements Strategy {

        /**
         * The engine will call this function every time the field is reset:
         * either after a goal, if the ball has not moved for too long, or right before endgame
         */
        @Override
        public List<Vec2> onReset(Score score) {
            Vec2 field = config.field.bottomRight();
            
            return Arrays.asList(
                new Vec2(field.x * 0.1f, field.y * 0.5f),
                new Vec2(field.x * 0.4f, field.y * 0.4f),
                new Vec2(field.x * 0.4f, field.y * 0.5f),
                new Vec2(field.x * 0.4f, field.y * 0.6f)
            );
        }

        /**
         * Very simple strategy to chase the ball and shoot on goal
         */
        @Override
        public List<PlayerAction> onTick(GameState gameState) {
            // NOTE Do not worry about what side your bot is on! 
            // The engine mirrors the world for you if you are on the right, 
            // so to you, you always appear on the left.
            
            List<PlayerAction> actions = new ArrayList<>();
            Vec2 goalOther = config.field.goalOther();
            
            for (PlayerState me : gameState.getSelfTeam()) {
                Vec2 moveDirection = gameState.ball.pos.subtract(me.pos);
                actions.add(new PlayerAction(moveDirection, goalOther.subtract(me.pos));
            }
            return actions;
        }
    }

    /**
     * This strategy will do nothing :(
     */
    private static class DoNothing implements Strategy {
        @Override
        public List<Vec2> onReset(Score score) {
            Vec2 field = config.field.bottomRight();
            
            return Arrays.asList(
                new Vec2(field.x * 0.1f, field.y * 0.5f),
                new Vec2(field.x * 0.4f, field.y * 0.4f),
                new Vec2(field.x * 0.4f, field.y * 0.5f),
                new Vec2(field.x * 0.4f, field.y * 0.6f)
            );
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
