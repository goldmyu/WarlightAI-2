package mcts;


import bot.BotState;
import map.Region;
import map.SuperRegion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Monte Carlo Tree Search algorithm
 */
public class MonteCarloTreeSearch {

    private StateTree stateTree;
    private BotState currentState;
    private int totalSimulationsNum;
    private final static int maxTreeLayerWidth = 20;
    private ArrayList<MoveOrder> myMoves;
    private ArrayList<MoveOrder> enemyMoves;


    public void runPlaceArmies(BotState state) {
        currentState = state;
        buildTree();
        StateNode selectedNode;
        StateNode expandedNode;

        while (true) {//TODO - Change to while we have time
            selectedNode = select();
            for (int i = 0; i < maxTreeLayerWidth; i++) {
                expandedNode = expand(selectedNode);
                executeAllMoveOrders(expandedNode);

                simulate(expandedNode);//TODO
                backPropagate(expandedNode);//TODO
            }
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
        StateNode newRandStateNode = randomizeState(selectedNode);
        selectedNode.getSonsNodes().add(newRandStateNode);
        return newRandStateNode;
    }

    private StateNode randomizeState(StateNode node) {
        BotState botState = new BotState(currentState);

        //TODO - need to address the fog - only do this if i am one level below root

        placeArmiesRandomly(botState);

        findPathsToNearestEnemies(botState.getEnemyRegions(), botState.getOpponentPlayerName());//this scans for paths from my player to enemy
        findPathsToNearestEnemies(botState.getMyRegions(), botState.getMyPlayerName());//this scans for paths from enemy to my player

        createAllMovesOrderRandomly(botState);


        //Combine
        //simulate the combined on botState
        //create node with botState, return it
        return new StateNode(node, botState);
    }

    private void createAllMovesOrderRandomly(BotState botState) {
        myMoves = new ArrayList<>();
        enemyMoves = new ArrayList<>();
        List<Region> enemyRegions = botState.getEnemyRegions();
        List<Region> myRegions = botState.getMyRegions();

        for (Region region : myRegions) {
            myMoves.addAll(randomizeRegionMoveOrderList(botState, region, botState.getMyPlayerName()));
        }

        for (Region region : enemyRegions) {
            enemyMoves.addAll(randomizeRegionMoveOrderList(botState, region, botState.getOpponentPlayerName()));
        }
    }

    private ArrayList<MoveOrder> randomizeRegionMoveOrderList(BotState botState, Region region, String thisPlayerName) {
        ArrayList<MoveOrder> moveOrdersList = new ArrayList<>();
        if (region.getArmies() == 1) {// we cant make any move
            return moveOrdersList;
        }

        LinkedList<Region> neighbors = region.getNeighbors();
        boolean isAllNeighborsMine = neighbors.stream().allMatch(neighbor -> neighbor.getPlayerName().equals(region.getPlayerName()));
        if (isAllNeighborsMine) {
            return buildMoveAllNeighborsMine(botState, region, moveOrdersList);
        }

        buildMoveOrderForRegion(region, thisPlayerName, moveOrdersList, neighbors);
        return moveOrdersList;
    }

    private ArrayList<MoveOrder> buildMoveAllNeighborsMine(BotState botState, Region region, ArrayList<MoveOrder> moveOrdersList) {
        MoveOrder moveOrder = new MoveOrder(region.getPlayerName());
        moveOrder.setFromRegion(region);
        if (region.getPlayerName().equals(botState.getMyPlayerName())) {
            moveOrder.setToRegion(region.getRegionCloseToEnemy());
        } else {
            moveOrder.setToRegion(region.getRegionCloseToMe());
        }
        moveOrder.setAmountOfArmiesToMove(region.getArmies() - 1);
        moveOrdersList.add(moveOrder);
        return moveOrdersList;
    }

    /**
     * check all my neighbors to see if i can conquer any of them?
     * flip a coin if i want to conquer or not? if so, pick a random region from the one i can conqour
     * keep this until i have no more armies available to make moves
     */
    private void buildMoveOrderForRegion(Region region, String thisPlayerName, ArrayList<MoveOrder> moveOrdersList, LinkedList<Region> neighbors) {
        int regionArmiesAvailable = region.getArmies();

        do {
            int finalRegionArmiesAvailable = regionArmiesAvailable;
            List<Region> neighborsForConqour = neighbors.stream().filter(neighbor ->
                    ((neighbor.getArmies() * 1.66) < finalRegionArmiesAvailable - 1) && !neighbor.getPlayerName().equals(thisPlayerName)).collect(Collectors.toList());//calc if i can conquer neighbor

            MoveOrder moveOrder = new MoveOrder(region.getPlayerName());
            moveOrder.setFromRegion(region);

            if (!neighborsForConqour.isEmpty() && getRandomNumberInRange(2) == 1) {
                //pick random region to conquer
                int randomNeighborToConqourIndex = getRandomNumberInRange(neighborsForConqour.size());
                Region randomNeighborToConqour = neighborsForConqour.get(randomNeighborToConqourIndex);
                moveOrder.setToRegion(randomNeighborToConqour);
                int armiesNeededToConqour = (int) Math.ceil(randomNeighborToConqour.getArmies() * 1.66);//calc amount of armies we have to use to conqour the region
                int numOfArmiesToAttackWith = armiesNeededToConqour + getRandomNumberInRange(regionArmiesAvailable - 1 - armiesNeededToConqour);//add a random amount of armies to that
                moveOrder.setAmountOfArmiesToMove(numOfArmiesToAttackWith);
            } else {
                int randomNeighborToMoveIndex = getRandomNumberInRange(neighbors.size());
                Region randomNeighborToMove = neighbors.get(randomNeighborToMoveIndex);
                moveOrder.setToRegion(randomNeighborToMove);
                moveOrder.setAmountOfArmiesToMove(getRandomNumberInRange(regionArmiesAvailable - 1));
            }
            neighbors.remove(moveOrder.getToRegion());
            regionArmiesAvailable -= moveOrder.getAmountOfArmiesToMove();
            moveOrdersList.add(moveOrder);
        } while (!neighbors.isEmpty() && regionArmiesAvailable > 1);
    }

    private void findPathsToNearestEnemies(List<Region> regions, String otherPlayerName) {
        //TODO - change this algo to find path to nearest enemy or neutral region - randomize between equal distance regions

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

    private void simulate(StateNode expandedNode) {
        totalSimulationsNum++;
        //TODO - random simulate from a leaf up to a point of win\loss or their approx


    }

    private void executeAllMoveOrders(StateNode expandedNode) {
        //TODO - take new botState and execute the moveOrder we created
        //first choose randomly who will be the first to move
        if (getRandomNumberInRange(2) == 1) {//myPlayer is first

            int myMovesIterator = 0;
            int enemyMovesIterator = 0;
            int totalNumOfMovesLeft = myMoves.size() + enemyMoves.size();

            while (totalNumOfMovesLeft > 0) {
                if (myMovesIterator < myMoves.size()) {
                    executeMoveOrder(myMoves.get(myMovesIterator));
                    myMovesIterator++;
                }
                if (enemyMovesIterator < enemyMoves.size()) {
                    executeMoveOrder(enemyMoves.get(enemyMovesIterator));
                    enemyMovesIterator++;
                }
            }
        }
    }

    private void executeMoveOrder(MoveOrder moveOrderToExec) {
        Region fromRegion = moveOrderToExec.getFromRegion();
        Region toRegion = moveOrderToExec.getToRegion();
        int numOfMovingArmies = moveOrderToExec.getAmountOfArmiesToMove();

        if (fromRegion.getPlayerName().equals(toRegion.getPlayerName())) {//This is a move order
            conductMoveBetweenRegions(fromRegion, toRegion, numOfMovingArmies);
        } else { //This is an attack
            conductWarBetweenRegions(fromRegion, toRegion, numOfMovingArmies);
        }

    }

    private void conductMoveBetweenRegions(Region fromRegion, Region toRegion, int numOfMovingArmies) {
        fromRegion.setArmies(fromRegion.getArmies() - numOfMovingArmies);
        toRegion.setArmies(toRegion.getArmies() + numOfMovingArmies);
    }

    private void conductWarBetweenRegions(Region attackingRegion, Region defendingRegion, int numOfAttackingArmies) {
        long remainingDefendingArmies = Math.round(defendingRegion.getArmies() - (0.6 * numOfAttackingArmies));
        long remainingAttackingArmies = Math.round(numOfAttackingArmies - (0.7 * defendingRegion.getArmies()));

        attackingRegion.setArmies(attackingRegion.getArmies() - numOfAttackingArmies);

        if (remainingDefendingArmies <= 0) {//attacker won
            defendingRegion.setPlayerName(attackingRegion.getPlayerName());
            if (remainingAttackingArmies < 1) {
                defendingRegion.setArmies(1);
            } else {
                defendingRegion.setArmies((int) remainingAttackingArmies);
            }
        } else {
            defendingRegion.setArmies((int) remainingDefendingArmies);
        }
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


