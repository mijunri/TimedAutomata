package ta.twoClockTA;

import lombok.Data;
import ta.ota.LogicTimeWord;
import ta.ota.LogicTimedAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TwoClockResetLogicTimeWord {

    private List<TwoClockResetLogicAction> timedActions;

    public TwoClockResetLogicTimeWord() {
        timedActions = new ArrayList<>();
    }

    public TwoClockResetLogicTimeWord(List<TwoClockResetLogicAction> timedActions) {
        this.timedActions = timedActions;
    }

    public int size(){
        return timedActions.size();
    }

    public TwoClockResetLogicAction get(int i){
        return timedActions.get(i);
    }

    public Set<TwoClockResetLogicTimeWord> getAllPrefixes() {
        Set<TwoClockResetLogicTimeWord> prefixes = new HashSet<>();
        List<TwoClockResetLogicAction> actionList = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            actionList.add(get(i));
            TwoClockResetLogicTimeWord prefixWord = new TwoClockResetLogicTimeWord(new ArrayList<>(actionList));
            prefixes.add(prefixWord);
        }
        return prefixes;
    }

    public TwoClockResetLogicTimeWord concat(TwoClockResetLogicTimeWord resetLogicTimeWord) {
        List<TwoClockResetLogicAction> resetLogicActionList = new ArrayList<>();
        resetLogicActionList.addAll(getTimedActions());
        resetLogicActionList.addAll(resetLogicTimeWord.getTimedActions());
        return new TwoClockResetLogicTimeWord(resetLogicActionList);
    }

    public TwoClockLogicTimeWord logicTimeWord() {
        List<TwoClockLogicAction> logicTimedActions = new ArrayList<>();
        getTimedActions().stream().forEach(e -> {
            TwoClockLogicAction logicTimedAction = new TwoClockLogicAction(e.getSymbol(),e.getClockValueMap());
            logicTimedActions.add(logicTimedAction);
        });
        return new TwoClockLogicTimeWord(logicTimedActions);
    }

    public TwoClockLogicAction getLastLogicAction() {
        TwoClockResetLogicAction resetAction = getTimedActions().get(size() - 1);
        return new TwoClockLogicAction(resetAction.getSymbol(),resetAction.getClockValueMap());
    }

    public TwoClockResetLogicAction getLastResetAction() {
        return getTimedActions().get(size() - 1);
    }

    public static TwoClockResetLogicTimeWord emptyWord() {
        return new TwoClockResetLogicTimeWord();
    }

    public TwoClockResetLogicTimeWord subWord(int fromIndex, int toIndex) {
        try {
            List<TwoClockResetLogicAction> subList = getTimedActions().subList(fromIndex, toIndex);
            return new TwoClockResetLogicTimeWord(subList);
        } catch (Exception e) {
            return emptyWord();
        }
    }

    public TwoClockResetLogicTimeWord concat(TwoClockResetLogicAction timedAction) {
        List<TwoClockResetLogicAction> resetLogicActionList = new ArrayList<>();
        resetLogicActionList.addAll(getTimedActions());
        resetLogicActionList.add(timedAction);
        return new TwoClockResetLogicTimeWord(resetLogicActionList);
    }


}
