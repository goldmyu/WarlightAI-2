package mcts;

import bot.BotState;

import java.util.ArrayList;

public class StateNode {

    private BotState botState;
    private ArrayList<StateNode> sonsNodes;
    private StateNode fatherNode;
    private int numOfWins;
    private int numOfRuns;
    private boolean simulateWon = false;//only one per node!
    private ArrayList<MoveOrder> myMoves;
    private ArrayList<MoveOrder> enemyMoves;
    private ArrayList<PlaceArmiesOrder> myPlacedArmiesOrderList;
    private ArrayList<PlaceArmiesOrder> enemyPlacedArmiesOrderList;
    


    public StateNode(StateNode fatherNode, BotState botState){
        this.botState = botState;
        this.fatherNode = fatherNode;
        myMoves = new ArrayList<>();
        enemyMoves = new ArrayList<>();
        myPlacedArmiesOrderList = new ArrayList<>();
        enemyPlacedArmiesOrderList = new ArrayList<>();
    }


    public ArrayList<MoveOrder> getMyMoves() {
        return myMoves;
    }

    public void setMyMoves(ArrayList<MoveOrder> myMoves) {
        this.myMoves = myMoves;
    }

    public ArrayList<MoveOrder> getEnemyMoves() {
        return enemyMoves;
    }

    public void setEnemyMoves(ArrayList<MoveOrder> enemyMoves) {
        this.enemyMoves = enemyMoves;
    }

    public BotState getBotState() {
        return botState;
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



    public void updateScore(boolean simulateWon){
        numOfRuns++;
        if(simulateWon) numOfWins++;
    }
    
    public void addMyPlacedArmiesOrder(PlaceArmiesOrder myPlaceArmiesOrder){
        myPlacedArmiesOrderList.add(myPlaceArmiesOrder);
    }
    
    public void addEnemyPlacedArmiesOrder(PlaceArmiesOrder enemyPlaceArmiesOrder){
        enemyPlacedArmiesOrderList.add(enemyPlaceArmiesOrder);
    }

}
