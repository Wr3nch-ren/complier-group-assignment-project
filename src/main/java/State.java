import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class State {
    
    private int ID;
    private char stateName;
    private Set<Integer> name;
    private HashMap<String, State> move;
    
    private boolean IsAcceptable;
    private boolean IsMarked;
    
    public State(int ID){
        this.ID = ID;
        move = new HashMap<>();
        name = new HashSet<>();
        IsAcceptable = false;
        IsMarked = false;
    }
    
    public void addMove(String symbol, State s){
        move.put(symbol, s);
    }

    public void addAllToName(Set<Integer> number){
        name.addAll(number);
    }
    
    public void setIsMarked(boolean bool){
        IsMarked = bool;
    }
    
    public boolean getIsMarked(){
        return IsMarked;
    }
    
    public Set<Integer> getName(){
        return name;
    }

    public void setAccept() {
        IsAcceptable = true;
    }
    
    public boolean getIsAcceptable(){
        return  IsAcceptable;
    }
    
    public HashMap<String, State> getAllMoves(){
        return move;
    }

    public void setStateName(char stateName){
        this.stateName = stateName;
    }

    public char getStateName(){
        return stateName;
    }
    public int getID(){
        return ID;
    }

    
    
}
