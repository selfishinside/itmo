#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <unordered_map>

using namespace std;

int main() {
    string s;
    cin >> s;

    vector<long long> weights(26);
    for (int i = 0; i < 26; ++i) {
        cin >> weights[i];
    }

    // Создаем мапу для хранения веса каждого символа
    unordered_map<char, long long> weight_map;
    for (int i = 0; i < 26; ++i) {
        weight_map['a' + i] = weights[i];
    }

    // Сортируем строку по весу символов
    stable_sort(s.begin(), s.end(), [&](char a, char b) {
        return weight_map[a] > weight_map[b];
    });

    // Выводим результат
    cout << s << endl;

    return 0;
}
