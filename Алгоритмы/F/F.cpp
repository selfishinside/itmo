#include <algorithm>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

int main() {
  vector<string> a, b;
  string c;
  string d = "";

  ifstream infile("number.in");

  if (!infile.is_open()) {
    cerr << "Error opening file!" << endl;
    return 1;
  }

  while (infile >> c) {
    a.push_back(c);
  }

  infile.close();

  for (size_t i = 0; i < a.size(); i++) {
    b.push_back(a[i]);
  }

  sort(b.begin(), b.end(),
       [](const string &x, const string &y) { return x + y > y + x; });

  if (b.size() > 0 && b[0] == "0") {
    cout << "0" << endl;
    return 0;
  }

  for (size_t i = 0; i < b.size(); i++) {
    d += b[i];
  }

  cout << d << endl;

  return 0;
}
