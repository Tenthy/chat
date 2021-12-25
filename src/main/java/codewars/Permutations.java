package codewars;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Permutations {

    public static void main(String[] args) {
        String s = "aabb";
        System.out.println(singlePermutations(s));
    }

    //aabb -
    //abab
    //baba
    //abba
    //baab -
    //bbaa

    public static List<String> singlePermutations(String s) {
        List<String> list = new ArrayList<>();
        list.add(s);
        int allCombinations = IntStream.range(2, s.length()).reduce(1, (a, b) -> a * b);
        for (int i = 0; i < s.length(); i++) {
            String[] sArray = s.split("");
            for (int j = 0; j < s.length(); j++) {
                if (i == j) {
                    continue;
                }
                String temp = sArray[i];
                sArray[i] = sArray[j];
                sArray[j] = temp;
                String sJoin = String.join("", sArray);
                boolean isUnique = false;
                for (int l = 0; l < list.size(); l++) {
                    isUnique = !list.get(l).equals(sJoin);
                }
                if (isUnique) {
                    list.add(sJoin);
                }
                temp = sArray[j];
                sArray[j] = sArray[i];
                sArray[i] = temp;
            }
        }
        return list;
    }
}
