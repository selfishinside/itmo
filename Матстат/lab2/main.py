import numpy as np

sample = [
    5, 9, 4, 6, 5, 4, 9, 5, 8, 2,
    10, 4, 7, 4, 5, 5, 4, 7, 7, 8,
    2, 8, 8, 6, 6, 3, 5, 2, 8, 4,
    5, 5, 3, 2, 3, 2, 5, 5, 3, 5,
    5, 4, 6, 9, 6, 5, 4, 4, 7, 4
]

theta = np.mean(sample)

alpha = 0.95
z = 1.96 #1.645 1.96
n = len(sample)
se = np.sqrt(theta / n)

ci_lower = theta - z * se
ci_upper = theta + z * se

# Выводим результат
print(f"Точечная оценка θ (λ): {theta:.2f}")
print(f"95% доверительный интервал: ({ci_lower:.2f}, {ci_upper:.2f})")


