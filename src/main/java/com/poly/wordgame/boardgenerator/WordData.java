package com.poly.wordgame.boardgenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    public static String getWordWithLen(int len) {
        List<String> wordsWithLen = wordSet.stream()
            .filter(t -> t.length() == len)
            .filter(t -> !t.contains("-"))
            .collect(Collectors.toList());
        System.out.println("Filtered wordlist contains " + wordsWithLen.size() + " words");
        Random random = new Random(System.currentTimeMillis());
        return wordsWithLen.get(random.nextInt(wordsWithLen.size()));
    }
}
