import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;
import components.utilities.Reporter;

/**
 * Program to test static methods {@code generateElements} and
 * {@code nextWordOrSeparator}.
 *
 * @author Put your name here
 *
 */
public final class TagCloudGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * Compare {@code Map.Pair<String, Integer>}s in greater than order for
     * value.
     */
    private static class PairValueLT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o2.value().compareTo(o1.value());
        }
    }

    /**
     * Compare {@code Map.Pair<String, Integer>}s in lexicographic order for
     * key.
     */
    private static class PairKeyLT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o1.key().compareTo(o2.key());
        }
    }

    /**
     * Gets one line at a time from {@code in} until end of input, and puts them
     * into the queue {@code lines}.
     *
     * @param in
     *            the source of the lines to be input
     * @param lines
     *            the queue of lines that are read
     * @updates in
     * @replaces lines
     * @requires in.is_open
     * @ensures <pre>
     * in.is_open  and
     * in.ext_name = #in.ext_name  and
     * in.content = ""  and
     * lines = STRING_OF_LINES(#in.content)
     * </pre>
     */
    private static void getLinesFromInput(SimpleReader in,
            Queue<String> lines) {
        assert in != null : "Violation of: in is not null";
        assert lines != null : "Violation of: lines is not null";
        assert in.isOpen() : "Violation of: in.is_open";

        lines.clear();
        while (!in.atEOS()) {
            String str = in.nextLine();
            lines.enqueue(str);
        }
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        Set<Character> temp = charSet.newInstance();
        for (int i = 0; i < str.length(); i++) {
            char element = str.charAt(i);
            if (!temp.contains(element)) {
                temp.add(element);
            }
        }
        charSet.transferFrom(temp);

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String x = "";
        int i = position;
        boolean findSeparatorOrWord = false;
        int length = text.length();
        if (separators.contains(text.charAt(position))) {
            while (i < length && !findSeparatorOrWord) {
                findSeparatorOrWord = !separators.contains(text.charAt(i));
                i++;
            }
        } else {
            while (i < length && !findSeparatorOrWord) {
                findSeparatorOrWord = separators.contains(text.charAt(i));
                i++;
            }
        }

        if (findSeparatorOrWord) {
            x = text.substring(position, i - 1);
        } else {
            x = text.substring(position, i);
        }

        return x;
    }

    /**
     * Outputs the header of HTML file.
     *
     * @param out
     *            the out stream to the HTML file
     * @param inFileName
     *            the HTML file name
     * @param n
     *            number of words to be output
     */
    private static void outputHeader(SimpleWriter out, String inFileName,
            int n) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Top " + n + " words in " + inFileName + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web"
                        + "-sw2/assignments/projects/tag-cloud-generator/data/"
                        + "tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Top " + n + " words in " + inFileName + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");
    }

    /**
     * Outputs the footer of HTML file.
     *
     * @param out
     *            the out stream to the HTML file
     */
    private static void outputFooter(SimpleWriter out) {
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Outputs a HTML file with a queue.
     *
     * @param out
     *            the out stream to the HTML file
     * @param sp2
     *            sorting machine of map pairs
     * @param largestCount
     *            most counts of a word
     * @param minCount
     *            minimum counts of a word
     * @requires out.isOpen()
     */
    private static void outputBody(SimpleWriter out,
            SortingMachine<Map.Pair<String, Integer>> sp2, int largestCount,
            int minCount) {
        final int maxFont = 48;
        final int minFont = 11;
        for (Map.Pair<String, Integer> x : sp2) {
            int size = (maxFont - minFont) * (x.value() - minCount)
                    / (largestCount - minCount) + minFont;
            out.println("<span style=\"cursor:default\" class=\"f" + size
                    + "\" title=\"count: " + x.value() + "\">" + x.key()
                    + "</span>");
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Define separator characters for test
         */
        final String separatorStr = " \t\n\r,-.!?[]';:/()*\"";
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorStr, separatorSet);
        /*
         * Open input and output streams
         */
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        /*
         * Get input file name and open input stream
         */
        out.println("Enter an input file name: ");
        String inFileName = in.nextLine();
        final int four = 4;
        Reporter.assertElseFatalError(
                inFileName.substring(inFileName.length() - four).equals(".txt"),
                "Invalid input file name.");
        SimpleReader inFile = new SimpleReader1L(inFileName);
        out.println("Enter an output file name: ");
        String outFileName = in.nextLine();
        final int five = 5;
        Reporter.assertElseFatalError(outFileName
                .substring(outFileName.length() - five).equals(".html"),
                "Invalid output file name.");
        SimpleWriter outFile = new SimpleWriter1L(outFileName);
        out.println(
                "Enter the number of words to be included in the generated tag cloud: ");
        int n = in.nextInteger();
        outputHeader(outFile, inFileName, n);

        Queue<String> q = new Queue1L<String>();
        Queue<String> allWords = new Queue1L<String>();
        getLinesFromInput(inFile, q);

        //add all words to a queue
        while (q.length() > 0) {
            String line = q.dequeue();
            int position = 0;
            while (position < line.length()) {
                String token = nextWordOrSeparator(line, position,
                        separatorSet);
                if (!separatorSet.contains(token.charAt(0))) {
                    allWords.enqueue(token);
                }
                position += token.length();
            }
        }

        //add words and counts to map
        Map<String, Integer> wordCount = new Map1L<>();
        int length = allWords.length();
        for (int i = 0; i < length; i++) {
            String element = allWords.dequeue();

            if (!wordCount.hasKey(element)) {
                wordCount.add(element, 1);
            } else {
                Map.Pair<String, Integer> x = wordCount.remove(element);
                wordCount.add(element, x.value() + 1);
            }
        }

        Comparator<Map.Pair<String, Integer>> ci = new PairValueLT();
        SortingMachine<Map.Pair<String, Integer>> sp = new SortingMachine1L<>(
                ci);

        //change all words to lower case
        Map<String, Integer> temp = wordCount.newInstance();
        while (wordCount.size() > 0) {
            Map.Pair<String, Integer> element = wordCount.removeAny();
            String key = element.key().toLowerCase();
            if (temp.hasKey(key)) {
                Map.Pair<String, Integer> x = temp.remove(key);
                int counts = x.value() + element.value();
                temp.add(key, counts);
            } else {
                temp.add(key, element.value());
            }
        }
        wordCount.transferFrom(temp);

        //add pairs to sorting machine
        while (wordCount.size() > 0) {
            Map.Pair<String, Integer> element = wordCount.removeAny();
            sp.add(element);
        }

        //sort by value
        sp.changeToExtractionMode();

        //add pairs to new map
        Map<String, Integer> topN = wordCount.newInstance();
        int largestCount = 0;
        int minCount = 0;
        for (int i = 0; i < n; i++) {
            Map.Pair<String, Integer> x = sp.removeFirst();
            if (i == 0) {
                largestCount = x.value();
            }
            if (i == n - 1) {
                minCount = x.value();
            }
            topN.add(x.key(), x.value());
        }

        Comparator<Map.Pair<String, Integer>> cs = new PairKeyLT();
        SortingMachine<Map.Pair<String, Integer>> sp2 = new SortingMachine1L<>(
                cs);

        //add pairs to sorting machine
        Map<String, Integer> topNWords = topN.newInstance();
        while (topN.size() > 0) {
            Map.Pair<String, Integer> element = topN.removeAny();
            topNWords.add(element.key(), element.value());
            sp2.add(element);
        }

        //sort by key
        sp2.changeToExtractionMode();

        outputBody(outFile, sp2, largestCount, minCount);

        outputFooter(outFile);

        /*
         * Close input and output streams
         */
        in.close();
        out.close();
        inFile.close();
        outFile.close();
    }
}
