#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

bool verify(const vector<int> &p, int k, int d) {
  int c = 1;
  for (size_t i = 1, lst = 0; i < p.size(); ++i) {
    if (p[i] - p[lst] >= d) {
      c++;
      lst = i;
      if (c >= k)
        return true;
    }
  }
  return false;
}

int solve(vector<int> &p, int k) {
  int l = 0, r = p.back() - p.front(), res = 0;

  while (l <= r) {
    int m = (l + r) >> 1;

    if (verify(p, k, m)) {
      res = m;
      l = m + 1;
    } else {
      r = m - 1;
    }
  }

  return res;
}

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, k;
  cin >> n >> k;

  vector<int> p(n);
  for (int i = 0; i < n; ++i) {
    cin >> p[i];
  }

  sort(p.begin(), p.end());

  cout << solve(p, k) << '\n';

  return 0;
}
