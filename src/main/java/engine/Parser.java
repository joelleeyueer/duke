package engine;

import cat.Nala;
import storage.Storage;
import task.Deadline;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static formatting.Helper.separator;
import static formatting.Helper.userInput;
import static formatting.Helper.FORMATTER;
import static storage.Storage.validateStringFilenameUsingIO;


public class Parser {
    static ArrayList<String> remainingTokens;

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

    public static void parsePrint() throws Exception {
        Storage s = Storage.getInstance();
        System.out.println("Meow! I will be printing the following list:");
        parseShowlist();
        System.out.println("I will be saving the .txt file under Desktop/dukeFile, if it is not created already.\n" + "What are we naming the output file?");
        while (true){
            String userFilename = userInput();
            s.setFileName(userFilename);
            if (userFilename.equalsIgnoreCase("/e")){
                break;
            }

            //validateStringFilenameUsingIO cannot catch / and \
            else if (userFilename.contains("/") || userFilename.contains("\\")){
                System.out.println("You tried to create a file with restricted characters! Please try again, or type \"/e\" to go back to the main menu." );
                continue;
            }

            try{
                validateStringFilenameUsingIO(s.getFileName());
                if (!s.checkExistence()) {
                    s.createFile();
                    s.populateFile();
                    break;
                }
                else{
                    System.out.println(userFilename + ".txt already exists on " + System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "dukeFile" + "\n Please type a different filename, or type \"/e\" to go back to the main menu!" );
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                continue;
            }
        }
    }

    public static void parseMark(){
        Parser p = getInstance();
        TaskList t = TaskList.getInstance();
        //mark something as done. e.g. mark 5
        //delete the "mark"
        next();

        //parse the index to changeToMarkAsDone
        try{
            String indexString = p.front();
            int index = Integer.parseInt(indexString);
            t.changeToMarkAsDone(index);
        } catch (NumberFormatException e) {
            System.out.println("Meow! " + p.front() + " is not a valid integer.");
            t.showTodoList();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Meow! " + p.front() + " is not in the list. "+ p.front() +" is not marked as done.");
            t.showTodoList();
        }
    }

    public static void parseUnmark() {
        Parser p = getInstance();
        TaskList t = TaskList.getInstance();
        //mark something as not done. e.g. unmark 5
        next();
        //parse the index to changeToMarkNotDone
        try{
            String indexString = p.front();
            int index = Integer.parseInt(indexString);
            t.changeToMarkNotDone(index);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Meow! " + p.front() + " is not a valid integer.");
            t.showTodoList();
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println("Meow! " + p.front() + " is not in the list. "+ p.front() +" is not marked as undone.");
            t.showTodoList();
        }
    }

    public static void parseBye() {
        Parser p = getInstance();
        separator();
        System.out.println("Goodbye! Hope to see you again soon!");
        separator();
        p.clear();
    }

    public static void parseShowlist() {
        TaskList t = TaskList.getInstance();
        next();
        t.showTodoList();
    }

    public static void parseDelete() {
        Parser p = getInstance();
        TaskList t = TaskList.getInstance();
        //delete a task. e.g. delete 5
        next();
        //parse the index to deleteTask
        try{
            String indexString = p.front();
            int index = Integer.parseInt(indexString);
            t.deleteTask(index);
        } catch (NumberFormatException e) {
            System.out.println("Meow! " + p.front() + " is not a valid integer.");
            t.showTodoList();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Meow! " + p.front() + " is not in the list.");
            t.showTodoList();
        }
    }

    public static void parseAddTask() throws Exception {
        Parser p = getInstance();
        TaskList t = TaskList.getInstance();
        String incomingTaskName;
        String incomingType;

        switch (p.front().toLowerCase()){
            case ("todo"):
                if (p.getTokenContains("/at")){
                    Nala.nalaSyntaxError("Event");
                    break;
                }
                else if (p.getTokenContains("/by")){
                    Nala.nalaSyntaxError("Deadline");
                    break;
                }
                else{
                    next();
                    incomingTaskName = p.tokenToString();
                    if (incomingTaskName.isBlank()){
                        Nala.nalaSyntaxError("BlankTodo");
                        break;
                    }
                }
                t.addNewTask(incomingTaskName);
                p.clear();
                break;

            case ("deadline"):
                String incomingDate;
                if (p.getTokenContains("/at")){
                    Nala.nalaSyntaxError("Event");
                    break;
                }
                else if (p.getTokenContains("/by")){
                    incomingType = p.front();
                    next();
                    StringBuilder TaskNameString= new StringBuilder();
                    if (!p.front().equalsIgnoreCase("/by")){
                        TaskNameString.append(p.front());
                        next();
                        while (!p.front().equalsIgnoreCase("/by")){
                            TaskNameString.append(" ").append(p.front());
                            next();
                        }
                    }
                    p.expect("/by");
                    incomingDate = p.tokenToString();
                    incomingTaskName = TaskNameString.toString();
                    if (incomingTaskName.isBlank()){
                        Nala.nalaSyntaxError("BlankDeadline");
                        break;
                    }
                    if (incomingDate.isBlank()){
                        Nala.nalaSyntaxError("NoDate");
                        break;
                    }
                }
                else{ //no /by
                    Nala.nalaSyntaxError("DeadlineNoBy");
                    break;
                }
                t.addNewTask(incomingTaskName, incomingType, parseDate(incomingDate));
                p.clear();
                break;

            case  ("event"):
                String incomingStartDate;
                String incomingEndDate;
                if (p.getTokenContains("/by")){
                    Nala.nalaSyntaxError("Deadline");
                    break;
                }
                else if (p.getTokenContains("/at")){
                    incomingType = p.front();
                    next();
                    StringBuilder TaskNameString= new StringBuilder();
                    if (!p.front().equalsIgnoreCase("/at")){
                        TaskNameString.append(p.front());
                        next();
                        while (!p.front().equalsIgnoreCase("/at")){
                            TaskNameString.append(" ").append(p.front());
                            next();
                        }
                    }
                    p.expect("/at");
                    StringBuilder DateString = new StringBuilder();
                    DateString.append(p.front());
                    next();
                    DateString.append(" ").append(p.front());
                    next();
                    incomingStartDate = DateString.toString();

                    p.expect("to");
                    StringBuilder DateString2 = new StringBuilder();
                    DateString2.append(p.front());
                    next();
                    DateString2.append(" ").append(p.front());
                    next();
                    incomingEndDate = DateString2.toString();

                    incomingTaskName = TaskNameString.toString();
                    if (incomingTaskName.isBlank()){
                        Nala.nalaSyntaxError("BlankEvent");
                        break;
                    }
                    if (incomingStartDate.isBlank() || incomingEndDate.isBlank()){
                        Nala.nalaSyntaxError("NoDate");
                        break;
                    }
                }
                else{ //no /at
                    Nala.nalaSyntaxError("EventNoAt");
                    break;
                }
                LocalDateTime startDate = parseDate(incomingStartDate);
                LocalDateTime endDate = parseDate(incomingEndDate);
                validateTwoDates(startDate,endDate);
                t.addNewTask(incomingTaskName, incomingType, startDate, endDate);
                p.clear();
                break;
            default:
                Nala.nalaConfused();

        }
    }

    public static LocalDateTime parseDate(String dateString) throws Exception {
        LocalDateTime incomingDateTime = LocalDateTime.parse(dateString, FORMATTER);
        incomingDateTime.format(FORMATTER);

        if (incomingDateTime.isBefore(LocalDateTime.now())) {
            throw new Exception("Meow :( Date or time has already passed. The current date and time is " + LocalDateTime.now().format(FORMATTER));
        }

        return incomingDateTime;
    }

    public static void validateTwoDates( LocalDateTime startDate, LocalDateTime endDate) throws Exception {

        if (startDate.isAfter(endDate)) throw new Exception("Meow :( Start Date/Time cannot be after End Date/Time.");
        else if (startDate.isEqual(endDate)) throw new Exception("Meow :( Start Date/Time cannot be the same as End Date/Time.");
    }

    public static void parseFind() {
        Parser p = getInstance();
        TaskList t = TaskList.getInstance();
        next();
        String keyword = p.front();
        next();
        if (remainingTokens.isEmpty()){
            t.showFilteredTodoList(keyword);
        } else {
            System.out.println("Meow! The keyword should only consist of 1 word.");
            return;
        }
    }

    public static void parseSnooze() {
        Parser p = Parser.getInstance();
        TaskList t = TaskList.getInstance();
        int taskIndex;
        int snoozeValue;
        String snoozeType;
        try {
            taskIndex = Integer.parseInt(p.front());
            if (!t.get(taskIndex).getClass().equals(Deadline.class)){
                System.out.println("Meow! " + taskIndex + " is not a Deadline.");
                t.showTodoList();
                return;
            }
            next();
        } catch (NumberFormatException e) {
            System.out.println("Meow! You did not type a number (taskIndex)");
            return;
        }
        try {
            snoozeValue = Integer.parseInt(p.front());
            next();
        } catch (NumberFormatException e) {
            System.out.println("Meow! You did not type a number (snoozeValue)");
            return;
        }
        next();
        try {
            snoozeType = p.front();
            next();
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public boolean getTokenContains(String token) {
        return remainingTokens.contains(token);
    }

    public void stringToToken(String incomingText) {
        String[] strSplit = incomingText.split(" ");
        remainingTokens = new ArrayList<>(Arrays.asList(strSplit));
    }

    public String tokenToString(){
        return String.join(" ", remainingTokens);
    }

    public String front() { return remainingTokens.get(0); }
    public static void next(){
        remainingTokens.remove(0);
    }

    public void clear(){
        remainingTokens.clear();
    }

    public void expect(String symbol) {
        try{
            if (match(symbol)){
                next();
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