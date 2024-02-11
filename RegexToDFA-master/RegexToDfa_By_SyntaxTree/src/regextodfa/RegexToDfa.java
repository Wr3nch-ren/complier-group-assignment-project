package regextodfa;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author @ALIREZA_KAY
 */
public class RegexToDfa {

    private static Set<Integer>[] followPos;
    private static Node root;
    private static Set<State> DStates;

    private static Set<String> input; //set of characters is used in input regex

    /**
     * a number is assigned to each characters (even duplicate ones)
     *
     * @param symbNum is a hash map has a key which mentions the number and has
     * a value which mentions the corresponding character or sometimes a string
     * for characters is followed up by backslash like "\*"
     */
    private static HashMap<Integer, String> symbNum;

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        Scanner in = new Scanner(System.in);
        //allocating
        DStates = new HashSet<>();
        input = new HashSet<String>();

        String regex = getRegex(in);
        getSymbols(regex);

        /**
         * giving the regex to SyntaxTree class constructor and creating the
         * syntax tree of the regular expression in it
         */
        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        followPos = st.getFollowPos(); //the followpos of the syntax tree

        /**
         * creating the DFA using the syntax tree were created upside and
         * returning the start state of the resulted DFA
         */
        State q0 = createDFA();
        /*DfaTraversal dfat = new DfaTraversal(q0, input);
        
        String str = getStr(in);
        boolean acc = false;
        for (char c : str.toCharArray()) {
            if (dfat.setCharacter(c)) {
                acc = dfat.traverse();
            } else {
                System.out.println("WRONG CHARACTER!");
                System.exit(0);
            }
        }
        if (acc) {
            System.out.println((char) 27 + "[32m" + "this string is acceptable by the regex!");
        } else {
            System.out.println((char) 27 + "[31m" + "this string is not acceptable by the regex!");
        }
        in.close();*/
    }

    private static String getRegex(Scanner in) {
        System.out.print("Enter a regex: ");
        String regex = in.nextLine();
        return regex+"#";
    }

    private static void getSymbols(String regex) {
        /**
         * op is a set of characters have operational meaning for example '*'
         * could be a closure operator
         */
        Set<Character> op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            /**
             * if a character which is also an operator, is followed up by
             * backslash ('\'), then we should consider it as a normal character
             * and not an operator
             */
            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
        }
    }

    private static State createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(followPos.length)) {
            q0.setAccept();
        }
        DStates.clear();
        DStates.add(q0);

        while (true) {
            boolean exit = true;
            State s = null;
            for (State state : DStates) {
                if (!state.getIsMarked()) {
                    exit = false;
                    s = state;
                }
            }
            if (exit) {
                break;
            }

            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbNum.get(p).equals(a)) {
                        U.addAll(followPos[p - 1]);
                    }
                }
                boolean flag = false;
                State tmp = null;
                for (State state : DStates) {
                    if (state.getName().equals(U)) {
                        tmp = state;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    State q = new State(id++);
                    q.addAllToName(U);
                    if (U.contains(followPos.length)) {
                        q.setAccept();
                    }
                    DStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }
        System.out.println("Dstate: \n");
        String fileName = "output.csv";
        createFile(fileName);
        ArrayList<Integer> IDsortor = new ArrayList<>();
        for(State state : DStates){ 
            if (!state.getName().isEmpty()) {
                IDsortor.add(state.getID());
            }
        }
        Collections.sort(IDsortor);
        int control = 0;
        for(int _id : IDsortor){
            for(State state : DStates){ 
                if (state.getID() == _id) {
                    char stateName = (char) ('1' + control++);
                    state.setStateName(stateName);
                }
            }
        }
        ArrayList<String> forWrite = new ArrayList<>();
        for(int _id : IDsortor){
            for(State state : DStates){ 
                if (state.getID() == _id) {
                    if (!state.getName().isEmpty()) {
                        System.out.println("[NFA] " + state.getName() + " | [DFA] " + state.getStateName() + ": ");
                        for (Map.Entry<String, State> entry : state.getAllMoves().entrySet()) {
                            if (!entry.getKey().equals("#")) {
                                if (entry.getValue().getName().isEmpty()) {
                                    forWrite.add(state.getStateName() + "," + entry.getKey() + "," + "0" + "," + state.getIsAcceptable() + "\n");
                                    System.out.print(state.getStateName() + " -" + entry.getKey() + "-> " + "{empty}" + "\n");
                                }
                                else{
                                    forWrite.add(state.getStateName() + "," + entry.getKey() + "," + entry.getValue().getStateName() + "," + state.getIsAcceptable() + "\n");
                                    System.out.print(state.getStateName() + " -" + entry.getKey() + "-> " + entry.getValue().getStateName() + "\n");
                                }
                            }
                        }
                    }
                    System.out.println("Acceptable?: " + state.getIsAcceptable());
                    System.out.println("------------------------------------------------------");
                }
            }
        }
        writeFile(fileName, forWrite);
        return q0;
    }

    private static String getStr(Scanner in) {
        System.out.print("Enter a string: ");
        String str;
        str = in.nextLine();
        return str;
    }

    private static void createFile(String fileName) {
        try{
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("File output created.");
            }else{
                System.out.println("File already exists.");
                System.out.println("Writing...");
            }
        }catch (IOException e){
            System.out.println("Error.");
        }
    }
    
    private static void writeFile(String fileName, ArrayList<String> texts) {
        try{
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter buffer = new BufferedWriter(writer);
            for (String text : texts) {
                buffer.write(text);
            }
            buffer.write("end");
            buffer.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing");
        }
    }
}
