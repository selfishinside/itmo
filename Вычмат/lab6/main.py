import math
import matplotlib.pyplot as plt

# --- ОДУ и точные решения ---
def f1(x, y): return math.exp(x)
def exact1(x): return math.exp(x) - math.exp(2) + 1

def f2(x, y): return x + y
def exact2(x): return math.exp(x) - x - 1

def f3(x, y): return math.sin(x) - y
def exact3(x): return 0.5 * (math.exp(-x) + math.sin(x) - math.cos(x)) + 0.5 * math.exp(-2)

ODES = {
    1: {"f": f1, "exact": exact1, "title": "y' = e^x"},
    2: {"f": f2, "exact": exact2, "title": "y' = x + y"},
    3: {"f": f3, "exact": exact3, "title": "y' = sin(x) - y"},
}

# --- Модифицированный метод Эйлера + Рунге ---
def improved_euler_runge(f, x0, y0, xn, h, e):
    xs = [x0]
    ys = [y0]
    errors = []

    print(f"0) x = {x0:.5f}, y_0 = {y0:.10f}, Погрешность (Рунге) = 0.0000000000")  # начальная точка

    n = 1
    x, y = x0, y0
    output_start = x0 + h  # первый шаг — с x = x0 + h

    while x < xn - 1e-10:
        # Модифицированный метод Эйлера
        k1 = f(x, y)
        y_tilde = y + h * k1
        k2 = f(x + h, y_tilde)
        y_new = y + (h / 2) * (k1 + k2)

        # Погрешность Рунге
        h4 = h / 4
        y_r = y
        for i in range(4):
            k1_r = f(x + i * h4, y_r)
            y_r += h4 * k1_r
        runge_err = abs(y_new - y_r) / (2 ** 2 - 1)

        if runge_err > e:
            h /= 2
            continue

        x += h
        y = y_new
        xs.append(x)
        ys.append(y)
        errors.append(runge_err)

        print(f"{n}) x = {x:.5f}, y_{n} = {y:.10f}, Погрешность (Рунге) = {runge_err:.10f}")
        n += 1

        if runge_err < e / 4:
            h *= 2

    return xs, ys, errors


# --- Метод Рунге-Кутты 4-го порядка ---
def rk4(f, x0, y0, h, steps):
    xs = [x0]
    ys = [y0]
    x = x0
    y = y0
    for _ in range(steps):
        k1 = f(x, y)
        k2 = f(x + h / 2, y + h * k1 / 2)
        k3 = f(x + h / 2, y + h * k2 / 2)
        k4 = f(x + h, y + h * k3)
        y += h * (k1 + 2 * k2 + 2 * k3 + k4) / 6
        x += h
        xs.append(x)
        ys.append(y)
    return xs, ys

# --- Метод Милна ---
def milne_method(f, xs_init, ys_init, xn, h, exact):
    xs = xs_init[:]
    ys = ys_init[:]
    n = int((xn - xs[-1]) / h)
    results = []

    for _ in range(n):
        x_next = xs[-1] + h
        y_pred = ys[-4] + (4 * h / 3) * (2 * f(xs[-3], ys[-3]) -
                                        f(xs[-2], ys[-2]) + 2 * f(xs[-1], ys[-1]))
        y_corr = ys[-2] + (h / 3) * (f(xs[-2], ys[-2]) +
                                    4 * f(xs[-1], ys[-1]) + f(x_next, y_pred))
        ys.append(y_corr)
        xs.append(x_next)
        results.append((x_next, y_corr, exact(x_next)))

    max_err = max(abs(y - exact(x)) for x, y in zip(xs[-n:], ys[-n:]))
    return results, max_err, xs, ys

# --- Основной код ---
def main():
    print("Выберете ОДУ:")
    for i, ode in ODES.items():
        print(f"{i}) {ode['title']}")
    num = int(input())
    ode = ODES[num]
    f, exact = ode["f"], ode["exact"]

    y0 = float(input("Введите y0: "))
    x0 = float(input("Введите левую границу x: "))
    xn = float(input("Введите правую границу x: "))
    h = float(input("Введите шаг: "))
    eps = float(input("Введите точность: "))
    print("--------------------------------------\n")

    # Модифицированный Эйлер + Рунге
    print("Модифицированный метод Эйлера")
    xs_euler, ys_euler, errors = improved_euler_runge(f, x0, y0, xn, h, eps)
    print("--------------------------------------")

    # Метод Рунге-Кутты 4 порядка
    steps = int((xn - x0) / h)
    xs_rk4, ys_rk4 = rk4(f, x0, y0, h, steps)
    print("Метод Рунге-Кутты 4 порядка")
    for i in range(len(xs_rk4)):
        print(f"{i}) x = {xs_rk4[i]:.5f}, y = {ys_rk4[i]:.10f}")
    print("--------------------------------------")

    # Метод Милна
    xs_init, ys_init = rk4(f, x0, y0, h, 3)
    results, max_err, xs_milne, ys_milne = milne_method(f, xs_init, ys_init, xn, h, exact)

    print("Метод Милна")
    for i, (x, y, yt) in enumerate(results):
        print(f"{i}) x = {x:.5f}, y_h = {y:.10f}, y_exact = {yt:.10f}")
    print(f"\nПогрешность (max|y_iточн - y_i|): {max_err:.10f}")

    # Построение графика
    xs_exact = [x0 + i * h for i in range(steps + 1)]
    ys_exact = [exact(x) for x in xs_exact]

    plt.figure(figsize=(10, 6))
    plt.plot(xs_exact, ys_exact, 'k--', label='Точное решение')
    plt.plot(xs_euler, ys_euler, 'ro-', label='Модифицированный Эйлер')
    plt.plot(xs_rk4, ys_rk4, 'bs-', label='Рунге-Кутта 4 порядка')
    plt.plot(xs_milne, ys_milne, 'g^-', label='Метод Милна')

    plt.title(f"Сравнение численных методов решения: {ode['title']}")
    plt.xlabel("x")
    plt.ylabel("y")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    main()