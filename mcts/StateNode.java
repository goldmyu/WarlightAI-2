package mcts;

import bot.BotState;

import java.util.ArrayList;

public class StateNode {

    private BotState state;
    private ArrayList<StateNode> sonsNodes;
    private StateNode fatherNode;
    private int numOfWins;
    private int numOfRuns;
    private boolean simulateWon = false;//only one per node!

    public BotState getState() {
        return state;
    }

    public ArrayList<StateNode> getSonsNodes() {
        return sonsNodes;
    }

    public StateNode getFatherNode() {
        return fatherNode;
    }

    public boolean isSimulateWon() {
        return simulateWon;
    }

    public int getNumOfWins() {
        return numOfWins;
    }

    public int getNumOfRuns() {
        return numOfRuns;
    }

    public double getSuccessRatio() {
        return numOfWins/numOfRuns;
    }

    public StateNode(StateNode fatherNode, BotState state){
        this.state = state;
        this.fatherNode = fatherNode;
    }

    public void updateScore(boolean simulateWon){
        numOfRuns++;
        if(simulateWon) numOfWins++;
    }

}
