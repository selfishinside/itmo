#include <deque>
#include <iostream>
using namespace std;

int main() {
  int n, x;
  char c;
  deque<int> a, b;
  cin >> n;
  while (n--) {
    cin >> c;
    if (c == '+') {
      cin >> x;
      b.push_back(x);
    } else if (c == '*') {
      cin >> x;
      a.push_back(x);
    } else {
      cout << a.front() << '\n';
      a.pop_front();
    }
    if (a.size() < b.size()) {
      a.push_back(b.front());
      b.pop_front();
    }
    if (a.size() > b.size() + 1) {
      b.push_front(a.back());
      a.pop_back();
    }
  }
}
