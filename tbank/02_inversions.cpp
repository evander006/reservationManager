#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string s;
    cin >> s;

    // freq[c] = how many times letter c appeared so far (to the left)
    array<int, 26> freq{};
    long long ans = 0;

    for (char ch : s) {
        int x = ch - 'a';
        // count letters already seen that are strictly greater than x
        for (int c = x + 1; c < 26; ++c) {
            ans += freq[c];
        }
        ++freq[x];
    }

    cout << ans << '\n';
    return 0;
}
