import com.poly.wordgame.boardgenerator.Board;
import com.poly.wordgame.boardgenerator.WordData;
// import com.poly.wordgame.boardgenerator.Tile;
import com.poly.wordgame.boardgenerator.BoardData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardGenerator {
  public static void main(String[] args) throws Exception {
    WordData.generateWordList();

    BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Choose Action: (1) Generate board, (2) Rebalance boards: ");
    String actionStr = user.readLine();
    System.out.print("Number of boards to generate: ");
    String boardsToGenerateStr = user.readLine();
    System.out.println();
    System.out.print("Minimum score requirement: ");
    String scoreRequirementStr = user.readLine();
    System.out.print("Contains word with length (0 to ignore): ");
    String wordLenStr = user.readLine();
    System.out.print("Output file name: ");
    String outputFile = user.readLine();

    try {
      int index = Integer.parseInt(actionStr);
      if (index == 2) {
        balanceFiles();
        return;
      }

      // Random board generation - generate 10 boards
      //File boardFile = new File("boards.xml");
      File boardFile = new File(outputFile);

      FileWriter writer = new FileWriter(boardFile, boardFile.isFile());
      BufferedWriter bufferedWriter = new BufferedWriter(writer);
      if(boardFile.length() < 1) {
        System.out.println("Created new File: " + outputFile);
        final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String countString = String.format("%n<count>%d</count>%n", Integer.parseInt(boardsToGenerateStr));

        bufferedWriter.write(xmlHeader);
        bufferedWriter.write(countString);
      }

      else {
        int oldCount = getBoardCount(outputFile);
        replaceCount(outputFile, oldCount, oldCount + Integer.parseInt(boardsToGenerateStr));
      }

      int score = 0;
      int boardIndex = 0;
      int totalGenerateCount = 0;
      int boardsToGenerate = Integer.parseInt(boardsToGenerateStr) - 1;
      int scoreRequirement = Integer.parseInt(scoreRequirementStr);
      int wordRequirement = Integer.parseInt(wordLenStr);
      
      while(boardIndex < boardsToGenerate) {
        while(score < scoreRequirement) {
          totalGenerateCount++;
          Board board;
          if (wordRequirement > 0) {
            board = new Board(wordRequirement);
          }
          else {
            board = new Board();
          }
          
          board.checkBoard();
          score = board.getMaxScore();
          if(score >= scoreRequirement) {
            System.out.println(board.toString());
            String data = board.getXmlString();
            bufferedWriter.write(data);
            board.printBuffer();
            board.clearBuffer();

            boardIndex++;
            if(boardIndex > boardsToGenerate)
              break;
            score = 0;
            System.out.format("Completed: %d / %d%n", boardIndex, boardsToGenerate + 1);
          }
        } 
      }

      bufferedWriter.close();
      System.out.println("Wrote " + boardsToGenerateStr + " boards to file " + outputFile);
      System.out.println("Total boards processed: " + totalGenerateCount);
      System.out.format("Success ratio: %.2f percent", ((double)(boardsToGenerate + 1) / (double)totalGenerateCount) * 100);
    }

    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static int getBoardCount(String file) {
    int boardCount = 0;
    try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
          if(line.contains("<count>")) {
            System.out.println(line);
            int subBegin = line.indexOf("<count>") + "<count>".length();
            int subEnd = line.indexOf("</count>");
            String countStr = line.substring(subBegin, subEnd);
            boardCount = Integer.parseInt(countStr);
            break;
          }
        }

        reader.close();
        return boardCount;
    }

    catch (Exception e) {
      e.printStackTrace();
      return 0;
    } 
  }

  public static void replaceCount(String file, int oldCount, int newCount) {
    try {
      Path path = Paths.get(file);
      Charset charset = StandardCharsets.UTF_8;

      String content = new String(Files.readAllBytes(path), charset);

      String toReplace = String.format("<count>%d</count>", oldCount);
      String replacee = String.format("<count>%d</count>", newCount);

      content = content.replaceAll(toReplace, replacee);
      Files.write(path, content.getBytes(charset));
      System.out.println("Updated board count!");
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void balanceFiles() {
    System.out.println("Balance files");
    try {
      BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Insert filenames to include in rebalancing (separate with comma):");
      String fileStr = user.readLine();

      String[] fileNames = fileStr.split(",");
      BoardData.readBoardStrings(fileNames);

      int boardsPerFile = BoardData.getBoardStrings().size() / fileNames.length;
      System.out.println("Boards per file: " + boardsPerFile);

      ArrayList<String> boardList = new ArrayList<>();
      for(var elem : BoardData.getBoardStrings()) {
        boardList.add(elem);
      }

      Collections.shuffle(boardList);
      for(int i = 0; i < fileNames.length; i++) {
        String newFileName = "balanced-" + fileNames[i];
        File boardFile = new File(newFileName);

        List<String> subList = boardList.subList(i * boardsPerFile, (i + 1) * boardsPerFile);
        System.out.println("Sublist size: " + subList.size());

        FileWriter writer = new FileWriter(boardFile, boardFile.isFile());
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String countString = String.format("%n<count>%d</count>%n", boardsPerFile);

        bufferedWriter.write(xmlHeader);
        bufferedWriter.write(countString);

        for(String boardString : subList) {
          Board board = new Board(boardString);
          board.checkBoard();

          System.out.println(board.toString());
          System.out.println();
          String data = board.getXmlString();
          bufferedWriter.write(data);
          // board.printBuffer();
          board.clearBuffer();
        }

        System.out.println("Generated board file: " + newFileName);
        bufferedWriter.close();
      }

      System.out.println("Generated balanced files");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
