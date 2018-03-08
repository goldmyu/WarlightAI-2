package mcts;

import map.Region;

public class PlaceArmiesOrder {
    private Region regionToPlaceOn;
    private int amountOfArmiesPlaced;
    private String playerName;


    public Region getRegionToPlaceOn() {
        return regionToPlaceOn;
    }

    public void setRegionToPlaceOn(Region regionToPlaceOn) {
        this.regionToPlaceOn = regionToPlaceOn;
    }

    public int getAmountOfArmiesPlaced() {
        return amountOfArmiesPlaced;
    }

    public void setAmountOfArmiesPlaced(int amountOfArmiesPlaced) {
        this.amountOfArmiesPlaced = amountOfArmiesPlaced;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
