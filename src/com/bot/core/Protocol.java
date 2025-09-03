package com.bot.core;

import com.bot.core.Conf.*;
import com.bot.core.State.*;
import com.sun.jna.Structure;
import com.sun.jna.Union;

public class Protocol {

    public static final long HANDSHAKE_BOT = 0xabe119c019aaffccL;

    public static class ProtocolId {
        public static final byte HandshakeMsg = 0;
        public static final byte HandshakeResponse = 1;
        public static final byte ResetMsg = 2;
        public static final byte ResetResponse = 3;
        public static final byte TickMsg = 4;
        public static final byte TickResponse = 5;
    }

    @Structure.FieldOrder({"team", "config"})
    public static class HandshakeMsg extends Structure {
        public byte team;
        public GameConfig config;
    }

    public static class ProtocolUnion extends Union {
        public HandshakeMsg handshake_msg;
        public long handshake_response;
        public Score reset_msg;
        public Vec2[] reset_response = new Vec2[Conf.NUM_PLAYERS];
        public GameState tick_msg; 
        public PlayerAction[] tick_response = new PlayerAction[Conf.NUM_PLAYERS];
    }
    
    @Structure.FieldOrder({"type", "data"})
    public static class ProtocolStruct extends Structure {
        public byte type;
        public ProtocolUnion data;
    }
    
    @Structure.FieldOrder({"sync", "protocol"})
    public static class Shm extends Structure {
        public byte sync;
        public ProtocolStruct protocol;
    }
}
