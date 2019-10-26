package by.ponasovmax;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        final List<String> dictionary = Files.readAllLines(Paths.get(args[0]), Charset.forName("Windows-1251"));
        final String word = args[1];
        collectAnagrams(dictionary.toArray(new String[0]), word).forEach(System.out::println);
    }

    private static Collection<String> collectAnagrams(String[] dictionary, String searchAnagramsFor) {
        final var wordMap = convertWordToMap(searchAnagramsFor);
        final int[] wordAsArray = wordToArray(searchAnagramsFor, wordMap);

        return Arrays
                .stream(dictionary)
                .parallel()
                .filter(el -> el.length() == searchAnagramsFor.length())
                .filter(el -> isAnagram(el, wordAsArray, wordMap))
                .collect(Collectors.toList());
    }

    private static Map<Character, Integer> convertWordToMap(String searchAnagramsFor) {
        final Map<Character, Integer> wordMap = new HashMap<>(searchAnagramsFor.length());
        int index = 0;
        for (char c : searchAnagramsFor.toCharArray()) {
            if (wordMap.putIfAbsent(c, index) == null) {
                index++;
            }
        }
        return wordMap;
    }

    private static boolean isAnagram(String candidate,
                                     final int[] wordAsArray,
                                     final Map<Character, Integer> wordMap) {
        int[] wordAsArrayCopy = new int[wordMap.size()];
        System.arraycopy(wordAsArray, 0, wordAsArrayCopy, 0, wordMap.size());

        for (char c : candidate.toCharArray()) {
            Integer index = wordMap.get(c);
            if (index == null) {
                return false;
            }
            int count = wordAsArrayCopy[index] - 1;
            if (count < 0) {
                return false;
            }
            wordAsArrayCopy[index] = count;
        }
        return true;
    }

    private static int[] wordToArray(String word, Map<Character, Integer> wordMap) {
        int[] result = new int[wordMap.size()];
        for (char c : word.toCharArray()) {
            Integer index = wordMap.get(c);
            result[index] = result[index] + 1;
        }
        return result;
    }
}