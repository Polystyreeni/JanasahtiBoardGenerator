package com.poly.wordgame.boardgenerator;

import java.util.HashSet;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class WordData {
	private static final String filePath = ".\\kotus-sanalista_v1.xml";
    private static HashSet<String> wordSet = new HashSet<String>();

    public static void generateWordList() {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document wordFile = builder.build(filePath);

            Element rootNode = wordFile.getRootElement();

            System.out.println("Rootnode is : " + rootNode.getName());

            List<Element> children = rootNode.getChildren();
            for(var child : children) {
                
                String word = child.getChildren().get(0).getValue();
                if(word.length() >= 3 && word.length() <= 10) {
                    wordSet.add(word);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean containsWord(String text) {
        return wordSet.contains(text);
    }
}
