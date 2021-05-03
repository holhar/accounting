package de.holhar.accounting.config;

public class Expense {

    private String accommodation;
    private String food;
    private String health;
    private String transportation;
    private String purchases;

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getPurchases() {
        return purchases;
    }

    public void setPurchases(String purchases) {
        this.purchases = purchases;
    }
}
