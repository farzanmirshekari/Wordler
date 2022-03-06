import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Wordler {

    private Set<Character> bonusLetters = new HashSet<>(Arrays.asList('r','l','s','t','n','e'));

    public static void main(String[] args) throws IOException {
        new Wordler().run(5);     
    }

    private void run(int wordLength) throws IOException {
        Set<String> dictionary = getWords("word_list.txt", wordLength);

        Set<String> words = new HashSet<>(dictionary);

        boolean foundWinningWord = false;

        try(Scanner scanner = new Scanner(System.in)) {

            for(int i = 0; i < 6 && !foundWinningWord; i++) {

                String tryWord = getTryWord(words);
                String result;

                do {
                    System.out.println("#" + (i+1) + " Try: " + tryWord.toUpperCase() + " (out of " + words.size() + " words)");
                    System.out.println("(g = green, y = yellow, a = absent)");
                    result = scanner.nextLine().trim();

                    if(result.startsWith("get ")) {
                        printWordsWith(dictionary, result.substring(4).toCharArray());
                    }
                    else {
                        words = getNewWords(words, tryWord, result);
                    }

                    if (words.size() == 1) {
                        System.out.println("The winning word is " + words.toArray()[0].toString().toUpperCase());
                        foundWinningWord = true;
                    }

                } while(result.startsWith("get "));
            }

        }
    }

    private Set<String> getWords(String path, int wordLength) throws IOException {

        Set<String> words = new HashSet<>();

        for (String word : Files.readAllLines(Paths.get(path))) {

            String wordIterable = word.replaceAll("[\\[\\],\\\"]", "");
            if(wordIterable.length() == wordLength) {
                words.add(wordIterable);
            }

        }
        return words;
    }

    private String getTryWord(Set<String> words) {

        String bestWord = null;
        int maxUniqueLetters =0;
        int maxScore = words.iterator().next().length()*2;
        Set<Character> letters = new HashSet<>();

        for (String word : words) {
            
            for (int i = 0; i < word.length(); i++) {
                
                letters.add(word.charAt(i));

            }

            int score = letters.size();

            for (char c : letters) {
                
                if (bonusLetters.contains(c)) {
                    score++;
                }

            }

            if(score == maxScore) {
                return word;
            }
            else if (letters.size() > maxUniqueLetters) {
                maxUniqueLetters = letters.size();
                bestWord = word;
            }

            letters.clear();

        }
        
        return bestWord;

    }

    private Set<String> getNewWords(Set<String> words, String tryWord, String result) {
        
        Set<String> newWords = new HashSet<>();

        System.out.println("------------------");

        for (String word : words) {
            
            boolean isWordValid = true;
            char[] wordCharacters = word.toCharArray();

            for (int i = 0; i < tryWord.length(); i++) {

                char resultChar = result.charAt(i);
                if ((resultChar == 'g' && wordCharacters[i] != tryWord.charAt(i))
                    || (resultChar == 'y' && (wordCharacters[i] == tryWord.charAt(i) || !contains(wordCharacters, tryWord.charAt(i))))
                    || (resultChar == 'a' && contains(wordCharacters, tryWord.charAt(i)))) {
            
                        isWordValid = false;
                        break;

                } else if(resultChar == 'g') {
                    wordCharacters[i] = '-';
                }
            }

            if (isWordValid) {
                System.out.println(word);
                newWords.add(word);
            }

        }

        System.out.println("------------------");

        return newWords;

    }

    private boolean contains(char[] characters, char targetCharacter) {
        
        for (char character : characters) {
            if (character == targetCharacter) return true;
        }

        return false;
    }

    private void printWordsWith(Set<String> dictionary, char[] targetCharacters) {

        int bestScore = 2;

        for (String word : dictionary) {

            Set<Character> letters = new HashSet<>(); 

            for (int i = 0; i < word.length(); i++) {
                letters.add(word.charAt(i));
            }

            int score = 0;

            for (char letter : letters) {
                if(contains(targetCharacters, letter)) score++;
            }

            if (score >= bestScore) {

                System.out.print(word);

                for (int i = 2; i <= score; i++) {
                    System.out.print("*");
                }

                System.out.println();

                if (score > bestScore) bestScore = score;
            }
        }

    }

}