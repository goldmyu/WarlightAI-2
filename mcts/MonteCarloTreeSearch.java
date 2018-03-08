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

        //TODO - fix this issue, we create all sons but return the father
        return selectedNode;
    }

    private StateNode randomizeState(StateNode node) {
        BotState botState = new BotState(currentState);
        placeArmiesRandomly(botState);
        //TODO - need to address the fog


        findPathsToNearestEnemies(botState.getEnemyRegions(), botState.getOpponentPlayerName());//this scans for paths from my player to enemy
        findPathsToNearestEnemies(botState.getMyRegions(), botState.getMyPlayerName());//this scans for paths from enemy to my player


        //Combine
        //simulate the combined on botState
        //create node with botState, return it
        return new StateNode(node, botState);
    }

    private void createAllMovesOrderRandomly(BotState botState) {
        ArrayList<MoveOrder> allMoves = new ArrayList<>();
        List<Region> enemyRegions = botState.getEnemyRegions();
        List<Region> myRegions = botState.getMyRegions();

        for (Region region : myRegions) {


        }


    }

    private MoveOrder randomizeMoveOrder(BotState botState, Region region) {
        //TODO - check if we want to add randomness


        MoveOrder moveOrder = new MoveOrder(region.getPlayerName());

        LinkedList<Region> neighbors = region.getNeighbors();

        boolean isAllNeighborsMine = neighbors.stream().allMatch(neighbor -> neighbor.getPlayerName().equals(region.getPlayerName()));
        if (isAllNeighborsMine) {
            moveOrder.setFromRegion(region);
            if (region.getPlayerName().equals(botState.getMyPlayerName())) {
                moveOrder.setToRegion(region.getRegionCloseToEnemy());
            } else {
                moveOrder.setToRegion(region.getRegionCloseToMe());
            }
            moveOrder.setAmountOfArmiesToMove(region.getArmies() - 1);
            return moveOrder;
        }


        //TODO - dont return null
        return null;

    }

    private void findPathsToNearestEnemies(List<Region> regions, String otherPlayerName) {
        List<Region> newRegionList = new ArrayList<>();
        for (Region region : regions) {
            LinkedList<Region> neighbors = region.getNeighbors();
            for (Region neighbor : neighbors) {
                if (!neighbor.ownedByPlayer(otherPlayerName) && getRegionCloseToOtherPlayer(neighbor, otherPlayerName) == null) {
                    setCloseToOtherPlayer(neighbor, otherPlayerName, region);
                    newRegionList.add(neighbor);
                }
            }
        }
        if (newRegionList.isEmpty()) {
            return;
        }

        findPathsToNearestEnemies(newRegionList, otherPlayerName);
    }

    private void setCloseToOtherPlayer(Region neighbor, String otherPlayerName, Region region) {
        if (otherPlayerName.equals(currentState.getOpponentPlayerName())) {
            neighbor.setRegionCloseToEnemy(region);
        } else {
            neighbor.setRegionCloseToMe(region);
        }
    }

    private Region getRegionCloseToOtherPlayer(Region neighbor, String otherPlayerName) {
        if (otherPlayerName.equals(currentState.getOpponentPlayerName())) {
            return neighbor.getRegionCloseToEnemy();
        } else {
            return neighbor.getRegionCloseToMe();
        }
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
            if (isAllNeighborsMine(randomRegion, botState.getOpponentPlayerName())) {
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
            if (isAllNeighborsMine(randomRegion, botState.getOpponentPlayerName())) {
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


