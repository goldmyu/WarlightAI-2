package mcts;

import bot.BotState;

public class StateTree {

    public StateNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(StateNode rootNode) {
        this.rootNode = rootNode;
    }

    public StateNode getMaximumScoreNode() {
        return maximumScoreNode;
    }

    public void setMaximumScoreNode(StateNode maximumScoreNode) {
        this.maximumScoreNode = maximumScoreNode;
    }

    StateNode rootNode;
    StateNode maximumScoreNode;//This will be update always


    public StateTree(BotState currentGameState){
        rootNode = new StateNode(null, currentGameState);
    }
}
