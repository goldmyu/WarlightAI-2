package mcts;


import bot.BotState;
import map.Region;
import map.SuperRegion;

import java.util.*;

//Monte Carlo Tree Search algorithm
public class MonteCarloTreeSearch {

    private StateTree stateTree;
    private BotState currentState;
    private int totalSimulationsNum;
    private final static int maxTreeLayerWidth = 20;


    public void runPlaceArmies(BotState state) {
        currentState = state;
        buildTree();
        StateNode selectedNode;
        StateNode expandedNode;

        while (true) {//TODO - Change to while we have time

            selectedNode = select();

            expandedNode = expand(selectedNode);

            simulate(expandedNode);

            backPropagate(expandedNode);

        }
    }

    private void buildTree() {
        stateTree = new StateTree(currentState);
    }

    private StateNode select() {
        StateNode maximumNode = findMaximumNode(stateTree.getRootNode());
        return maximumNode;
    }

    private StateNode findMaximumNode(StateNode node) {
        ArrayList<StateNode> sonsNodes = node.getSonsNodes();
        if (sonsNodes.isEmpty()) {
            return node;
        } else {
            StateNode maxNode = sonsNodes.stream().max(Comparator.comparing(sonNode ->
                    sonNode.getSuccessRatio() + Math.sqrt((2 * Math.log(totalSimulationsNum)) / sonNode.getNumOfRuns()))).get();
            return findMaximumNode(maxNode);
        }
    }

    private StateNode expand(StateNode selectedNode) {
        //TODO - expand each layer up to the max layer number
        for (int i = 0; i < maxTreeLayerWidth; i++) {
            selectedNode.getSonsNodes().add(randomizeState(selectedNode));
        }

        return selectedNode;
    }

    private StateNode randomizeState(StateNode node) {
        BotState botState = new BotState(currentState);
        placeArmiesRandomly(botState);
        //TODO - need to address the fog
//        List<> myMoves =
//        List <>enemyMoves =
        //Combine
        //simulate the combined on botState
        //create node with botState, return it
        return new StateNode(node, botState);
    }


    private void createAllMovesOrderRandomly(BotState botState) {
        ArrayList<MoveOrder> allMoves = new ArrayList<>();
        List<Region> enemyRegions = botState.getEnemyRegions();
        List<Region> myRegions = botState.getMyRegions();

        for(Region region: myRegions){
            allMoves.add(randomizeMoveOrder(botState, region));
        }




    }

    private MoveOrder randomizeMoveOrder(BotState botState, Region region) {
        MoveOrder moveOrder = new MoveOrder(region.getPlayerName());

        for(Region neighborRegion : region.getNeighbors()){

        }


        return null;

    }

    private void findPathsToNearstEnemies (BotState botState, Region region)
    {
            //mark itself
            //if all nighbors are marked do nothing
            //
    }



    private void placeArmiesRandomly(BotState botState) {
        placeMyArmies(botState);
        placeEnemyArmiesRandomly(botState);
    }

    private void placeEnemyArmiesRandomly(BotState botState) {
        int enemyIncome = calcEnemyArmiesIncome(botState);
        List<Region> enemyRegions = botState.getEnemyRegions();

        int armiesPlaced = 0;
        while (armiesPlaced < enemyIncome) {
            int index = getRandomNumberInRange(enemyRegions.size());
            Region randomRegion = enemyRegions.get(index);
            if(isAllNeighborsMine(randomRegion, botState.getOpponentPlayerName())){
                continue;
            }

            int armiesToPlace = getRandomNumberInRange(enemyIncome - armiesPlaced);
            botState.getVisibleMap().getRegion(randomRegion.getId()).setArmies(randomRegion.getArmies() + armiesToPlace);
            armiesPlaced = armiesPlaced + armiesToPlace;
        }
    }

    private boolean isAllNeighborsMine(Region randomRegion, String playerName) {
        return randomRegion.getNeighbors().stream().allMatch(neighbor -> neighbor.getPlayerName().equals(playerName));
    }

    private int calcEnemyArmiesIncome(BotState botState) {
        int enemyIncome = 5;
        LinkedList<SuperRegion> superRegions = botState.getVisibleMap().getSuperRegions();

        for (SuperRegion superRegion : superRegions) {
            boolean isEnemySuperRegion = superRegion.getSubRegions().stream().allMatch(region ->
                    region.getPlayerName().equals(botState.getOpponentPlayerName()));
            if (isEnemySuperRegion) {
                enemyIncome += superRegion.getArmiesReward();
            }
        }
        return enemyIncome;
    }

    private void placeMyArmies(BotState botState) {
        List<Region> myRegions = botState.getMyRegions();
        int armiesPlaced = 0;
        while (armiesPlaced < botState.getStartingArmies()) {
            int randomRegionIndex = getRandomNumberInRange(myRegions.size());
            Region randomRegion = myRegions.get(randomRegionIndex);
            if(isAllNeighborsMine(randomRegion, botState.getOpponentPlayerName())){
                continue;
            }
            int armiesToPlace = getRandomNumberInRange(botState.getStartingArmies() - armiesPlaced);
            botState.getVisibleMap().getRegion(randomRegion.getId()).setArmies(randomRegion.getArmies() + armiesToPlace);
            armiesPlaced = armiesPlaced + armiesToPlace;
        }
    }

    private void simulate(StateNode expandedNode) {
        totalSimulationsNum++;
        //TODO - randome simulate from a leaf up to a point of win\loss or their approx

    }

    private void backPropagate(StateNode expandedNode) {
        boolean simulateWon = expandedNode.isSimulateWon();
        StateNode iterateNode = expandedNode;

        while (iterateNode.getFatherNode() != null) {
            iterateNode.updateScore(simulateWon);
            iterateNode = iterateNode.getFatherNode();
        }
    }

    private int getRandomNumberInRange(int randomRange) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(randomRange);
    }
}


