import java.net.URL;
import java.util.Scanner;
import java.util.TreeSet;

// This is a bot for the game Word500 (https://www.word500.com/).
// Just enter your guesses one by one and get the possible answers and their number.
public class Word500Bot {
    private static final String PROBABLE_ANSWERS_LIST_URL =
            "https://gist.githubusercontent.com/kcwhite/b367cbb8bcc1c69cecd7a38090cbd9ca/raw/71d78177239e667e65072d94d7b3df74c3232b00/wordle_targets.txt";
    @SuppressWarnings("unused")
    private static final String UNLIKELY_POSSIBILITIES_LIST_URL =
            "https://gist.githubusercontent.com/kcwhite/bb598f1b3017b5477cb818c9b086a5d9/raw/5a0adbbb9830ed93a573cb87a7c14bb5dd0b1883/wordle_possibles.txt";
    private static final int WORDS_LENGTH = 5;
    private static final int LATIN_ALPHABET_SIZE = 26;
    private static final int LATIN_LOWERCASE_OFFSET = 97;
    private static final int LIST_SIZE_THRESHOLD = 20;

    public static void main(String... args) throws Exception {
        var dictionary = new TreeSet<String>();
        try (var s = new Scanner(new URL(PROBABLE_ANSWERS_LIST_URL).openStream())) {
            while (s.hasNext()) dictionary.add(s.next());
        }
        var in = new Scanner(System.in);
        while (true) {
            var guess = in.next(); // each guess contains a word...
            if (guess.length() != WORDS_LENGTH) break;
            int greensGuessed = in.nextInt(), yellowsGuessed = in.nextInt(), redsGuessed = in.nextInt(); // ...and its coloring
            var it = dictionary.iterator();
            while (it.hasNext()) {
                var c = getColoring(it.next(), guess);
                if (c.greens != greensGuessed || c.yellows != yellowsGuessed || c.reds != redsGuessed) it.remove();
            }
            System.out.printf("%d word(s) remaining%n", dictionary.size());
            if (dictionary.size() <= LIST_SIZE_THRESHOLD) System.out.println(dictionary);
            if (dictionary.size() <= 1) break;
        }
    }

    private static Coloring getColoring(String magic, String guess) {
        var magicLetters = new int[LATIN_ALPHABET_SIZE];
        var guessLetters = new int[LATIN_ALPHABET_SIZE];
        int greens = 0, reds = 0;
        for (var i = 0; i < WORDS_LENGTH; i++) {
            if (magic.charAt(i) == guess.charAt(i)) greens++;
            else {
                magicLetters[magic.charAt(i) - LATIN_LOWERCASE_OFFSET]++;
                guessLetters[guess.charAt(i) - LATIN_LOWERCASE_OFFSET]++;
            }
        }
        for (var i = 0; i < LATIN_ALPHABET_SIZE; i++) {
            if (guessLetters[i] > magicLetters[i]) reds += (guessLetters[i] - magicLetters[i]);
        }
        return new Coloring(greens, WORDS_LENGTH - greens - reds, reds);
    }

    private record Coloring(int greens, int yellows, int reds) {
    }
}