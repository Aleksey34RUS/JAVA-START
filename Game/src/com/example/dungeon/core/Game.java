package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Charset consoleCharset;

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        this.consoleCharset = Charset.forName("IBM866");
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> {
            System.out.println("=== КОМАНДЫ ===");
            System.out.println("help - показать это сообщение");
            System.out.println("look - осмотреть комнату");
            System.out.println("move <направление> - переместиться");
            System.out.println("take <предмет> - взять предмет");
            System.out.println("inventory - показать инвентарь");
            System.out.println("use <предмет> - использовать предмет");
            System.out.println("fight - сразиться с монстром");
            System.out.println("save - сохранить игру");
            System.out.println("load - загрузить игру");
            System.out.println("scores - таблица лидеров");
            System.out.println("gc-stats - статистика памяти");
            System.out.println("exit - выйти из игры");
        });

        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            rt.gc(); // Рекомендуем сборку мусора перед выводом статистики
            long free = rt.freeMemory();
            long total = rt.totalMemory();
            long used = total - free;
            System.out.println("=== СТАТИСТИКА ПАМЯТИ ===");
            System.out.println("Используется: " + (used / 1024 / 1024) + " MB");
            System.out.println("Свободно: " + (free / 1024 / 1024) + " MB");
            System.out.println("Всего: " + (total / 1024 / 1024) + " MB");
        });

        commands.put("look", (ctx, a) -> {
            System.out.println(ctx.getCurrent().describe());
        });

        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите направление: north, south, east, west");
            }

            String direction = a.get(0).toLowerCase();
            Room currentRoom = ctx.getCurrent();
            Room nextRoom = currentRoom.getNeighbors().get(direction);

            if (nextRoom == null) {
                throw new InvalidCommandException("Нет выхода в направлении: " + direction);
            }

            // Проверяем, жив ли игрок
            if (ctx.getPlayer().getHp() <= 0) {
                throw new InvalidCommandException("Вы не можете перемещаться с 0 HP!");
            }

            ctx.setCurrent(nextRoom);
            System.out.println("Вы перешли в: " + nextRoom.getName());
            commands.get("look").execute(ctx, Collections.emptyList());
        });

        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите название предмета");
            }

            String itemName = String.join(" ", a);
            Room currentRoom = ctx.getCurrent();
            Player player = ctx.getPlayer();

            Optional<Item> itemOpt = currentRoom.getItems().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            if (itemOpt.isEmpty()) {
                // Показываем доступные предметы
                String availableItems = currentRoom.getItems().stream()
                        .map(Item::getName)
                        .collect(Collectors.joining(", "));
                if (availableItems.isEmpty()) {
                    throw new InvalidCommandException("В комнате нет предметов");
                } else {
                    throw new InvalidCommandException("Предмет не найден. Доступные: " + availableItems);
                }
            }

            Item item = itemOpt.get();
            currentRoom.getItems().remove(item);
            player.getInventory().add(item);

            System.out.println("Взято: " + item.getName());
        });

        commands.put("inventory", (ctx, a) -> {
            Player player = ctx.getPlayer();
            List<Item> inventory = player.getInventory();

            if (inventory.isEmpty()) {
                System.out.println("Инвентарь пуст");
                return;
            }

            System.out.println("=== ИНВЕНТАРЬ ===");

            inventory.stream()
                    .collect(Collectors.groupingBy(
                            item -> item.getClass().getSimpleName(),
                            TreeMap::new,
                            Collectors.toList()
                    ))
                    .forEach((type, items) -> {
                        String itemNames = items.stream()
                                .map(Item::getName)
                                .collect(Collectors.joining(", "));
                        System.out.println("• " + type + " (" + items.size() + "): " + itemNames);
                    });

            System.out.println("Всего предметов: " + inventory.size());
        });

        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) {
                throw new InvalidCommandException("Укажите название предмета");
            }

            String itemName = String.join(" ", a);
            Player player = ctx.getPlayer();

            Optional<Item> itemOpt = player.getInventory().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(itemName))
                    .findFirst();

            if (itemOpt.isEmpty()) {
                throw new InvalidCommandException("Предмет '" + itemName + "' не найден в инвентаре");
            }

            Item item = itemOpt.get();
            System.out.println("Используется: " + item.getName());
            item.apply(ctx);
        });

        commands.put("fight", (ctx, a) -> {
            Room currentRoom = ctx.getCurrent();
            Monster monster = currentRoom.getMonster();
            Player player = ctx.getPlayer();

            if (monster == null) {
                throw new InvalidCommandException("В этой комнате нет монстров");
            }

            if (player.getHp() <= 0) {
                throw new InvalidCommandException("Вы не можете сражаться с 0 HP!");
            }

            System.out.println("=== БОЙ С " + monster.getName().toUpperCase() + " ===");

            int round = 1;
            while (player.getHp() > 0 && monster.getHp() > 0) {
                System.out.println("\n--- Раунд " + round + " ---");

                // Ход игрока
                int playerDamage = player.getAttack();
                System.out.println("Вы атакуете " + monster.getName() + " и наносите " + playerDamage + " урона");
                monster.setHp(monster.getHp() - playerDamage);

                if (monster.getHp() <= 0) {
                    System.out.println("🎉 " + monster.getName() + " побежден!");

                    // Выпадение лута
                    if (Math.random() > 0.3) { // 70% шанс выпадения лута
                        Item loot;
                        if (Math.random() > 0.5) {
                            loot = new Potion("Зелье здоровья", 5);
                        } else {
                            loot = new Weapon("Заточка", 1);
                        }
                        currentRoom.getItems().add(loot);
                        System.out.println("💎 Монстр выпал: " + loot.getName());
                    }

                    currentRoom.setMonster(null);
                    ctx.addScore(15); // Больше очков за победу
                    System.out.println("➕ Получено 15 очков");
                    return;
                }

                // Ход монстра
                int monsterDamage = Math.max(1, monster.getLevel()); // Минимум 1 урон
                System.out.println(monster.getName() + " атакует вас и наносит " + monsterDamage + " урона");
                player.setHp(player.getHp() - monsterDamage);

                System.out.println("❤️ Ваше HP: " + player.getHp());
                System.out.println("💀 HP " + monster.getName() + ": " + monster.getHp());

                round++;

                // Пауза между раундами
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (player.getHp() <= 0) {
                System.out.println("💀 ВЫ ПОГИБЛИ! Игра окончена.");
                SaveLoad.writeScore(player.getName(), ctx.getScore());
                System.out.println("🏆 Ваш финальный счет: " + ctx.getScore());
                System.exit(0);
            }
        });

        commands.put("save", (ctx, a) -> {
            try {
                SaveLoad.save(ctx);
            } catch (Exception e) {
                throw new InvalidCommandException("Ошибка сохранения: " + e.getMessage());
            }
        });

        commands.put("load", (ctx, a) -> {
            try {
                SaveLoad.load(ctx);
            } catch (Exception e) {
                throw new InvalidCommandException("Ошибка загрузки: " + e.getMessage());
            }
        });

        commands.put("scores", (ctx, a) -> {
            SaveLoad.printScores();
        });

        commands.put("demo-error", (ctx, a) -> {
            System.out.println("Демонстрация ошибки выполнения:");
            if (a.isEmpty()) {
                // Намеренно вызываем ArithmeticException
                int zero = 0;
                int result = 10 / zero;
            }
        });

        commands.put("alloc", (ctx, a) -> {
            System.out.println("Создание временных объектов...");
            List<Object> tempObjects = new ArrayList<>();
            for (int i = 0; i < 50000; i++) {
                tempObjects.add(new String("Object-" + i));
                tempObjects.add(i);
            }
            System.out.println("Создано " + tempObjects.size() + " временных объектов");
            System.out.println("Объекты будут собраны сборщиком мусора");
        });

        commands.put("exit", (ctx, a) -> {
            SaveLoad.writeScore(ctx.getPlayer().getName(), ctx.getScore());
            System.out.println("👋 До свидания! Ваш счет: " + ctx.getScore());
            System.exit(0);
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 25, 6); // Увеличим стартовые характеристики
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном в центре.");
        Room forest = new Room("Лес", "Густой лес с шелестом листьев.");
        Room cave = new Room("Пещера", "Темная и сырая пещера.");

        // Создаем связи между комнатами
        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);

        // Добавляем предметы
        forest.getItems().add(new Potion("Maloe zelie", 8));
        forest.getItems().add(new Weapon("Rjavij mech", 2));

        // Добавляем монстров
        forest.setMonster(new Monster("Волк", 1, 10));
        cave.getItems().add(new Key("Ржавый ключ"));
        cave.setMonster(new Monster("Гоблин", 2, 15));

        state.setCurrent(square);
        state.addScore(0); // Инициализируем счет
    }

    public void run() {
        System.out.println("🎮 DungeonMini - Консольная RPG игра");
        System.out.println("=====================================");
        System.out.println("Кодировка: " + consoleCharset.name());
        System.out.println("Для начала введите 'look' чтобы осмотреться");
        System.out.println("Или 'help' для списка команд");
        System.out.println("=====================================");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, consoleCharset))) {
            while (true) {
                System.out.print("\n> ");
                String line = in.readLine();
                if (line == null) break;

                line = line.trim();
                if (line.isEmpty()) continue;

                List<String> parts = Arrays.asList(line.split("\\s+"));
                String cmd = parts.get(0).toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
                Command command = commands.get(cmd);

                try {
                    if (command == null) {
                        throw new InvalidCommandException("Неизвестная команда: '" + cmd + "'. Введите 'help' для справки");
                    }

                    command.execute(state, args);
                    state.addScore(1); // Начисляем 1 очко за каждую команду

                } catch (InvalidCommandException e) {
                    System.out.println("❌ Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("💥 Непредвиденная ошибка: " + e.getClass().getSimpleName());
                    System.out.println("Сообщение: " + e.getMessage());
                    // Не выводим stack trace для удобства пользователя
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}