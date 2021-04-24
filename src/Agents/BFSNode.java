package Agents;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;

public class BFSNode {
    private String sentence;//with deleted word
    private ArrayList<BFSNode> children;
    private ArrayList<String> wordsRemoved;

    public BFSNode(String sentence){
        this.sentence = sentence;
        this.children = new ArrayList<>();
        this.wordsRemoved = new ArrayList<>();
    }

    public String getSentence() {
        return sentence;
    }

    public ArrayList<BFSNode> getChildren() {
        return children;
    }

    public ArrayList<String> getWordsRemoved() {
        return wordsRemoved;
    }
}
