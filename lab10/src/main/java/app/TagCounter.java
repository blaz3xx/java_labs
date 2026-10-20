package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagCounter {

    private static final Logger LOG = Logger.getLogger(TagCounter.class.getName());
    private static final Pattern TAG = Pattern.compile("<([a-zA-Z][a-zA-Z0-9-]*)");

    public TagStatistics count(String url) throws IOException {
        LOG.log(Level.INFO, "Counting tags on {0}", url);
        String html = download(url);
        html = html.replaceAll("(?s)<!--.*?-->", "");

        HashMap<String, Integer> counts = new HashMap<>();
        Matcher matcher = TAG.matcher(html);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            counts.put(tag, counts.getOrDefault(tag, 0) + 1);
        }
        LOG.log(Level.FINE, "Found {0} different tags", counts.size());
        return new TagStatistics(url, counts);
    }

    private String download(String url) throws IOException {
        LOG.log(Level.FINE, "Downloading {0}", url);
        StringBuilder html = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(URI.create(url).toURL().openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append('\n');
            }
        }
        LOG.log(Level.FINE, "Downloaded {0} characters", html.length());
        return html.toString();
    }
}
