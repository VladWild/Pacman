package com.vladwild.game.records;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.List;

public class PreferencesRecords {
    private static Preferences prefs = Gdx.app.getPreferences("PacmanGameVladWild");
    private static String[] SCORE = {"Score1", "Score2", "Score3", "Score4", "Score5",
                                     "Score6", "Score7", "Score8", "Score9", "Score10"};
    private static String[] LEVEL = {"Level1", "Level2", "Level3", "Level4", "Level5",
                                     "Level6", "Level7", "Level8", "Level9", "Level10"};
    public static final int MAX_NUMBER_RECORDS_IN_GAME = 10;

    public static void write(Record record) {
        if (record.getScore() > prefs.getInteger(SCORE[MAX_NUMBER_RECORDS_IN_GAME - 1])) {
            if (record.getScore() > prefs.getInteger(SCORE[0])) {
                for (int i = MAX_NUMBER_RECORDS_IN_GAME; i > 1; i--){
                    prefs.putInteger(SCORE[i - 1], prefs.getInteger(SCORE[i - 2]));
                    prefs.putInteger(LEVEL[i - 1], prefs.getInteger(LEVEL[i - 2]));
                    prefs.flush();
                }
                prefs.putInteger(SCORE[0], record.getScore());
                prefs.putInteger(LEVEL[0], record.getLevel());
                prefs.flush();
            } else {
                for (int i = MAX_NUMBER_RECORDS_IN_GAME; i > 0; i--) {
                    if (record.getScore() > prefs.getInteger(SCORE[i - 1]) &&
                            record.getScore() <= prefs.getInteger(SCORE[i - 2])) {
                        for (int j = MAX_NUMBER_RECORDS_IN_GAME; j > i; j--){
                            prefs.putInteger(SCORE[j - 1], prefs.getInteger(SCORE[j - 2]));
                            prefs.putInteger(LEVEL[j - 1], prefs.getInteger(LEVEL[j - 2]));
                            prefs.flush();
                        }
                        prefs.putInteger(SCORE[i - 1], record.getScore());
                        prefs.putInteger(LEVEL[i - 1], record.getLevel());
                        prefs.flush();
                    }
                }
            }
        }
    }

    public static List<Record> read(){
        List<Record> records = new ArrayList<Record>();
        for (int i = 0; i < MAX_NUMBER_RECORDS_IN_GAME; i++) {
            records.add(new Record(prefs.getInteger(SCORE[i]), prefs.getInteger(LEVEL[i])));
        }
        return records;
    }

    public static Record getHighRecord(){
        return new Record(prefs.getInteger(SCORE[0]), prefs.getInteger(LEVEL[0]));
    }

    public static String toStringOut() {
        String str = "";
        for (int i = 0; i < MAX_NUMBER_RECORDS_IN_GAME; i++){
            str += String.valueOf(i + 1) + ": " + String.valueOf(prefs.getInteger(SCORE[i])) +
                    " - " + String.valueOf(prefs.getInteger(LEVEL[i])) + "\n";
        }
        return str;
    }

}
