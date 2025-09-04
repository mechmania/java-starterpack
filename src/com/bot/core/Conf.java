package com.bot.core;

import com.sun.jna.Structure;
import java.util.List;

public class Conf {

    public static final int NUM_PLAYERS = 4;

    @Structure.FieldOrder({"friction", "radius", "capture_ticks", "stagnation_radius", "stagnation_ticks"})
    public static class BallConfig extends Structure {
        public float friction;
        public float radius;
        public int capture_ticks;
        public float stagnation_radius;
        public int stagnation_ticks;
    }

    @Structure.FieldOrder({"radius", "pickup_radius", "speed", "pass_speed", "pass_error", "possession_slowdown"})
    public static class PlayerConfig extends Structure {
        public float radius;
        public float pickup_radius;
        public float speed;
        public float pass_speed;
        public float pass_error;
        public float possession_slowdown;
    }

    @Structure.FieldOrder({"width", "height"})
    public static class FieldConfig extends Structure {
        public int width;
        public int height;

        public Vec2 center() {
            return new Vec2(this.width * 0.5f, this.height * 0.5f);
        }

        public Vec2 bottomRight() {
            return new Vec2(this.width, this.height);
        }

        public Vec2 goal_a() {
            return new Vec2(0, this.height * 0.5f);
        }

        public Vec2 goal_b() {
            return new Vec2(this.width, this.height * 0.5f);
        }
    }

    @Structure.FieldOrder({"normal_height", "thickness", "penalty_box_width", "penalty_box_height", "penalty_box_radius" })
    public static class GoalConfig extends Structure {
        public int normal_height;
        public int thickness;
        public int penalty_box_width;
        public int penalty_box_height;
        public int penalty_box_radius;


        public int currentHeight(GameConfig conf, int tick) {
            if (tick < conf.max_ticks) {
                return this.normal_height;
            } else {
                return this.penalty_box_height;
            }
        }
    }

    @Structure.FieldOrder({"max_ticks", "endgame_ticks", "spawn_ball_dist", "ball", "player", "field", "goal"})
    public static class GameConfig extends Structure {
        public int max_ticks;
        public int endgame_ticks;
        public float spawn_ball_dist;
        public BallConfig ball;
        public PlayerConfig player;
        public FieldConfig field;
        public GoalConfig goal;
        
        public void copyFrom(GameConfig source) {
            this.getPointer().write(0, source.getPointer().getByteArray(0, source.size()), 0, source.size());
            this.read();
        }
    }
}
