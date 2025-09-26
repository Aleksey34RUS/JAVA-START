package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SaveLoad {
    private static final Path SAVE = Paths.get("save.txt");
    private static final Path SCORES = Paths.get("scores.csv");

    public static void save(GameState s) {
        try (BufferedWriter w = Files.newBufferedWriter(SAVE, StandardCharsets.UTF_8)) {
            Player p = s.getPlayer();
            // Защита от null имени
            String name = (p.getName() != null && !p.getName().isEmpty()) ? p.getName() : "Герой";
            w.write("player;" + name + ";" + p.getHp() + ";" + p.getAttack());
            w.newLine();

            // Сохраняем инвентарь
            String inv = p.getInventory().stream()
                    .map(i -> i.getClass().getSimpleName() + ":" + i.getName())
                    .collect(Collectors.joining(","));
            w.write("inventory;" + (inv.isEmpty() ? "" : inv));
            w.newLine();

            // Сохраняем текущую комнату
            String roomName = (s.getCurrent() != null && s.getCurrent().getName() != null)
                    ? s.getCurrent().getName()
                    : "Площадь";
            w.write("room;" + roomName);
            w.newLine();

            // Сохраняем счет
            w.write("score;" + s.getScore());
            w.newLine();

            System.out.println("Сохранено в " + SAVE.toAbsolutePath());
            writeScore(p.getName(), s.getScore());

        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить игру", e);
        }
    }

    public static void load(GameState s) {
        if (!Files.exists(SAVE)) {
            System.out.println("Сохранение не найдено.");
            return;
        }

        try (BufferedReader r = Files.newBufferedReader(SAVE, StandardCharsets.UTF_8)) {
            Map<String, String> map = new HashMap<>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }

            Player p = s.getPlayer();
            boolean useDefaults = false;

            // Загрузка данных игрока
            String playerData = map.get("player");
            if (playerData == null || playerData.trim().isEmpty()) {
                System.out.println("Данные игрока отсутствуют в сохранении.");
                useDefaults = true;
            } else {
                String[] pp = playerData.split(";", -1);
                if (pp.length < 3) {
                    System.out.println("Ошибка формата данных игрока: ожидается ИМЯ;HP;АТАКА.");
                    useDefaults = true;
                } else {
                    String name = pp[0].trim();
                    try {
                        int hp = Integer.parseInt(pp[1].trim());
                        int attack = Integer.parseInt(pp[2].trim());

                        if (hp <= 0 || attack <= 0 || name.isEmpty()) {
                            throw new NumberFormatException("Некорректные значения");
                        }

                        p.setName(name);
                        p.setHp(hp);
                        p.setAttack(attack);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: некорректные числовые данные игрока.");
                        useDefaults = true;
                    }
                }
            }

            if (useDefaults) {
                p.setName("Герой");
                p.setHp(20);
                p.setAttack(5);
                System.out.println("Используются значения по умолчанию.");
            }

            // Очищаем инвентарь перед загрузкой
            p.getInventory().clear();

            // Загружаем инвентарь
            String invData = map.get("inventory");
            if (invData != null && !invData.trim().isEmpty()) {
                String[] inventoryItems = invData.split(",");
                for (String itemStr : inventoryItems) {
                    String[] itemParts = itemStr.split(":", 2);
                    if (itemParts.length == 2) {
                        String itemType = itemParts[0];
                        String itemName = itemParts[1];

                        try {
                            switch (itemType) {
                                case "Potion":
                                    p.getInventory().add(new Potion(itemName, 5));
                                    break;
                                case "Key":
                                    p.getInventory().add(new Key(itemName));
                                    break;
                                case "Weapon":
                                    p.getInventory().add(new Weapon(itemName, 3));
                                    break;
                                default:
                                    System.out.println("Неизвестный тип предмета: " + itemType);
                            }
                        } catch (Exception e) {
                            System.out.println("Не удалось создать предмет: " + itemStr);
                        }
                    }
                }
            }

            // Загружаем счет
            String scoreData = map.get("score");
            int loadedScore = 0;
            if (scoreData != null && !scoreData.trim().isEmpty()) {
                try {
                    loadedScore = Integer.parseInt(scoreData);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка формата счета. Счет сброшен.");
                    loadedScore = 0;
                }
            }
            // Устанавливаем счет напрямую, а не через addScore
            s.setScore(loadedScore);

            System.out.println("Игра загружена. Счет: " + s.getScore());
            System.out.println("HP: " + p.getHp() + ", Атака: " + p.getAttack());
            System.out.println("Инвентарь: " + p.getInventory().size() + " предметов");

        } catch (IOException e) {
            System.out.println("Не удалось загрузить игру: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
        }
    }

    public static void printScores() {
        if (!Files.exists(SCORES)) {
            System.out.println("Пока нет результатов.");
            return;
        }

        try (BufferedReader r = Files.newBufferedReader(SCORES, StandardCharsets.UTF_8)) {
            System.out.println("Таблица лидеров (топ-10):");

            List<Score> scores = r.lines()
                    .skip(1) // Пропускаем заголовок
                    .map(l -> l.split(","))
                    .filter(a -> a.length >= 3)
                    .map(a -> {
                        try {
                            return new Score(a[1], Integer.parseInt(a[2]));
                        } catch (NumberFormatException e) {
                            return new Score(a[1], 0);
                        }
                    })
                    .sorted(Comparator.comparingInt(Score::score).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            if (scores.isEmpty()) {
                System.out.println("Нет данных о результатах.");
            } else {
                for (int i = 0; i < scores.size(); i++) {
                    Score score = scores.get(i);
                    System.out.println((i + 1) + ". " + score.player() + " - " + score.score() + " очков");
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка чтения результатов: " + e.getMessage());
        }
    }

    public static void writeScore(String player, int score) {
        try {
            boolean header = !Files.exists(SCORES);
            try (BufferedWriter w = Files.newBufferedWriter(SCORES,
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (header) {
                    w.write("timestamp,player,score");
                    w.newLine();
                }
                w.write(LocalDateTime.now() + "," + (player != null ? player : "Герой") + "," + score);
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать очки: " + e.getMessage());
        }
    }

    private record Score(String player, int score) {}
}