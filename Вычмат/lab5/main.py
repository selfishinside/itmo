
import numpy as np
from matplotlib import pyplot as plt

def finite_differences(y):
    n = len(y)
    table = np.zeros((n, n))
    table[:, 0] = y
    for j in range(1, n):
        for i in range(n - j):
            table[i][j] = table[i+1][j-1] - table[i][j-1]
    return table
def input_manual():
    while True:
        try:
            n = int(input("Введите количество точек: "))
            if n >= 2:  # Минимум 2 точки для интерполяции
                break
            else:
                print("Ошибка: нужно хотя бы 2 точки!")
        except ValueError:
            print("Ошибка: введите целое число!")

    x = []
    y = []
    for i in range(n):
        while True:
            try:
                x_val = float(input(f"x[{i}]: "))
                y_val = float(input(f"y[{i}]: "))
                if x_val in x:
                    print(f"Ошибка: x={x_val} уже существует! Введите уникальное значение.")
                    continue
                x.append(x_val)
                y.append(y_val)
                break
            except ValueError:
                print("Ошибка: введите число!")

    return np.array(x), np.array(y)


def input_file(filename):
    try:
        data = np.loadtxt(filename)
    except (IOError, ValueError) as e:
        print(f"Ошибка чтения файла: {e}")
        return None, None
    if len(data) < 2:
        print("Ошибка: файл должен содержать минимум 2 точки")
        return None, None
    if data.shape[1] != 2:
        print("Ошибка: файл должен содержать ровно 2 столбца (x и y)")
        return None, None
    x = data[:, 0]
    if len(np.unique(x)) != len(x):
        print("Ошибка: значения x должны быть уникальными")
        return None, None

    return x, data[:, 1]

def input_function(func_name, a, b, n):
    x = np.linspace(a, b, n)
    if func_name == "sin":
        y = np.sin(x)
    elif func_name == "cos":
        y = np.cos(x)
    elif func_name == "x^2":
        y = x ** 2
    else:
        raise ValueError("Функция не поддерживается.")
    return x, y

def lagrange(x, y, x_val):
    n = len(x)
    result = 0.0
    for i in range(n):
        term = y[i]
        for j in range(n):
            if i != j:
                term *= (x_val - x[j]) / (x[i] - x[j])
        result += term
    return result

def newton_divided(x, y, x_val):
    n = len(x)
    coef = np.zeros(n)
    coef[0] = y[0]
    for j in range(1, n):
        for i in range(n-1, j-1, -1):
            y[i] = (y[i] - y[i-1]) / (x[i] - x[i-j])
        coef[j] = y[j]
    result = coef[0]
    product = 1.0
    for j in range(1, n):
        product *= (x_val - x[j-1])
        result += coef[j] * product
    return result

def newton_finite(x, y, x_val):
    h = x[1] - x[0]
    n = len(x)
    table = finite_differences(y)
    result = table[0][0]
    u = (x_val - x[0]) / h
    temp = u
    fact = 1
    for i in range(1, n):
        result += (temp * table[0][i]) / fact
        temp *= (u - i)
        fact *= (i + 1)
    return result


def stirling(x, y, x_val):
    n = len(x)
    if n % 2 == 0:
        raise ValueError("Формула Стирлинга требует нечётного количества узлов")

    h = x[1] - x[0]
    mid = n // 2
    u = (x_val - x[mid]) / h

    diff = finite_differences(y)
    result = y[mid]
    factorial = 1

    for i in range(1, mid + 1):
        factorial *= i
        # Чётные разности
        term = (diff[mid - i][2 * i] + diff[mid - i + 1][2 * i]) / (2 * factorial)
        term *= u ** 2 / (i * (i - 0.5))
        result += term

        # Нечётные разности
        term = diff[mid - i][2 * i - 1] / factorial
        term *= u * (u ** 2 - (i - 1) ** 2)
        result += term

    return result


def bessel(x, y, x_val):
    n = len(x)
    if n % 2 != 0:
        raise ValueError("Формула Бесселя требует чётного количества узлов")

    h = x[1] - x[0]
    k = min(int((x_val - x[0]) // h), n - 2)
    u = (x_val - x[k]) / h

    diff = finite_differences(y)
    result = (y[k] + y[k + 1]) / 2 + (u - 0.5) * diff[k][1]
    factorial = 1

    for i in range(2, n - k):
        factorial *= i
        if i % 2 == 0:
            # Чётные разности
            term = diff[k - i // 2][i] / factorial
            for j in range(i):
                term *= (u - j)
        else:
            # Нечётные разности
            term = (diff[k - i // 2][i] + diff[k - i // 2 + 1][i]) / (2 * factorial)
            term *= (u - 0.5)
            for j in range(1, i):
                term *= (u - j)
        result += term

    return result
def is_uniform_grid(x):
    if len(x) < 2:
        return False
    h = x[1] - x[0]
    return all(abs(x[i + 1] - x[i] - h) < 1e-6 for i in range(len(x) - 1))

def main():
    func_name = None  # Инициализация переменной

    print("Выберите способ ввода данных:")
    print("1 - Ручной ввод")
    print("2 - Из файла")
    print("3 - Сгенерировать по функции")
    choice = int(input("Ваш выбор: "))

    if choice == 1:
        x, y = input_manual()
    elif choice == 2:
        filename = input("Введите имя файла: ")
        x, y = input_file(filename)
        if x is None:
            return
    elif choice == 3:
        func_name = input("Выберите функцию (sin/cos/x^2): ")
        a = float(input("Начало интервала: "))
        b = float(input("Конец интервала: "))
        n = int(input("Количество точек: "))
        x, y = input_function(func_name, a, b, n)
    else:
        print("Ошибка ввода!")
        return

    # Вывод таблицы разностей
    print("\nТаблица конечных разностей:")
    print(finite_differences(y))

    # Интерполяция
    x_val = float(input("\nВведите x для интерполяции: "))

    print("\nРезультаты интерполяции:")
    print(f"1. Лагранж: {lagrange(x, y, x_val)}")
    print(f"2. Ньютон (разделённые): {newton_divided(x.copy(), y.copy(), x_val)}")
    print(f"3. Ньютон (конечные): {newton_finite(x, y, x_val)}")

    # Проверка для Стирлинга и Бесселя
    uniform = is_uniform_grid(x)
    if uniform:
        try:
            if len(x) % 2 != 0:  # Нечётное количество узлов
                print(f"\n4. Стирлинг: {stirling(x, y, x_val)}")
            else:
                print("\nФормула Стирлинга не применяется (требуется нечётное количество узлов)")

            if len(x) % 2 == 0:  # Чётное количество узлов
                print(f"5. Бессель: {bessel(x, y, x_val)}")
            else:
                print("Формула Бесселя не применяется (требуется чётное количество узлов)")
        except Exception as e:
            print(f"\nОшибка в специализированных методах: {str(e)}")
    else:
        print("\nПримечание: формулы Стирлинга и Бесселя требуют равномерной сетки")

    # График
    x_new = np.linspace(min(x), max(x), 100)
    y_lagrange = [lagrange(x, y, xi) for xi in x_new]
    y_newton = [newton_finite(x, y, xi) for xi in x_new]

    # Инициализация переменных для Стирлинга и Бесселя
    y_stirling = None
    y_bessel = None

    if is_uniform_grid(x):
        try:
            y_stirling = [stirling(x, y, xi) for xi in x_new]
            y_bessel = [bessel(x, y, xi) for xi in x_new]
        except Exception as e:
            print(f"\nОшибка при вычислении специализированных методов: {str(e)}")

    # Построение графика
    plot_interpolation(
        x=x,
        y=y,
        x_new=x_new,
        y_lagrange=y_lagrange,
        y_newton=y_newton,
        y_stirling=y_stirling,
        y_bessel=y_bessel,
        func_name=func_name if choice == 3 else None
    )
def plot_interpolation(x, y, x_new, y_lagrange, y_newton, y_stirling=None, y_bessel=None, func_name=None):
    """Визуализация интерполяции с поддержкой всех методов"""
    plt.figure(figsize=(10, 6))

    # Узлы интерполяции
    plt.scatter(x, y, color='red', s=100, label='Узлы интерполяции')

    # Методы интерполяции
    plt.plot(x_new, y_lagrange, color='blue', label='Многочлен Лагранжа')
    plt.plot(x_new, y_newton, color='green', label='Многочлен Ньютона')

    if y_stirling is not None:
        plt.plot(x_new, y_stirling, '--', color='purple', label='Стирлинг')
    if y_bessel is not None:
        plt.plot(x_new, y_bessel, ':', color='orange', label='Бессель')

    if func_name:
        plt.title(f'Интерполяция для {func_name}')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.legend()
    plt.grid()
    plt.show()

if __name__ == "__main__":
    main()