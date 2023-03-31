package models;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchHistory {
    private static SearchHistory instance = null;
    private List<History> histories;

    public List<History> getHistories() {
        return histories;
    }

    private SearchHistory(){
        histories = new ArrayList<>();
    }

    public static SearchHistory getInstance(){
        if(instance == null){
            instance = new SearchHistory();
        }
        return instance;
    }

    public void parseJson(String filepath) throws IOException {
        Path path = Path.of(filepath);
        if(Files.notExists(path)){
            Files.createFile(path);
        }
        JsonReader jsonReader = new JsonReader(new FileReader(filepath));
        Gson gson = new Gson();
        SearchHistory histories = gson.fromJson(jsonReader, SearchHistory.class);
        if(histories == null){
            histories = new SearchHistory();
        }
        instance = histories;
    }

    public TreeMap<String, Integer> getHistoryStatistics(Date from, Date to) throws ParseException {
        TreeMap<String, Integer> count = new TreeMap<>();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        from = formatter.parse(formatter.format(from));
        to = formatter.parse(formatter.format(to));
        for (History item : histories) {
            String word = item.getWord();
            Date date = formatter.parse(formatter.format(item.getDate()));
            if ((date.equals(from) || date.equals(to)) || (date.after(from) && date.before(to))) {
                count.putIfAbsent(word, 0);
                count.replace(word, count.get(word) + 1);
            }
        }
        return count;
    }

    public void saveHistory(String filepath) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(instance);
        PrintWriter printWriter = new PrintWriter(new FileWriter(filepath));
        printWriter.write(jsonString);
        printWriter.close();
    }

    public void addHistory(History history){
        histories.add(history);
    }
}
