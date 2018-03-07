package bot.bot_types;

import bot.Bot;
import bot.BotParser;
import bot.BotState;
import map.Region;
import mcts.MonteCarloTreeSearch;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;

public class SmartBotStarter implements Bot {


    private MonteCarloTreeSearch mcts;


    public SmartBotStarter() {
        mcts = new MonteCarloTreeSearch();
    }







    @Override
    public Region getStartingRegion(BotState state, Long timeOut) {
        double rand = Math.random();
        int r = (int) (rand*state.getPickableStartingRegions().size());
        int regionId = state.getPickableStartingRegions().get(r).getId();
        Region startingRegion = state.getFullMap().getRegion(regionId);

        return startingRegion;
    }

    @Override
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {
        mcts.runPlaceArmies(state);
        return null;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        return null;
    }


    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new SmartBotStarter());
        parser.run();
    }
}
