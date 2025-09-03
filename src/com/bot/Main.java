package com.bot;

import com.bot.core.EngineChannel;
import com.bot.strategy.Strategy;
import com.bot.strategy.StrategyMain;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: java -jar your-bot.jar [shmem path]");
            return;
        }

        String shmemPath = args[0];
        
        try (EngineChannel chan = EngineChannel.fromPath(shmemPath)) {
            Strategy strat = StrategyMain.getStrategy();
            System.out.println("Bot connected to shared memory: " + shmemPath);
            
            chan.handleHandshake();

            while (true) {
                chan.handleMsg(strat);
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
