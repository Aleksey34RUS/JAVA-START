package com.example.dungeon.model;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> neighbors = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private Monster monster;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Map<String, Room> getNeighbors() {
        return neighbors;
    }

    public List<Item> getItems() {
        return items;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster m) {
        this.monster = m;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + ": " + description);

        if (!items.isEmpty()) {
            sb.append("\nПредметы: ");
            sb.append(items.stream()
                    .map(Item::getName)
                    .collect(Collectors.joining(", ")));
        }

        if (monster != null) {
            sb.append("\nВ комнате монстр: ")
                    .append(monster.getName())
                    .append(" (ур. ").append(monster.getLevel())
                    .append(", HP: ").append(monster.getHp()).append(")");
        }

        if (!neighbors.isEmpty()) {
            sb.append("\nВыходы: ")
                    .append(String.join(", ", neighbors.keySet()));
        }

        return sb.toString();
    }
}