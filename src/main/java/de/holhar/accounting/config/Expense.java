package de.holhar.accounting.config;

import java.util.List;

public class Expense {

    private List<String> accommodation;
    private List<String> food;
    private List<String> health;
    private List<String> transportation;
    private List<String> purchases;

    public List<String> getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(List<String> accommodation) {
        this.accommodation = accommodation;
    }

    public List<String> getFood() {
        return food;
    }

    public void setFood(List<String> food) {
        this.food = food;
    }

    public List<String> getHealth() {
        return health;
    }

    public void setHealth(List<String> health) {
        this.health = health;
    }

    public List<String> getTransportation() {
        return transportation;
    }

    public void setTransportation(List<String> transportation) {
        this.transportation = transportation;
    }

    public List<String> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<String> purchases) {
        this.purchases = purchases;
    }
}
