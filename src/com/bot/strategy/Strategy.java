package com.bot.strategy;

import com.bot.core.Conf.GameConfig;
import com.bot.core.State.GameState;
import com.bot.core.State.PlayerAction;
import com.bot.core.State.Score;
import com.bot.core.Vec2;
import java.util.List;

public interface Strategy {

    /**
     * Called when the game resets.
     * @param score The current score.
     * @param config The static game configuration.
     * @return A list of initial positions for your team's players.
     */
    List<Vec2> onReset(Score score);

    /**
     * Called for every game tick.
     * @param gameState The mutable state of the game for the current tick.
     * @param config The static game configuration.
     * @return A list of actions for each of your team's players.
     */
    List<PlayerAction> onTick(GameState gameState);
}
