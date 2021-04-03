package ta.twoClockTA;

import lombok.Data;
import ta.ota.LogicTimeWord;
import ta.ota.LogicTimedAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TwoClockLogicTimeWord {
    
    private List<TwoClockLogicAction> timedActions;

    public TwoClockLogicTimeWord() {
        timedActions = new ArrayList<>();
    }

    public TwoClockLogicTimeWord(List<TwoClockLogicAction> timedActions) {
        this.timedActions = timedActions;
    }
    
    public int size(){
        return timedActions.size();
    }

    public TwoClockLogicAction get(int i){
        return timedActions.get(i);
    }
    
    public Set<TwoClockLogicTimeWord> getAllPrefixes() {
        Set<TwoClockLogicTimeWord> prefixes = new HashSet<>();
        List<TwoClockLogicAction> actionList = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            actionList.add(get(i));
            TwoClockLogicTimeWord prefixWord = new TwoClockLogicTimeWord(new ArrayList<>(actionList));
            prefixes.add(prefixWord);
        }
        return prefixes;
    }

    public TwoClockLogicTimeWord concat(TwoClockLogicTimeWord resetLogicTimeWord) {
        List<TwoClockLogicAction> resetLogicActionList = new ArrayList<>();
        resetLogicActionList.addAll(getTimedActions());
        resetLogicActionList.addAll(resetLogicTimeWord.getTimedActions());
        return new TwoClockLogicTimeWord(resetLogicActionList);
    }


    public TwoClockLogicAction getLastLogicAction() {
        TwoClockLogicAction logicAction = getTimedActions().get(size() - 1);
        return logicAction;
    }

    public static TwoClockLogicTimeWord emptyWord() {
        return new TwoClockLogicTimeWord();
    }


    public TwoClockLogicTimeWord subWord(int fromIndex, int toIndex) {
        try {
            List<TwoClockLogicAction> subList = getTimedActions().subList(fromIndex, toIndex);
            return new TwoClockLogicTimeWord(subList);
        } catch (Exception e) {
            return emptyWord();
        }
    }

    public TwoClockLogicTimeWord concat(TwoClockLogicAction timedAction) {
        List<TwoClockLogicAction> resetLogicActionList = new ArrayList<>();
        resetLogicActionList.addAll(getTimedActions());
        resetLogicActionList.add(timedAction);
        return new TwoClockLogicTimeWord(resetLogicActionList);
    }


}
