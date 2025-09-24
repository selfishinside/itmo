#include <iostream>
#include <map>
#include <set>
#include <utility>
#include <vector>
using namespace std;
typedef long long ll;

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  ll N;
  int M;
  cin >> N >> M;

  set<pair<ll, ll>> byPos;
  set<pair<ll, ll>> bySize;
  map<pair<ll, ll>, set<pair<ll, ll>>::iterator> posIter;

  byPos.insert({1, N});
  bySize.insert({N, 1});
  posIter[{1, N}] = bySize.begin();

  vector<pair<ll, ll>> rec(M + 1, make_pair(-1, 0));

  for (int i = 1; i <= M; ++i) {
    ll x;
    cin >> x;
    if (x > 0) {
      ll K = x;
      auto it = bySize.lower_bound({K, 0});
      if (it == bySize.end()) {
        cout << -1 << ' ';
        rec[i] = {-1, 0};
      } else {
        ll len = it->first;
        ll s = it->second;
        ll e = s + len - 1;

        cout << s << ' ';
        rec[i] = {s, K};

        bySize.erase(it);
        auto pit = byPos.find({s, e});
        if (pit != byPos.end()) {
          byPos.erase(pit);
          posIter.erase({s, e});
        }

        if (K < len) {
          ll ns = s + K;
          ll ne = e;
          byPos.insert({ns, ne});
          auto itNew = bySize.insert({ne - ns + 1, ns}).first;
          posIter[{ns, ne}] = itNew;
        }
      }
    } else {
      int t = -x;
      if (rec[t].first == -1)
        continue;

      ll s = rec[t].first;
      ll K = rec[t].second;
      ll e = s + K - 1;
      ll ns = s, ne = e;

      auto it = byPos.lower_bound({s, 0});
      if (it != byPos.begin()) {
        auto prev = it;
        --prev;
        if (prev->second + 1 == s) {
          ns = prev->first;
          auto itSize = posIter[*prev];
          bySize.erase(itSize);
          posIter.erase(*prev);
          byPos.erase(prev);
        }
      }

      it = byPos.lower_bound({s, 0});
      if (it != byPos.end() && it->first == e + 1) {
        ne = it->second;
        auto itSize = posIter[*it];
        bySize.erase(itSize);
        posIter.erase(*it);
        byPos.erase(it);
      }

      byPos.insert({ns, ne});
      auto itNew = bySize.insert({ne - ns + 1, ns}).first;
      posIter[{ns, ne}] = itNew;
    }
  }

  return 0;
}
