package models;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Favorites {
    private List<String> engFavList = new ArrayList<>();
    private List<String> vieFavList = new ArrayList<>();
    private static Favorites instance = null;

    public static Favorites getInstance() {
        if (instance == null) {
            instance = new Favorites();
        }
        return instance;
    }

    private Favorites() {
    }

    private Favorites(List<String> engFavList, List<String> vieFavList) {
        this.engFavList = engFavList;
        this.vieFavList = vieFavList;
    }

    public List<String> getEngFavList() {
        return engFavList;
    }

    public void setEngFavList(List<String> engFavList) {
        this.engFavList = engFavList;
    }

    public void setVieFavList(List<String> vieFavList) {
        this.vieFavList = vieFavList;
    }

    public List<String> getVieFavList() {
        return vieFavList;
    }

    public void parseFavoriteList(String filepath) throws IOException {
        Path path = Path.of(filepath);
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        JsonReader jsonReader = new JsonReader(new FileReader(filepath));
        Gson gson = new Gson();
        Favorites favorites = gson.fromJson(jsonReader, Favorites.class);
        if (favorites == null) {
            favorites = new Favorites();
        }
        instance = favorites;
    }

    public void saveFavoriteList(Favorites favorites, String filepath) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(favorites);
        PrintWriter printWriter = new PrintWriter(new FileWriter(filepath));
        printWriter.write(jsonString);
        printWriter.close();
    }
}
