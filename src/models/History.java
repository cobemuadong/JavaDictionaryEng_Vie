package models;

import java.time.LocalDate;
import java.util.Date;

public class History {
    private String word;
    private Date date;

    public String getWord() {
        return word;
    }

    public Date getDate() {
        return date;
    }

    public History(String word, Date date) {
        this.word = word;
        this.date = date;
    }
}
