package net.jeedup.coding

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by zack on 5/28/14.
 */
class CSV {

    /*
     * This Pattern will match on either quoted text or text between commas, including
     * whitespace, and accounting for beginning and end of line.
     */
    private final static Pattern pattern = Pattern.compile('\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)')

    public static List<Map<String, String>> decode(List<String> header, String csv) {
        List<Map<String, Object>> result = []

        String[] lines = csv.split('\r\n')
        for (String line : lines) {
            String[] fields = parse(line)
            Map<String, Object> obj = [:]
            int i = 0;
            for (String h : header) {
                obj[h] = fields[i]
                i++
            }
            result << obj
        }

        return result
    }

    public static String[] parse(String csvLine) {
        List<String> allMatches = []
        Matcher matcher = pattern.matcher(csvLine)
        allMatches.clear()
        String match
        while (matcher.find()) {
            match = matcher.group(1)
            if (match!=null) {
                allMatches.add(match)
            }
            else {
                allMatches.add(matcher.group(2))
            }
        }

        int size = allMatches.size()
        if (size > 0) {
            return allMatches.toArray(new String[size])
        }
        else {
            return [0]
        }
    }
}
