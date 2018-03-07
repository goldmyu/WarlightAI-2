package mcts;

public class MoveOrder {
    private int fromRegionId;
    private int toRegionId;
    private int amountOfArmiesToMove;
    private String playerName;

    public MoveOrder(String playerName) {
        this.playerName = playerName;
    }



    public int getFromRegionId() {
        return fromRegionId;
    }

    public int getToRegionId() {
        return toRegionId;
    }

    public int getAmountOfArmiesToMove() {
        return amountOfArmiesToMove;
    }
}
