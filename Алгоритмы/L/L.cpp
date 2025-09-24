#include <deque>
#include <iostream>
#include <vector>
using namespace std;

int main() {
  ios::sync_with_stdio(false);
  cin.tie(NULL);

  int N, K;
  cin >> N >> K;
  vector<int> A(N);
  for (int i = 0; i < N; i++)
    cin >> A[i];

  deque<int> D;
  for (int i = 0; i < N; i++) {
    while (!D.empty() && D.front() <= i - K)
      D.pop_front();
    while (!D.empty() && A[D.back()] >= A[i])
      D.pop_back();
    D.push_back(i);
    if ((i + 1) % K == 0) {
      int t = D.back();
      D.push_back(t);
      D.pop_back();
    }
    if (i >= K - 1)
      cout << A[D.front()] << ' ';
  }

  return 0;
}
