#include <iostream>
#include <vector>

using namespace std;

int main() {
  int numFlowers;
  cin >> numFlowers;

  vector<int> flowerTypes(numFlowers);
  for (int i = 0; i < numFlowers; ++i) {
    cin >> flowerTypes[i];
  }

  int maxSegmentLength = 0, bestStart = 0, bestEnd = 0;
  int left = 0;

  for (int right = 0; right < numFlowers; ++right) {
    if (right > 1 && flowerTypes[right] == flowerTypes[right - 1] &&
        flowerTypes[right] == flowerTypes[right - 2]) {
      left = right - 1;
    }

    if (right - left + 1 > maxSegmentLength) {
      maxSegmentLength = right - left + 1;
      bestStart = left;
      bestEnd = right;
    }
  }

  cout << bestStart + 1 << " " << bestEnd + 1 << "\n";
  return 0;
}