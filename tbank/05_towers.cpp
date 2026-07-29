#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<pair<int, int>> seg(q);
    for (int i = 0; i < q; ++i) {
        cin >> seg[i].first >> seg[i].second;
    }

    sort(seg.begin(), seg.end());  // by l ascending

    int cur = 1;   // first uncovered village
    int ans = 0;
    int i = 0;

    while (cur <= n) {
        int farthest = -1;
        // among all towers with l <= cur, take the one reaching farthest
        while (i < q && seg[i].first <= cur) {
            farthest = max(farthest, seg[i].second);
            ++i;
        }
        if (farthest < cur) {
            cout << "No\n";
            return 0;
        }
        cur = farthest + 1;
        ++ans;
    }

    cout << "Yes\n" << ans << '\n';
    return 0;
}
