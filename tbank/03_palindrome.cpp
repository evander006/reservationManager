#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;
    string s;
    cin >> s;
    vector<int> a(n);
    for (int i = 0; i < n; ++i) {
        cin >> a[i];
    }

    // dp[i][j][p] — max cost of palindromic subsequence on s[i..j]
    // using at most p mismatched symmetric pairs
    vector<vector<vector<int>>> dp(
        n, vector<vector<int>>(n, vector<int>(k + 1, 0)));

    for (int i = 0; i < n; ++i) {
        for (int p = 0; p <= k; ++p) {
            dp[i][i][p] = max(0, a[i]);
        }
    }

    for (int len = 2; len <= n; ++len) {
        for (int i = 0; i + len - 1 < n; ++i) {
            int j = i + len - 1;
            for (int p = 0; p <= k; ++p) {
                int best = max(dp[i + 1][j][p], dp[i][j - 1][p]);

                auto mid = [&](int pp) -> int {
                    if (i + 1 > j - 1) {
                        return 0;
                    }
                    return dp[i + 1][j - 1][pp];
                };

                if (s[i] == s[j]) {
                    best = max(best, a[i] + a[j] + mid(p));
                } else if (p >= 1) {
                    best = max(best, a[i] + a[j] + mid(p - 1));
                }

                // at most p: extra budget never hurts
                if (p >= 1) {
                    best = max(best, dp[i][j][p - 1]);
                }

                dp[i][j][p] = best;
            }
        }
    }

    cout << dp[0][n - 1][k] << '\n';
    return 0;
}
