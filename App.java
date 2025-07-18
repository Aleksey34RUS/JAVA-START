public class App {
    public static void main(String[] args) {
        // Создание объекта телевизора
        Television myTV = new Television("Sony");

        // Проверка начального состояния
        System.out.println("=== Начальное состояние ===");
        System.out.println("Марка: " + myTV.getBrand());
        System.out.println("Включен: " + myTV.isOn());
        System.out.println("Текущий канал: " + myTV.getCurrentChannel());
        System.out.println("Громкость: " + myTV.getVolume() + "%");
        System.out.println("===========================\n");

        // Тестирование функционала
        myTV.turnOn();
        myTV.increaseVolume();
        myTV.nextChannel();
        myTV.setChannel(25);
        myTV.decreaseVolume();
        myTV.previousChannel();
        myTV.turnOff();

        // Попытка управления выключенным телевизором
        myTV.setChannel(10);
    }
}

class Television {
    // 1. Поля класса (состояние объекта)
    private final String brand;       // Марка телевизора (неизменяемая)
    private boolean isOn;             // Состояние питания (true - включен)
    private int currentChannel;       // Текущий канал
    private int volume;               // Уровень громкости

    // 2. Константы для ограничений (лучшая практика - избегать "магических чисел")
    private static final int MIN_CHANNEL = 1;
    private static final int MAX_CHANNEL = 50;
    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 100;
    private static final int VOLUME_STEP = 5; // Шаг изменения громкости
    private static final int DEFAULT_VOLUME = 10; // Громкость по умолчанию

    // 3. Конструктор - инициализирует объект при создании
    public Television(String brand) {
        // Проверка входных данных
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Марка не может быть пустой");
        }

        this.brand = brand;
        this.isOn = false;
        this.currentChannel = MIN_CHANNEL; // Начинаем с первого канала
        this.volume = DEFAULT_VOLUME;      // Умеренная стартовая громкость
    }

    // 4. Геттеры (свойства только для чтения)
    public String getBrand() {
        return brand;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getCurrentChannel() {
        return currentChannel;
    }

    public int getVolume() {
        return volume;
    }

    // 5. Основные методы управления

    // Включение телевизора
    public void turnOn() {
        if (isOn) {
            System.out.println("Телевизор уже включен");
            return;
        }
        isOn = true;
        System.out.println("Телевизор " + brand + " включен. Канал: " + currentChannel + ", Громкость: " + volume + "%");
    }

    // Выключение телевизора
    public void turnOff() {
        if (!isOn) {
            System.out.println("Телевизор уже выключен");
            return;
        }
        isOn = false;
        System.out.println("Телевизор " + brand + " выключен");
    }

    // Переключение на следующий канал (циклическое)
    public void nextChannel() {
        if (!isOn) {
            System.out.println("[Ошибка] Телевизор выключен! Сначала включите телевизор");
            return;
        }

        currentChannel = (currentChannel == MAX_CHANNEL) ? MIN_CHANNEL : currentChannel + 1;
        System.out.println("Переключено на канал: " + currentChannel);
    }

    // Переключение на предыдущий канал (циклическое)
    public void previousChannel() {
        if (!isOn) {
            System.out.println("[Ошибка] Телевизор выключен! Сначала включите телевизор");
            return;
        }

        currentChannel = (currentChannel == MIN_CHANNEL) ? MAX_CHANNEL : currentChannel - 1;
        System.out.println("Переключено на канал: " + currentChannel);
    }

    // Установка конкретного канала
    public void setChannel(int channel) {
        if (!isOn) {
            System.out.println("[Ошибка] Телевизор выключен! Сначала включите телевизор");
            return;
        }

        if (channel < MIN_CHANNEL || channel > MAX_CHANNEL) {
            System.out.println("[Ошибка] Недопустимый канал! Допустимый диапазон: " +
                    MIN_CHANNEL + "-" + MAX_CHANNEL);
            return;
        }

        currentChannel = channel;
        System.out.println("Установлен канал: " + currentChannel);
    }

    // Увеличение громкости
    public void increaseVolume() {
        if (!isOn) {
            System.out.println("[Ошибка] Телевизор выключен! Сначала включите телевизор");
            return;
        }

        int newVolume = Math.min(volume + VOLUME_STEP, MAX_VOLUME);
        if (newVolume == volume) {
            System.out.println("Максимальная громкость уже достигнута!");
            return;
        }

        volume = newVolume;
        System.out.println("Громкость увеличена до: " + volume + "%");
    }

    // Уменьшение громкости
    public void decreaseVolume() {
        if (!isOn) {
            System.out.println("[Ошибка] Телевизор выключен! Сначала включите телевизор");
            return;
        }

        int newVolume = Math.max(volume - VOLUME_STEP, MIN_VOLUME);
        if (newVolume == volume) {
            System.out.println("Минимальная громкость уже достигнута!");
            return;
        }

        volume = newVolume;
        System.out.println("Громкость уменьшена до: " + volume + "%");
    }
}