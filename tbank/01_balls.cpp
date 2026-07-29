#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    unordered_map<int, int> cnt;
    cnt.reserve(n * 2);

    for (int i = 0; i < n; ++i) {
        int color;
        cin >> color;
        ++cnt[color];
    }

    int odd = 0;
    for (auto& [color, c] : cnt) {
        if (c % 2 != 0) {
            ++odd;
        }
    }

    // Each odd-count color leaves exactly one unpaired ball.
    // At most one ball may stay unpaired.
    cout << (odd <= 1 ? "YES" : "NO") << '\n';
    return 0;
}
