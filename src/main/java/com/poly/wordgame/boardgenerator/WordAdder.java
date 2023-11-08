package com.poly.wordgame.boardgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class WordAdder {
    public ArrayList<Integer> tiles = new ArrayList<Integer>();
    public int wordLen = 6;
    public final int tileCount = 16;

    public int startIndex;

    public void initialize() {
      Random random = new Random();
      startIndex = random.nextInt(tileCount);

      tiles.add(startIndex);
      populateTiles(tiles);
    }

    private void populateTiles(ArrayList<Integer> tiles) {
      if (tiles.size() >= tileCount)
        return;
      
      var neighbours = getValidNeighbours(tiles);
      if (neighbours.size() == 0) {
        return;
      }
        
      for (int i = 0; i < neighbours.size(); i++) {
        tiles.add(neighbours.get(i));
        populateTiles(tiles);
        if (tiles.size() >= tileCount)
          return;
        tiles.remove(neighbours.get(i));
      }
    }
  
    private ArrayList<Integer> getValidNeighbours(ArrayList<Integer> activeTiles) {
      int startIndex = activeTiles.get(activeTiles.size() - 1);
      ArrayList<Integer> neighbours = new ArrayList<>(Arrays.asList(new Integer[]{startIndex - 5, startIndex - 4, startIndex - 3, startIndex - 1, startIndex + 1, startIndex + 3, startIndex + 4, startIndex + 5}));
      Collections.shuffle(neighbours);

      ArrayList<Integer> validNeighbours = new ArrayList<>();
      for(int i = 0; i < neighbours.size(); i++) {
        if (activeTiles.contains(neighbours.get(i)))
          continue;
        if (neighbours.get(i) < 0)
          continue;
        if (neighbours.get(i) > 15)
          continue;
        
        int startX = startIndex % 4;
        int startY = startIndex / 4 + startIndex % 4;

        int neighbourX = neighbours.get(i) % 4;
        int neighbourY = neighbours.get(i) / 4 + startIndex % 4;

        if (Math.abs(startX - neighbourX) > 1 || Math.abs(startY - neighbourY) > 1)
          continue;

        validNeighbours.add(neighbours.get(i));
      }
  
      return validNeighbours;
    }
}
