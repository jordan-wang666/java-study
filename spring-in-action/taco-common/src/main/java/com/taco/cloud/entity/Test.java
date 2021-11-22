package com.taco.cloud.entity;

public class Test {

    static class Solution {
        public int lengthOfLongestSubstring(String s) {
            int count = 0;
            for (int i = 0; i < s.length(); i++) {
                String regularStr = s.substring(0, i);
                String str = s.substring(i, i);
            }
            return count;
        }
    }


    public static void main(String[] args) {
        Solution solution = new Solution();
        int pwwkew = solution.lengthOfLongestSubstring("abcabcbb");
        System.out.println("pwwkew = " + pwwkew);
    }
}
