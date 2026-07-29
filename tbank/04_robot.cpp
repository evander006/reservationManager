#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    string s;
    cin >> n >> s;

    vector<int> pref(n + 1);
    for (int i = 0; i < n; ++i) {
        pref[i + 1] = pref[i] + (s[i] == 'R' ? 1 : -1);
    }

    int total = pref[n];
    if (total == 0) {
        cout << 0 << '\n';
        return 0;
    }

    // Find shortest subarray with sum == total.
    // pref[r] - pref[l] == total  =>  pref[l] == pref[r] - total
    // For min length ending at r, take the latest such l.
    // pref values and (pref - total) lie in [-2n, 2n].
    const int OFF = 2 * n;
    vector<int> last(4 * n + 1, -1);
    last[OFF] = 0;  // pref 0 at index 0

    int ans = n + 1;
    for (int r = 1; r <= n; ++r) {
        int need = pref[r] - total;
        int id = need + OFF;
        if (last[id] != -1) {
            ans = min(ans, r - last[id]);
        }
        last[pref[r] + OFF] = r;
    }

    cout << (ans >= n ? -1 : ans) << '\n';
    return 0;
}
