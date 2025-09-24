import matplotlib.pyplot as plt

# Исходные данные
data = [4.11, 3.65, 4.04, 3.61, 4.5, 4.07, 4.56, 4.62, 2.64, 3.91,
        4.48, 3.94, 3.43, 3.59, 4.91, 3.37, 4.4, 4.16, 4.56, 4.15]
n = len(data)
print("═" * 50)
print("Исходные данные:", [f"{x:.2f}" for x in data])
print("Количество элементов (n):", n)


# 1) Математическое ожидание и дисперсия
def calculate_mean_variance(data):
    print("\n" + "═" * 20 + " Вычисление мат. ожидания " + "═" * 20)
    total = 0
    for i, num in enumerate(data, 1):
        total += num
        print(f"Шаг {i:2d}: {num:.4f} → Сумма = {total:.4f}")

    mean = total / n
    print("\n" + "-" * 50)
    print(f"Финальная сумма = {total:.4f}")
    print(f"Мат. ожидание = {total:.4f} / {n} = {mean:.4f}")

    print("\n" + "═" * 20 + " Вычисление дисперсии " + "═" * 20)
    squared_diff = 0
    for i, num in enumerate(data, 1):
        deviation = num - mean
        squared_diff += deviation ** 2
        print(f"Шаг {i:2d}: ({num:.4f}-{mean:.4f})² = {deviation ** 2:.4f} → Σ = {squared_diff:.4f}")

    variance = squared_diff / (n-1)
    print("\n" + "-" * 50)
    print(f"Сумма квадратов отклонений = {squared_diff:.4f}")
    print(f"Дисперсия = {squared_diff:.4f} / {n} = {variance:.4f}")
    return mean, variance


mean, variance = calculate_mean_variance(data)


# 2) Сортировка и графики
def manual_sort(data):
    print("\n" + "═" * 20 + " Сортировка данных " + "═" * 20)
    sorted_data = data.copy()
    for i in range(len(sorted_data)):
        for j in range(i + 1, len(sorted_data)):
            if sorted_data[i] > sorted_data[j]:
                sorted_data[i], sorted_data[j] = sorted_data[j], sorted_data[i]
        if i < 3 or i >= len(sorted_data) - 3:  # Выводим только первые и последние итерации
            print(f"После {i + 1:2d}-й итерации: {[f'{x:.2f}' for x in sorted_data]}")
    return sorted_data


sorted_data = manual_sort(data)
print("\n" + "-" * 50)
print("Отсортированный ряд:")
for i, x in enumerate(sorted_data, 1):
    print(f"{i:2d}: {x:.4f}")


# Построение графиков
def plot_variation_series_and_ecdf(sorted_data):
    print("\n" + "═" * 20 + " Построение графиков " + "═" * 20)

    # Подготовка данных для ECDF
    x = sorted_data
    y = [i / n for i in range(1, n + 1)]

    # Создаем фигуру с двумя графиками
    plt.figure(figsize=(15, 6))

    # Вариационный ряд
    plt.subplot(1, 2, 1)
    plt.stem(sorted_data, markerfmt='o', linefmt='C0-', basefmt='C0-')
    plt.title('Вариационный ряд (отсортированные данные)')
    plt.xlabel('Порядковый номер')
    plt.ylabel('Порядковый номер')
    plt.grid(True, alpha=0.3)

    # Эмпирическая функция распределения
    plt.subplot(1, 2, 2)
    plt.step(x, y, where='post', color='C1')
    plt.title('Эмпирическая функция распределения (ECDF)')
    plt.xlabel('x')
    plt.ylabel('F(x)')
    plt.grid(True, alpha=0.3)

    plt.tight_layout()
    print("Графики успешно построены! Закройте окно графиков для продолжения...")
    plt.show()


plot_variation_series_and_ecdf(sorted_data)


# 3) Медиана
def calculate_median(sorted_data):
    print("\n" + "═" * 20 + " Вычисление медианы " + "═" * 20)
    n = len(sorted_data)
    mid = n // 2
    print(f"Количество элементов: {n} ({'нечётное' if n % 2 else 'чётное'})")

    if n % 2 == 1:
        median = sorted_data[mid]
        print(f"Медиана = элемент на позиции {mid + 1}: {sorted_data[mid]:.4f}")
    else:
        median = (sorted_data[mid - 1] + sorted_data[mid]) / 2
        print(f"Медиана = среднее между элементами {mid} и {mid + 1}:")
        print(f"({sorted_data[mid - 1]:.4f} + {sorted_data[mid]:.4f}) / 2 = {median:.4f}")
    return median


median = calculate_median(sorted_data)

# Итоговый вывод
print("\n" + "═" * 50)
print(" " * 15 + "ИТОГОВЫЕ РЕЗУЛЬТАТЫ")
print("═" * 50)
print(f"1) Математическое ожидание: {mean:.4f}")
print(f"2) Дисперсия: {variance:.4f}")
print(f"3) Медиана: {median:.4f}")
print("═" * 50)