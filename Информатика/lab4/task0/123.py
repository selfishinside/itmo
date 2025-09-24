import numpy as np
import matplotlib.pyplot as plt

# Определение параметра t
t = np.linspace(0, 2 * np.pi, 1000)

# Вычисление x и y
x = 2 * np.cos(t)**3
y = 2 * np.sin(t)**3

# Построение графика
plt.figure(figsize=(8, 8))
plt.plot(x, y, label=r'$x = 2\cos^3(t), y = 2\sin^3(t)$')
plt.xlabel('x')
plt.ylabel('y')
plt.title('Параметрическая кривая')
plt.axhline(0, color='black', linewidth=0.5)
plt.axvline(0, color='black', linewidth=0.5)
plt.grid(color='gray', linestyle='--', linewidth=0.5)
plt.legend()
plt.gca().set_aspect('equal', adjustable='box')

# Показать график
plt.show()
