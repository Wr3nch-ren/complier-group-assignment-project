import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RegexToDfa {

    private static Set<Integer>[] followPos;
    private static Node root;
    private static Set<State> DStates;

    private static Set<String> input;

    private static HashMap<Integer, String> symbolNum;

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        Scanner in = new Scanner(System.in);
        DStates = new HashSet<>();
        input = new HashSet<String>();

        String regex = getRegex(in);
        getSymbols(regex);

        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot();
        followPos = st.getFollowPos();

        createDFA();
    }

    private static String getRegex(Scanner in) {
        System.out.print("Enter a regex: ");
        String regex = in.nextLine();
        return regex+"#";
    }

    private static void getSymbols(String regex) {
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        Set<Character> op = new HashSet<>(Arrays.asList(ch));

        input = new HashSet<>();
        symbolNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbolNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbolNum.put(num++, "" + charAt);
            }
        }
    }

    private static void createDFA() {
        int id = 0;
        Set<Integer> firstPos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstPos_n0);
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
            s.setIsMarked(true);
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbolNum.get(p).equals(a)) {
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
        ArrayList<Integer> idSorter = new ArrayList<>();
        for(State state : DStates){ 
            if (!state.getName().isEmpty()) {
                idSorter.add(state.getID());
            }
        }
        Collections.sort(idSorter);
        int control = 0;
        for(int _id : idSorter){
            for(State state : DStates){ 
                if (state.getID() == _id) {
                    char stateName = (char) ('1' + control++);
                    state.setStateName(stateName);
                }
            }
        }
        ArrayList<String> forWrite = new ArrayList<>();
        for(int _id : idSorter){
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
