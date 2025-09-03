package com.bot.core;

import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class State {

    private State() {} // Prevent instantiation

    public static class Team {
        public static final byte Self = 0;
        public static final byte Other = 1;
    }

    /** A sealed interface to represent the exclusive states of ball possession. */
    public sealed interface BallPossession {
        record Possessed(int owner, byte team, int captureTicks) implements BallPossession {}
        record Passing(byte team) implements BallPossession {}
        record Free() implements BallPossession {}
    }

    @Structure.FieldOrder({"self", "other"})
    public static class Score extends Structure {
        public int self;
        public int other;
    }

    @Structure.FieldOrder({"id", "pos", "dir", "speed", "radius", "pickup_radius"})
    public static class PlayerState extends Structure {
        public int id;
        public Vec2 pos;
        public Vec2 dir;
        public float speed;
        public float radius;
        public float pickup_radius;
    }

    @Structure.FieldOrder({"dir", "has_pass", "ball_pass"})
    public static class PlayerAction extends Structure {
        public Vec2 dir;
        public boolean has_pass;
        public Vec2 ball_pass;

        public PlayerAction() {}

        public PlayerAction(Vec2 dir, Vec2 ballPass) {
            this.dir = dir;
            if (ballPass != null) {
                this.has_pass = true;
                this.ball_pass = ballPass;
            } else {
                this.has_pass = false;
                this.ball_pass = new Vec2(0, 0); // Must initialize memory
            }
        }
    }

    @Structure.FieldOrder({"pos", "vel", "radius"})
    public static class BallState extends Structure {
        public Vec2 pos;
        public Vec2 vel;
        public float radius;
    }
    
    @Structure.FieldOrder({"center", "tick"})
    public static class BallStagnationState extends Structure {
        public Vec2 center;
        public int tick;
    }

    public static class BallPossessionType {
        public static final byte Possessed = 0;
        public static final byte Passing = 1;
        public static final byte Free = 2;
    }

    @Structure.FieldOrder({"owner", "team", "capture_ticks"})
    public static class BallPossessedStruct extends Structure {
        public int owner;
        public byte team;
        public int capture_ticks;
    }

    @Structure.FieldOrder({"team"})
    public static class BallPassingStruct extends Structure {
        public byte team;
    }

    public static class BallPossessionUnion extends Union {
        public BallPossessedStruct possessed;
        public BallPassingStruct passing;
    }

    @Structure.FieldOrder({"type", "data"})
    public static class BallPossessionStateStruct extends Structure {
        public byte type;
        public BallPossessionUnion data;
    }
    
    @Structure.FieldOrder({"tick", "ball", "_ball_possession", "ball_stagnation", "players", "score"})
    public static class GameState extends Structure {
        public int tick;
        public BallState ball;
        public BallPossessionStateStruct _ball_possession;
        public BallStagnationState ball_stagnation;
        public PlayerState[] players = new PlayerState[2 * Conf.NUM_PLAYERS];
        public Score score;
        
        public BallPossession getBallPossession() {
            return switch (_ball_possession.type) {
                case BallPossessionType.Possessed -> {
                    _ball_possession.data.setType(BallPossessedStruct.class);
                    _ball_possession.data.read();
                    var p = _ball_possession.data.possessed;
                    yield new BallPossession.Possessed(p.owner, p.team, p.capture_ticks);
                }
                case BallPossessionType.Passing -> {
                    _ball_possession.data.setType(BallPassingStruct.class);
                    _ball_possession.data.read();
                    yield new BallPossession.Passing(_ball_possession.data.passing.team);
                }
                case BallPossessionType.Free -> new BallPossession.Free();
                default -> throw new IllegalStateException("Unknown ball possession type: " + _ball_possession.type);
            };
        }
        
        public boolean isBallFree() {
            return this._ball_possession.type == BallPossessionType.Free;
        }
        
        public Optional<Integer> getBallOwner() {
            if (this._ball_possession.type == BallPossessionType.Possessed) {
                _ball_possession.data.setType(BallPossessedStruct.class);
                _ball_possession.data.read();
                return Optional.of(_ball_possession.data.possessed.owner);
            }
            return Optional.empty();
        }
        
        public Optional<Byte> getTeamOf(int id) {
            if (id < Conf.NUM_PLAYERS) {
                return Optional.of(Team.Self);
            } else if (id < Conf.NUM_PLAYERS * 2) {
                return Optional.of(Team.Other);
            }
            return Optional.empty();
        }
        
        public List<PlayerState> getSelfTeam() {
            return Arrays.asList(Arrays.copyOfRange(players, 0, Conf.NUM_PLAYERS));
        }

        public List<PlayerState> getOtherTeam() {
            return Arrays.asList(Arrays.copyOfRange(players, Conf.NUM_PLAYERS, 2 * Conf.NUM_PLAYERS));
        }
    }
}
