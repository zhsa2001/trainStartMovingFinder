package ui

/**
 * Стадии обработки графика Филевской линии для табло
 */
enum class UIStage {
    /**
     * Начало. Выбор половины графика (от Александровского сада или от Москвы-Сити) для обработки
     */
    START,

    /**
     * Настройка параметров перед обработкой верхней половины графика, от Александровского сада
     */
    SETTINGS_PROCESSING_UP,
    ROUTE_PROCESSING_UP, SETTINGS_PROCESSING_DOWN, ROUTE_PROCESSING_DOWN, DONE
}