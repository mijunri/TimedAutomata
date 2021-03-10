package timedWord;

import lombok.AllArgsConstructor;
import lombok.Data;
import timedAction.TimedAction;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TimedWord<T extends TimedAction> {

    private List<T> timedActions;

    public int size(){
        return timedActions.size();
    }

    public T get(int i){
        return timedActions.get(i);
    }

    public boolean isEmpty(){
        return timedActions.isEmpty();
    }

    public static TimedWord emptyWord(){
        return new TimedWord(new ArrayList<>());
    }

    public TimedWord subWord(int fromIndex, int toIndex){
        try{
            List<T> subList = timedActions.subList(fromIndex,toIndex);
            return new TimedWord(subList);
        }catch (Exception e){
            return emptyWord();
        }
    }

    @Override
    public String toString(){
        if (isEmpty()){
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        for (TimedAction timedAction : timedActions){
            sb.append(timedAction);
        }
        return sb.toString();
    }

}
