package com.poly.wordgame.boardgenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;


public class BoardData {
    private static final String basePath = ".\\";
    private static HashSet<String> boardStrings = new HashSet<String>();

    public static HashSet<String> getBoardStrings() {
        return boardStrings;
    }

    public static void readBoardStrings(String[] fileNames) {
        try {
            for(String file : fileNames) {
                int count = 0;
                FileReader fileReader = new FileReader(basePath + file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    if (line.contains("<tiles>")) {
                        count++;
                        String boardString = line.replace("<tiles>", "").replace("</tiles>", "").trim();
                        System.out.println(boardString);
                        boardStrings.add(boardString);
                    }
                }

                System.out.println();
                System.out.println("Boards found: " + count);
                System.out.println();
                bufferedReader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
