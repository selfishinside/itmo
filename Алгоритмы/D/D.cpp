#include <cstdint>
#include <iostream>

using namespace std;

int main() {
  long long a, b, c, d, k;
  cin >> a >> b >> c >> d >> k;

  long long count = a;

  for (long long day = 1; day <= k; ++day) {
    long long prev_count = count;
    count = count * b;

    if (count < c) {
      cout << 0 << endl;
      return 0;
    }

    count = count - c;

    if (count > d) {
      count = d;
      cout << d << endl;
      return 0;
    }

    if (count == prev_count) {
      cout << count << endl;
      return 0;
    }
  }

  cout << count << endl;
  return 0;
}
