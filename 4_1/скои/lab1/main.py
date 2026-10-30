import argparse
import csv
import time
from pathlib import Path

import cv2
import numpy as np


def median_filter_native(image: np.ndarray, kernel_size: int) -> np.ndarray:
    """Медианный фильтр на чистом Python: сортировка окна, без готовой фильтрации."""
    if kernel_size < 3 or kernel_size % 2 == 0:
        raise ValueError("Размер ядра должен быть нечётным и не меньше 3")
    if image.dtype != np.uint8 or image.ndim != 2:
        raise ValueError("Ожидается изображение в градациях серого, uint8")

    radius = kernel_size // 2
    height, width = image.shape
    result = np.empty_like(image)
    padded = np.pad(image, radius, mode="edge")

    for y in range(height):
        for x in range(width):
            neighborhood = []
            for dy in range(kernel_size):
                for dx in range(kernel_size):
                    neighborhood.append(int(padded[y + dy, x + dx]))
            neighborhood.sort()
            result[y, x] = neighborhood[len(neighborhood) // 2]
    return result


def run(image: np.ndarray, sizes: list[int], output: Path) -> None:
    output.mkdir(parents=True, exist_ok=True)
    cv2.imwrite(str(output / "input.png"), image)
    results = []
    for size in sizes:
        start = time.perf_counter()
        native = median_filter_native(image, size)
        native_time = time.perf_counter() - start

        start = time.perf_counter()
        library = cv2.medianBlur(image, size)
        library_time = time.perf_counter() - start

        equal = np.array_equal(native, library)
        max_diff = int(np.max(np.abs(native.astype(np.int16) - library.astype(np.int16))))
        cv2.imwrite(str(output / f"native_{size}x{size}.png"), native)
        cv2.imwrite(str(output / f"opencv_{size}x{size}.png"), library)
        comparison = np.hstack([image, native, library])
        cv2.imwrite(str(output / f"comparison_{size}x{size}.png"), comparison)
        results.append([size, round(native_time, 6), round(library_time, 6),
                        round(native_time / library_time, 2) if library_time else "inf", equal, max_diff])
        print(f"Ядро {size}x{size}: Python {native_time:.4f} с; OpenCV {library_time:.6f} с; "
              f"совпадение: {equal}; максимальная разница: {max_diff}")

    with (output / "results.csv").open("w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        writer.writerow(["kernel_size", "python_seconds", "opencv_seconds", "opencv_speedup", "equal", "max_difference"])
        writer.writerows(results)


    try:
        import matplotlib
        matplotlib.use("Agg")
        import matplotlib.pyplot as plt
        x = [r[0] for r in results]
        plt.plot(x, [r[1] for r in results], "o-", label="Native Python")
        plt.plot(x, [r[2] for r in results], "s-", label="OpenCV")
        plt.xticks(x)
        plt.xlabel("Kernel size")
        plt.ylabel("Time, seconds")
        plt.title("Median filter runtime")
        plt.grid(True, alpha=0.3)
        plt.legend()
        plt.tight_layout()
        plt.savefig(output / "time_comparison.png", dpi=150)
        plt.close()
    except ImportError:
        print("График не построен (CSV сохранён)")


def parse_args():
    parser = argparse.ArgumentParser(description="ЛР1: медианная фильтрация")
    parser.add_argument("--input", type=Path, required=True, help="Путь к изображению")
    parser.add_argument("--sizes", type=int, nargs="+", default=[3, 5, 7], help="Нечётные размеры ядер, например 3 5 7")
    parser.add_argument("--output", type=Path, default=Path("results"), help="Папка результатов")
    args = parser.parse_args()
    if any(k < 3 or k % 2 == 0 for k in args.sizes):
        parser.error("Все размеры ядра должны быть нечётными числами >= 3")
    return args


if __name__ == "__main__":
    args = parse_args()
    image = cv2.imread(str(args.input), cv2.IMREAD_GRAYSCALE)
    if image is None:
        raise SystemExit(f"Не удалось прочитать изображение: {args.input}")
    run(image, args.sizes, args.output)
