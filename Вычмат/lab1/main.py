import numpy as np


def gauss_elimination_pivot(A, b):
    """
    Решение СЛАУ методом Гаусса с выбором главного элемента по столбцам.
    A - квадратная матрица коэффициентов 
    b - столбец свободных членов
    Возвращает решение x или None, если система вырождена
    """
    n = len(A)
    A = A.astype(float)
    b = b.astype(float)

    for col in range(n):
        # макс
        max_row = np.argmax(abs(A[col:, col])) + col if np.any(A[col:, col] != 0) else col

        if A[max_row, col] == 0:
            continue

        # Перестановка строк
        if max_row != col:
            A[[col, max_row]] = A[[max_row, col]]
            b[[col, max_row]] = b[[max_row, col]]

        # Прямой ход
        for row in range(col + 1, n):
            factor = A[row, col] / A[col, col]
            A[row, col:] -= factor * A[col, col:]
            b[row] -= factor * b[col]

    for i in range(n):
        if A[i, i] == 0 and b[i] != 0:
            print("Система не имеет решений (несовместна).")
            return None
        elif A[i, i] == 0 and b[i] == 0:
            print("Система имеет бесконечно много решений.")
            return None

    # Обратный ход
    x = np.zeros(n)
    for i in range(n - 1, -1, -1):
        if A[i, i] == 0:
            x[i] = 0
        else:
            x[i] = (b[i] - np.dot(A[i, i + 1:], x[i + 1:])) / A[i, i]

    return x


def compute_residual(A, x, b):
    "вычисление вектора невязок r = Ax - b"
    if x is None:
        return None
    return np.dot(A, x) - b


def compute_determinant(A):
    "вычисление определителя."
    n = len(A)
    A = A.astype(float)
    det = 1
    for col in range(n):
        if np.all(A[col:, col] == 0):
            return 0

        max_row = np.argmax(abs(A[col:, col])) + col
        if max_row != col:
            A[[col, max_row]] = A[[max_row, col]]
            det *= -1

        det *= A[col, col]
        if A[col, col] == 0:
            return 0

        for row in range(col + 1, n):
            factor = A[row, col] / A[col, col]
            A[row, col:] -= factor * A[col, col:]

    return det


def read_matrix_from_file(filename):
    """Чтение матрицы A и вектора b из файла."""
    try:
        with open(filename, 'r') as f:
            lines = f.readlines()
        n = int(lines[0].strip())
        A = np.array([list(map(float, line.strip().split())) for line in lines[1:n + 1]])
        b = np.array(list(map(float, lines[n + 1].strip().split())))
        if len(b) != n:
            raise ValueError("Размерность вектора b не совпадает с размерностью матрицы A.")
        return A, b
    except Exception as e:
        print(f"Ошибка при чтении файла: {e}")
        exit()


def read_matrix_from_input():
    """Чтение матрицы A и вектора b с клавиатуры с проверкой ввода."""
    while True:
        try:
            n = int(input("Введите размерность матрицы (целое положительное число): "))
            if n <= 0:
                raise ValueError
            break
        except ValueError:
            print("Ошибка! Введите положительное целое число.")

    A = np.zeros((n, n))
    for i in range(n):
        while True:
            try:
                row = list(map(float, input(f"Введите строку {i + 1} (через пробел): ").strip().split()))
                if len(row) != n:
                    raise ValueError
                A[i] = row
                break
            except ValueError:
                print(f"Ошибка! Введите {n} вещественных чисел, разделённых пробелами.")

    while True:
        try:
            b = list(map(float, input("Введите свободные члены (через пробел): ").strip().split()))
            if len(b) != n:
                raise ValueError
            b = np.array(b)
            break
        except ValueError:
            print(f"Ошибка! Введите {n} вещественных чисел для вектора b.")

    return A, b


if __name__ == "__main__":
    while True:
        choice = input("Ввести данные с клавиатуры (1) или из файла (2)? ").strip()
        if choice in ["1", "2"]:
            break
        print("Некорректный ввод! Введите 1 или 2.")

    if choice == "1":
        A, b = read_matrix_from_input()
    else:
        filename = input("Введите имя файла: ")
        A, b = read_matrix_from_file(filename)

    x = gauss_elimination_pivot(A, b)
    residual = compute_residual(A, x, b)
    det_A = compute_determinant(A)

    print("\nРешение системы x:", x)
    print("\nВектор невязок r = Ax - b:", residual)
    print("\nОпределитель матрицы A:", det_A)

    if x is not None:
        try:
            x_np = np.linalg.solve(A, b)
            print("\nПроверка с помощью numpy.linalg.solve:", x_np)
        except np.linalg.LinAlgError:
            print("\nnumpy.linalg.solve не может решить эту систему (матрица вырождена)")