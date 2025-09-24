#include <iostream>
#include <string>
#include <unordered_map>
#include <vector>

using namespace std;

unordered_map<string, vector<int>> var_history;
vector<vector<string>> scope_stack;

void assign_variable(const string& lhs, const string& rhs) {
  if (!isalpha(rhs[0]))
    return;
  int value = var_history[rhs].empty() ? 0 : var_history[rhs].back();
  cout << value << "\n";
  var_history[lhs].push_back(value);
  scope_stack.back().push_back(lhs);
}

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  scope_stack.emplace_back();
  string input;
  while (cin >> input) {
    if (input == "{") {
      scope_stack.emplace_back();
    } else if (input == "}") {
      for (const string& var : scope_stack.back()) {
        var_history[var].pop_back();
      }
      scope_stack.pop_back();
    } else {
      size_t eq_pos = input.find('=');
      string var = input.substr(0, eq_pos);
      string value = input.substr(eq_pos + 1);

      if (isalpha(value[0])) {
        assign_variable(var, value);
      } else {
        int num_value = stoi(value);
        var_history[var].push_back(num_value);
        scope_stack.back().push_back(var);
      }
    }
  }
  return 0;
}
