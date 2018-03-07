package mcts;


import bot.Bot;
import bot.BotState;

import java.util.ArrayList;
import java.util.Comparator;

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
        for(int i = 0; i < maxTreeLayerWidth ; i++){
            selectedNode.getSonsNodes().add(randomizeState(selectedNode));
        }

        return selectedNode;
    }

    private StateNode randomizeState(StateNode node) {
        BotState botState = new BotState();


        //TODO - need to address the fog




        return new StateNode(node, botState);
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


}


