#include <iostream>
#include <vector>
#include <string>
#include <cctype>  // Для tolower()

using namespace std;

void solve(const string& s) {
    int n = s.length() / 2;  
    vector<int> animals;    
    vector<int> traps;       
    vector<int> result(n, -1);

    // Проходим по строке и собираем индексы животных и ловушек
    for (int i = 0; i < s.length(); ++i) {
        if (islower(s[i])) {  // Это животное
            animals.push_back(i);
        } else if (isupper(s[i])) {  // Это ловушка
            traps.push_back(i);
        }
    }

    // Для каждой ловушки находим соответствующее животное
    for (int i = 0; i < n; ++i) {
        int animal_idx = -1;
        int trap_idx = traps[i];

        // Находим животное, которое может попасть в эту ловушку
        

        // Если животного для ловушки не нашли, выводим Impossible
        if (animal_idx == -1) {
            cout << "Impossible" << endl;
            return;
        }

        result[i] = animal_idx; 
    }

    // Выводим результат
    cout << "Possible" << endl;
    for (int i = 0; i < n; ++i) {
        cout << result[i] << " ";
    }
    cout << endl;
}

int main() {
    string s;
    cin >> s;  // Вводим строку с клавиатуры

    solve(s);
    return 0;
}
