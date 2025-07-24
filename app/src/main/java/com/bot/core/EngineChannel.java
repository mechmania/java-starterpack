package com.bot.core;

import com.bot.core.Conf.GameConfig;
import com.bot.core.Protocol.*;
import com.bot.core.State.*;
import com.bot.strategy.Strategy;
import com.bot.strategy.StrategyMain;
import com.sun.jna.Pointer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class EngineChannel implements Closeable {

    private final RandomAccessFile file;
    private final MappedByteBuffer buffer;
    private final Shm shm;

    private EngineChannel(String path) throws IOException {
        File f = new File(path);
        this.file = new RandomAccessFile(f, "rw");
        FileChannel channel = file.getChannel();
        this.buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());
        this.shm = new Shm();
    }

    public static EngineChannel fromPath(String path) throws IOException {
        return new EngineChannel(path);
    }

    private void poll(byte expectedStatus) throws InterruptedException {
        int i = 0;
        while (true) {
            if (buffer.get(0) == expectedStatus) {
                return;
            }
            if (i < 100) {
                // busy-wait
            } else if (i < 1000) {
                Thread.yield();
            } else {
                Thread.sleep(i / 10000L);
            }
            i++;
        }
    }

    public void handleHandshake() throws InterruptedException {
        poll(EngineStatus.Ready);

        byte[] data = new byte[shm.size()];
        buffer.position(0);
        buffer.get(data);
        shm.getPointer().write(0, data, 0, data.length);
        shm.read();

        assert(shm.protocol.type == ProtocolId.HandshakeMsg);

        shm.protocol.data.setType(HandshakeMsg.class);
        shm.protocol.data.read();

        StrategyMain.realTeam = shm.protocol.data.handshake_msg.team;
        GameConfig shmConfig = shm.protocol.data.handshake_msg.config;
        StrategyMain.config = new GameConfig();
        StrategyMain.config.copyFrom(shmConfig);

        shm.protocol.data.setType("handshake_response");
        shm.protocol.data.handshake_response = Protocol.HANDSHAKE_BOT;

        shm.protocol.type += 1;
        shm.sync = EngineStatus.Busy;

        shm.write();
        Pointer p = shm.getPointer();
        p.read(0, data, 0, data.length);
        buffer.position(0);
        buffer.put(data);
    }

    public void handleMsg(Strategy strategy) throws InterruptedException {
        poll(EngineStatus.Ready);

        byte[] data = new byte[shm.size()];
        buffer.position(0);
        buffer.get(data);
        shm.getPointer().write(0, data, 0, data.length);
        shm.read();

        switch (shm.protocol.type) {
            case ProtocolId.ResetMsg:
                shm.protocol.data.setType(Score.class);
                shm.protocol.data.read();
                List<Vec2> resetResponse = strategy.onReset(shm.protocol.data.reset_msg);
                shm.protocol.data.setType("reset_response");
                for (int i = 0; i < Math.min(resetResponse.size(), Conf.NUM_PLAYERS); i++) {
                    shm.protocol.data.reset_response[i] = resetResponse.get(i);
                }
                break;

            case ProtocolId.TickMsg:
                shm.protocol.data.setType(GameState.class);
                shm.protocol.data.read();
                GameState tickMsg = shm.protocol.data.tick_msg;
                List<PlayerAction> tickResponse = strategy.onTick(tickMsg);
                
                shm.protocol.data.setType("tick_response");
                for (int i = 0; i < Math.min(tickResponse.size(), Conf.NUM_PLAYERS); i++) {
                    shm.protocol.data.tick_response[i] = tickResponse.get(i);
                }
                break;

            default:
                throw new IllegalStateException("Unreachable protocol type: " + shm.protocol.type);
        }

        shm.protocol.type += 1;
        shm.sync = EngineStatus.Busy;
        
        shm.write();
        Pointer p = shm.getPointer();
        p.read(0, data, 0, data.length);
        buffer.position(0);
        buffer.put(data);
    }

    @Override
    public void close() throws IOException {
        if (this.file != null) {
            this.file.close();
        }
    }
}
