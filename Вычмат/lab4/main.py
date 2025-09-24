import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit
from scipy.stats import pearsonr
import sys
import os


def linear(x, a, b):
    return a * x + b


def poly2(x, a, b, c):
    return a * x ** 2 + b * x + c


def poly3(x, a, b, c, d):
    return a * x ** 3 + b * x ** 2 + c * x + d


def exponential(x, a, b):
    return a * np.exp(b * x)


def logarithmic(x, a, b):
    return a * np.log(x) + b


def power(x, a, b):
    return a * x ** b


def mse(y, y_pred):
    return np.mean((y - y_pred) ** 2)


def rmse(y, y_pred):
    return np.sqrt(mse(y, y_pred))


def sse(y, y_pred):
    return np.sum((y - y_pred) ** 2)


def safe_fit(func, x, y, p0=None, filter_positive=False):
    try:
        if filter_positive:
            mask = (x > 0) & (y > 0)
            if np.sum(mask) < 3:
                return None, None, float('inf')
            x, y = x[mask], y[mask]
        popt, pcov = curve_fit(func, x, y, p0=p0, maxfev=10000)
        y_pred = func(x, *popt)
        return popt, y_pred, rmse(y, y_pred)
    except Exception:
        return None, None, float('inf')


def validate_input(value, min_val, max_val, prompt):
    while True:
        try:
            num = float(value.replace(',', '.'))
            if min_val <= num <= max_val:
                return num
            else:
                print(f"Значение должно быть от {min_val} до {max_val}")
                value = input(prompt)
        except ValueError:
            print("Введите корректное число")
            value = input(prompt)


def read_data():
    mode = input("Ввод из файла (f) или с консоли (c)? ").strip().lower()
    while mode not in ['f', 'c']:
        print("Пожалуйста, введите 'f' или 'c'")
        mode = input("Ввод из файла (f) или с консоли (c)? ").strip().lower()

    if mode == 'f':
        while True:
            fname = input("Имя файла: ").strip()
            try:
                if not os.path.exists(fname):
                    raise FileNotFoundError("Файл не найден")

                data = np.loadtxt(fname)
                if data.ndim != 2 or data.shape[1] != 2:
                    raise ValueError("Файл должен содержать два столбца чисел")

                if len(data) < 8 or len(data) > 12:
                    print("Предупреждение: количество точек должно быть от 8 до 12 для лучшей аппроксимации")

                return data[:, 0], data[:, 1]

            except Exception as e:
                print(f"Ошибка: {e}. Попробуйте еще раз.")
    else:
        while True:
            try:
                n = int(input("Количество точек (8–12): "))
                if 8 <= n <= 12:
                    break
                print("Число точек должно быть от 8 до 12")
            except ValueError:
                print("Введите целое число")

        print(f"Вводите по две пары чисел на каждой строке (Xi Yi)")
        data = []
        for i in range(n):
            while True:
                try:
                    row = input(f"[{i + 1}] x y: ")
                    parts = row.strip().split()
                    if len(parts) != 2:
                        print("Введите ровно два числа через пробел.")
                        continue
                    x_val, y_val = map(lambda x: validate_input(x, -1e6, 1e6, f"[{i + 1}] x y: "), parts)
                    data.append((x_val, y_val))
                    break
                except Exception as e:
                    print(f"Ошибка: {e}. Введите два числа через пробел.")

        return np.array(data).T


def print_table(x, y, y_pred, model_name):
    print(f"\nМОДЕЛЬ: {model_name.upper()}\n")
    print("Таблица значений:")
    print(f"     x       y    φ(x) ({model_name:14})        ε")

    for xi, yi, ypi in zip(x, y, y_pred):
        print(f"{xi:8.4f} {yi:8.4f} {ypi:18.4f} {yi - ypi:12.4f}")


def print_results(x, y, y_pred, coeffs, model_name):
    print_table(x, y, y_pred, model_name)

    print("\nКоэффициенты:")
    model_lower = model_name.lower()
    if model_lower == "линейная":
        print(f"a = {coeffs[0]:.6f}, b = {coeffs[1]:.6f}")
    elif model_lower == "полином 2-ст":
        print(f"a = {coeffs[0]:.6f}, b = {coeffs[1]:.6f}, c = {coeffs[2]:.6f}")
    elif model_lower == "полином 3-ст":
        print(f"a = {coeffs[0]:.6f}, b = {coeffs[1]:.6f}, c = {coeffs[2]:.6f}, d = {coeffs[3]:.6f}")
    else:
        for i, c in enumerate(coeffs):
            print(f"a{i} = {c:.6f}", end=", " if i < len(coeffs) - 1 else "\n")

    s = sse(y, y_pred)
    rmse_val = rmse(y, y_pred)
    print(f"\nМера отклонения (S): {s:.6f}")
    print(f"Среднеквадратичное отклонение (СКО): {rmse_val:.6f}")

    if model_lower == "линейная":
        r, _ = pearsonr(x, y)
        r2 = r ** 2
        print(f"Коэффициент корреляции Пирсона: {r:.6f}")
        print(f"Коэффициент детерминации (R²): {r2:.6f}")

        print("\nИнтерпретация R²:")
        if r2 >= 0.95:
            print("Высокое соответствие модели данным (R² ≥ 0.95)")
        elif r2 >= 0.75:
            print("Удовлетворительное соответствие модели данным (0.75 ≤ R² < 0.95)")
        elif r2 >= 0.5:
            print("Умеренное соответствие модели данным (0.5 ≤ R² < 0.75)")
        else:
            print("Недостаточное соответствие модели данным (R² < 0.5)")
    else:
        y_mean = np.mean(y)
        ss_tot = np.sum((y - y_mean) ** 2)
        ss_res = np.sum((y - y_pred) ** 2)
        r2 = 1 - (ss_res / ss_tot) if ss_tot != 0 else 0
        print(f"Коэффициент детерминации (R²): {r2:.6f}")

        print("\nИнтерпретация R²:")
        if r2 >= 0.95:
            print("Высокое соответствие модели данным (R² ≥ 0.95)")
        elif r2 >= 0.75:
            print("Удовлетворительное соответствие модели данным (0.75 ≤ R² < 0.95)")
        elif r2 >= 0.5:
            print("Умеренное соответствие модели данным (0.5 ≤ R² < 0.75)")
        else:
            print("Недостаточное соответствие модели данным (R² < 0.5)")


def output_results(results, best_func, output_mode):
    global x, y
    if output_mode == 'f':
        fname = input("Введите имя файла для сохранения результатов: ")
        with open(fname, 'w', encoding='utf-8') as f:
            original_stdout = sys.stdout
            sys.stdout = f
            for name, (popt, y_fit, err) in results.items():
                print_results(x, y, y_fit, popt, name)
            print("\nИТОГОВЫЙ РЕЗУЛЬТАТ")
            print(f"Наилучшая модель: {best_func[0].upper()}")
            print(f"Среднеквадратичное отклонение (СКО): {best_func[1][2]:.6f}")
            sys.stdout = original_stdout
        print(f"Результаты сохранены в файл {fname}")
    else:
        for name, (popt, y_fit, err) in results.items():
            print_results(x, y, y_fit, popt, name)
        print("\nИТОГОВЫЙ РЕЗУЛЬТАТ")
        print(f"Наилучшая модель: {best_func[0].upper()}")
        print(f"Среднеквадратичное отклонение (СКО): {best_func[1][2]:.6f}")


def plot_results(x, y, results, best_func):
    plt.figure(figsize=(10, 6))
    x_dense = np.linspace(min(x) - 0.1, max(x) + 0.1, 500)

    plt.scatter(x, y, color='black', s=50, label='Исходные данные', zorder=5)

    func_mapping = {
        'линейная': linear,
        'полином 2-ст': poly2,
        'полином 3-ст': poly3,
        'экспоненциальная': exponential,
        'логарифмическая': logarithmic,
        'степенная': power
    }

    for name, (popt, _, _) in results.items():
        func = func_mapping.get(name.lower())
        if func is None or popt is None:
            continue
        try:
            if name.lower() in ['логарифмическая', 'степенная']:
                x_plot = x_dense[x_dense > 0]
            else:
                x_plot = x_dense
            y_plot = func(x_plot, *popt)
            plt.plot(x_plot, y_plot, label=name, linewidth=2)
        except Exception:
            continue

    best_name = best_func[0]
    best_popt = best_func[1][0]
    best_func = func_mapping.get(best_name.lower())

    if best_func is not None and best_popt is not None:
        if best_name.lower() in ['логарифмическая', 'степенная']:
            x_plot = x_dense[x_dense > 0]
        else:
            x_plot = x_dense
        y_plot = best_func(x_plot, *best_popt)
        plt.plot(x_plot, y_plot, 'k--', linewidth=0.5,
                 label=f'Лучший результат: {best_name.upper()}')

    plt.grid(True)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.title('Аппроксимация данных разными моделями')
    plt.legend()
    plt.show()

if __name__ == '__main__':
    x, y = read_data()

    results = {}

    popt, y_fit, err = safe_fit(linear, x, y, p0=[1, 0])
    results['линейная'] = (popt, y_fit, err)

    popt, y_fit, err = safe_fit(poly2, x, y, p0=[1, 1, 1])
    results['полином 2-ст'] = (popt, y_fit, err)

    popt, y_fit, err = safe_fit(poly3, x, y, p0=[1, 1, 1, 1])
    results['полином 3-ст'] = (popt, y_fit, err)

    popt, y_fit, err = safe_fit(exponential, x, y, p0=[1, 0.1])
    results['экспоненциальная'] = (popt, y_fit, err)

    popt, y_fit, err = safe_fit(logarithmic, x, y, p0=[1, 1], filter_positive=True)
    results['логарифмическая'] = (popt, y_fit, err)

    popt, y_fit, err = safe_fit(power, x, y, p0=[1, 1], filter_positive=True)
    results['степенная'] = (popt, y_fit, err)

    best_func = min(results.items(), key=lambda item: item[1][2])

    out_mode = input("Вывод на экран (c) или в файл (f)? ").strip().lower()
    while out_mode not in ['c', 'f']:
        print("Введите 'c' для вывода на экран или 'f' для записи в файл")
        out_mode = input("Вывод на экран (c) или в файл (f)? ").strip().lower()

    output_results(results, best_func, out_mode)

    plot_results(x, y, results, best_func)
