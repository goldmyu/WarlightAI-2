package mcts;

import map.Region;

public class MoveOrder {
    private Region fromRegion;
    private Region toRegion;
    private int amountOfArmiesToMove;
    private String playerName;

    public MoveOrder(String playerName) {
        this.playerName = playerName;
    }


    public Region getFromRegion() {
        return fromRegion;
    }

    public void setFromRegion(Region fromRegion) {
        this.fromRegion = fromRegion;
    }

    public Region getToRegion() {
        return toRegion;
    }

    public void setToRegion(Region toRegion) {
        this.toRegion = toRegion;
    }

    public int getAmountOfArmiesToMove() {
        return amountOfArmiesToMove;
    }

    public void setAmountOfArmiesToMove(int amountOfArmiesToMove) {
        this.amountOfArmiesToMove = amountOfArmiesToMove;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
