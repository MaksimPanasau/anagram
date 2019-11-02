package by.ponasovmax;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class Main {
    public static void main(String[] args) throws IOException {
        final List<String> dictionary = Files.readAllLines(Paths.get(args[0]), Charset.forName("Windows-1251"));
        final String word = args[1];
        boolean ignoreCase = args.length > 3;
        collectAnagrams(dictionary.toArray(new String[0]), word, ignoreCase).forEach(System.out::println);
    }

    private static Collection<String> collectAnagrams(String[] dictionary, String searchAnagramsFor, boolean ignoreCase) {
        final Function<Character, Character> characterStrategy = ignoreCase ? Character::toLowerCase : identity();
        final var wordMap = convertWordToMap(searchAnagramsFor, characterStrategy);
        final int[] wordAsArray = wordToArray(searchAnagramsFor, wordMap);

        return Arrays
                .stream(dictionary)
                .parallel()
                .filter(el -> el.length() == searchAnagramsFor.length())
                .filter(el -> isAnagram(el, wordAsArray, wordMap, characterStrategy))
                .collect(Collectors.toList());
    }

    private static Map<Character, Integer> convertWordToMap(
            String searchAnagramsFor,
            Function<Character, Character> characterStrategy) {
        final var wordMap = new HashMap<Character, Integer>(searchAnagramsFor.length());
        int index = 0;
        for (char c : searchAnagramsFor.toCharArray()) {
            if (wordMap.putIfAbsent(characterStrategy.apply(c), index) == null) {
                index++;
            }
        }
        return wordMap;
    }

    private static boolean isAnagram(String candidate,
                                     final int[] wordAsArray,
                                     final Map<Character, Integer> wordMap,
                                     final Function<Character, Character> characterStrategy) {
        int[] wordAsArrayCopy = new int[wordMap.size()];
        System.arraycopy(wordAsArray, 0, wordAsArrayCopy, 0, wordMap.size());

        for (char c : candidate.toCharArray()) {
            Integer index = wordMap.get(characterStrategy.apply(c));
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