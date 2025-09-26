package com.example.dungeon.model;

import java.util.*;

public class Player extends Entity {
    private int attack;
    private final List<Item> inventory = new ArrayList<>();

    public Player(String name, int hp, int attack) {
        super(name, hp);
        this.attack = attack;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    // Вспомогательный метод для поиска предмета по имени
    public Optional<Item> findItem(String itemName) {
        return inventory.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .findFirst();
    }

    // Вспомогательный метод для удаления предмета
    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }
}