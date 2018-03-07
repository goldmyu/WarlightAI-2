package mcts;


import bot.BotState;

public class MonteCarloTreeSearch {

    private StateTree stateTree;

    public MonteCarloTreeSearch() {
        stateTree = new StateTree();
    }


    //Monte Carlo Tree Search algorithm

    public void runMcts(BotState state) {
        //TODO call : select->expand->simulate->backPropegate



    }


    private void select() {
        //TODO - go overa state tree and select according to the selection formula
    }

    private void expand() {
        //TODO - expand each layer up to the max layer number
    }

    private void simulate() {
        //TODO - randome simulate from a leaf up to a point of win\loss or their approx
    }

    private void backPropegate() {
        //TODO - update the tree with the winning\loosing status!!!
    }
}

