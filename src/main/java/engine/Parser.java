package engine;

import java.util.ArrayList;
import java.util.Arrays;


public class Parser {
    public String tokenToString;
    ArrayList<String> remainingTokens;

    private Parser() {
        remainingTokens = new ArrayList<>();
    }
    private static Parser instance = null;

    public static Parser getInstance(){
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    public boolean getTokenContains(String token) {
        return remainingTokens.contains(token);
    }

    public void stringToToken(String incomingText) {
        String[] strSplit = incomingText.split(" ");
        remainingTokens = new ArrayList<String>(Arrays.asList(strSplit));
    }

    public String tokenToString(){
        return String.join(" ", remainingTokens);
    }

    public String front() { return remainingTokens.get(0); }
    public void next(){
        remainingTokens.remove(0);
    }

    public void clear(){
        remainingTokens.clear();
    }

    public void expect(String symbol) {
        try{
            if (match(symbol)){
                next();
                return;
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Invalid syntax. You typed: " + front() + ". Expected: " + symbol + ".");
        }
    }

    public boolean match(String symbol){
        return symbol.equalsIgnoreCase(remainingTokens.get(0));
    }
}
