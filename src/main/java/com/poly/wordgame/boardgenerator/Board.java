package com.poly.wordgame.boardgenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Board
{
    private final int boardSize = 16;
    private final int boardSide = (int)Math.round(Math.sqrt(boardSize));

    private final String[] charList = {"a", "b", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
        "r", "s", "t", "u", "v", "y", "ä", "ö"};    // Need these in string format to support ä and ö
    private final HashMap<Integer, Integer> scoreList = new HashMap<>() {{
        put(3, 1);
        put(4, 3);
        put(5, 7);
        put(6, 12);   // Non-confirmed
        put(7, 21);   // Non-confirmed
        put(8, 31);
        put(9, 42);   // Non-confirmed
        put(10, 57);

    }};

    private List<Tile> tiles = new ArrayList<>();

    // Board score
    private List<String> boardWords = new ArrayList<String>();
    private int maxScore;

    private StringBuilder stringBuffer = new StringBuilder();

    // Default constructor - Generate board randomly
    public Board() {
        Random rand = new Random();
        for(int i = 0; i < boardSize; i++) {
            int n = rand.nextInt(charList.length);
            //System.out.println("random number: " + n);
            String c = charList[n];
            
            int x = i % boardSide;
            int y = i / boardSide;

            Tile tile = new Tile(x, y, c);
            tiles.add(tile);
        }
    }

    // Load board from given set of tiles
    public Board(List<Tile> tiles) {
        this.tiles = tiles;
    }

    // Load Board from predefined xml file
    public Board(String filePath, boolean loadWords) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document wordFile = builder.build(filePath);
            Element rootNode = wordFile.getRootElement();

            String tileString = rootNode.getChildText("tiles");

            for(int i = 0; i < tileString.length(); i++) {
                int x = i % boardSide;
                int y = i / boardSide;
                String c = Character.toString(tileString.charAt(i));

                Tile tile = new Tile(x, y, c);
                tiles.add(tile);
            }

            if(loadWords) {
                // Load max score
                String scoreString = rootNode.getChildText("score");
                maxScore = Integer.parseInt(scoreString);

                // Load words
                Element wordRoot = rootNode.getChild("words");
                for(Element child : wordRoot.getChildren()) {
                    String word = child.getAttributeValue("word");
                    boardWords.add(word);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate board by forcing a word with wordLen 
    public Board (int wordLen) {
        String word = WordData.getWordWithLen(wordLen);
        WordAdder wordAdder = new WordAdder();
        wordAdder.initialize();

        ArrayList<Integer> path = new ArrayList<>(wordAdder.tiles.subList(0, wordLen));

        // System.out.println("SubList:");
        // path.stream().forEach(System.out::println);

        Random rand = new Random(System.currentTimeMillis());
        
        for (int i = 0; i < boardSize; i++) {
            int x = i % boardSide;
            int y = i / boardSide;
            if (path.contains(i)) {
                int index = path.indexOf(i);
                String s = Character.toString(word.charAt(index));
                Tile tile = new Tile(x, y, s);
                tiles.add(tile);
            }
            else {
                int n = rand.nextInt(charList.length);
                String c = charList[n]; 
                Tile tile = new Tile(x, y, c);
                tiles.add(tile);
            }
        }
        stringBuffer.append("Created board that contains word: ").append(word).append(System.lineSeparator());
    }

    public Board (String boardString) {
        for(int i = 0; i < boardString.length(); i++) {
            int x = i % boardSide;
            int y = i / boardSide;
            String c = Character.toString(boardString.charAt(i));

            Tile tile = new Tile(x, y, c);
            tiles.add(tile);
        }
    }

    public void checkBoard() {
        for(int i = 0; i <  boardSize; i++) {
            List<Tile> selected = new ArrayList<>();
            selected.add(tiles.get(i));
            walkTile(i, selected);

            // Reset occupation after check
            for(Tile tile : tiles) {
                tile.setOccupied(false);
            }
        }

        printToBuffer("Max score for the board is: " + maxScore);
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void saveBoard(int saveIndex) {
        String savePath = "board" + saveIndex + ".xml";

        StringBuilder tileSb = new StringBuilder();
        for(Tile tile : tiles) {
            tileSb.append(tile.getCharacter());
        }

        String tileString = tileSb.toString();

        Document document = new Document();
        document.setRootElement(new Element("board"));

        Element tileElement = new Element("tiles").setText(tileString);
        document.getRootElement().addContent(tileElement);

        Element scoreElement = new Element("score").setText(Integer.toString(maxScore));
        document.getRootElement().addContent(scoreElement);

        Element wordElement = new Element("words");
        for(String word : boardWords) {
            wordElement.addContent(new Element("word").setText(word));
        }

        document.getRootElement().addContent(wordElement);

        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(document, new FileOutputStream(savePath));
        }

        catch (IOException e) {
            System.out.println("Saving board failed: ");
            e.printStackTrace();
        }
    }

    public String getXmlString() {
        String data = createSaveString();
        return data;
    }

    private String createSaveString() {
        StringBuilder tileSb = new StringBuilder();
        for(Tile tile : tiles) {
            tileSb.append(tile.getCharacter());
        }

        String tileString = tileSb.toString();

        Document document = new Document();
        document.setRootElement(new Element("board"));

        Element tileElement = new Element("tiles").setText(tileString);
        document.getRootElement().addContent(tileElement);

        Element scoreElement = new Element("score").setText(Integer.toString(maxScore));
        document.getRootElement().addContent(scoreElement);

        Element wordElement = new Element("words");
        for(String word : boardWords) {
            wordElement.addContent(new Element("word").setText(word));
        }

        document.getRootElement().addContent(wordElement);

        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());

        String output = xmlOutputter.outputString(document);
        final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        output = output.replace(xmlHeader, "");
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < boardSize; i++) {
            if(i % boardSide == 0 && i > 0)
            sb.append(System.lineSeparator());
            if(tiles.get(i).isOccupied()) {
                sb.append("-");
            }
                
            else {
                sb.append(tiles.get(i).getCharacter());
            }
            
            if(i % boardSide != boardSide - 1)
                sb.append(" ");
        }

        return sb.toString();
    }

    private void walkTile(int tileIndex, List<Tile> selected) {

        tiles.get(tileIndex).setOccupied(true);

        // Check if currently selected path has generated a word
        checkSelected(selected);

        // Maximum word length reached
        if(selected.size() > 10) {
            tiles.get(tileIndex).setOccupied(false);
            return;
        }
        
        // Check neighbours
        List<Integer> neighbours = getNeighbours(tileIndex);

        // End walk, if no neighbours are not found
        if(neighbours.size() < 1) {
            tiles.get(tileIndex).setOccupied(false);
            return;
        }

        for(int i = 0; i < neighbours.size(); i++) {
            ArrayList<Tile> selectedCopy = new ArrayList<Tile>(selected);
            Tile newCurrent = tiles.get(neighbours.get(i));
            selectedCopy.add(newCurrent);
            walkTile(neighbours.get(i), selectedCopy);
            tiles.get(neighbours.get(i)).setOccupied(false);
        }
    }

    private void checkSelected(List<Tile> selected) {
        if(selected.size() < 3)
            return;

        StringBuilder sb = new StringBuilder();
        for(Tile tile : selected) {
            sb.append(tile.getCharacter());
        }

        String text = sb.toString();
        if(WordData.containsWord(text)) {
            if(!boardWords.contains(text)) {
                boardWords.add(text);
                addWordToScore(text);
                printToBuffer("Board contains word: " + text);
                printToBuffer(this.toString());
            }  
        }     
    }

    private List<Integer> getNeighbours(int tileIndex) {

        List<Integer> neighbours = new ArrayList<>();

        // Neighbours are next to tile or diagonally next to tile
        int[] neighbourIndexes = {tileIndex - 1, tileIndex + 1, tileIndex - boardSide, tileIndex + boardSide,
        tileIndex - boardSide - 1, tileIndex - boardSide + 1, tileIndex + boardSide - 1, tileIndex + boardSide + 1};

        for(int i = 0; i < neighbourIndexes.length; i++) {
            if(neighbourIndexes[i] >= 0 && neighbourIndexes[i] < tiles.size()) {
                if(!tiles.get(neighbourIndexes[i]).isOccupied() 
                    && Math.abs(tiles.get(tileIndex).getX() - tiles.get(neighbourIndexes[i]).getX()) <= 1
                    && Math.abs(tiles.get(tileIndex).getY() - tiles.get(neighbourIndexes[i]).getY()) <= 1) {
                    neighbours.add(neighbourIndexes[i]);
                }
            }
        }

        return neighbours;
    }

    private void addWordToScore(String word) {
        int len = word.length();
        int score = scoreList.get(len);
        maxScore += score;
    }

    private void printToBuffer(String str) {
        stringBuffer.append(str);
        stringBuffer.append(System.lineSeparator());
    }

    public void printBuffer() {
        System.out.println(stringBuffer.toString());
    }

    public void clearBuffer() {
        stringBuffer.setLength(0);
    }

    
}