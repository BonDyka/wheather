package loc.abondarev.weather;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * Trying parse website with jsoup.
 */
/**
 * @author abondarev.
 * @since 24.10.2017.
 */
public class Parser {

    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private int iteratedLine;

    public static void main(String[] args) throws Exception {
        String lineSeparator = System.getProperty("line.separator");
        Parser parser = new Parser();
        Document page = parser.getPage();
        Element tableWth = page.select("table[class=wt]").first();// get main table with weather info
        Elements names = tableWth.select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");
        int startIndex = 0;
        for (Element elem : names) {
            String date = parser.getDateFromString(elem.select("th[id=dt]").text());
            System.out.printf("%s    Явление    Температура    Давление    Влажность    Ветер%s", date, lineSeparator);
            parser.printPartValues(values, startIndex);
            startIndex += parser.getIteratedLine();
        }

    }

    private int getIteratedLine() {
        return this.iteratedLine;
    }

    private Document getPage() throws IOException {
        //String url = "http://www.pogodka.com/wthr/50/RSXX0069/hourly.html";
        String url = "http://www.pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);//download page from pointer url

        return page;
    }

    private String getDateFromString(String text) throws Exception {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't extract date from string");
    }

    private void printPartValues(Elements values, int index) {
        iteratedLine = 0;
        int iterBound = 4;
        if (index == 0) {
            String dayPart = values.get(3).select("td").text();
            if (dayPart.contains(DayParts.MORNING)) {
                iterBound = 3;
            }
            if (dayPart.contains(DayParts.AFTERNOON)) {
                iterBound = 2;
            }
            if (dayPart.contains(DayParts.EVENING)) {
                iterBound = 1;
            }
        }
        for (int i = 0; i < iterBound; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.printf("%s    ", td.text());
            }
            System.out.println();
            iteratedLine++;
        }
    }

    private static class DayParts {
        static final String MORNING = "Утро";
        static final String AFTERNOON = "День";
        static final String EVENING = "Вечер";
        static final String NIGHT = "Ночь";
    }

}
