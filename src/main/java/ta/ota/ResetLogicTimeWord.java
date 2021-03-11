package ta.ota;

import timedAction.ResetTimedAction;
import timedWord.TimedWord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//只针对OTA
public class ResetLogicTimeWord extends TimedWord<ResetLogicAction> {

    public ResetLogicTimeWord(List<ResetLogicAction> timedActions) {
        super(timedActions);
    }

    public Set<ResetLogicTimeWord> getAllPrefixes() {
        Set<ResetLogicTimeWord> prefixes = new HashSet<>();
        List<ResetLogicAction> actionList = new ArrayList<>();
        for(int i = 0; i < size(); i ++){
            actionList.add(get(i));
            ResetLogicTimeWord prefixWord = new ResetLogicTimeWord(new ArrayList<>(actionList));
            prefixes.add(prefixWord);
        }
        return prefixes;
    }

    public ResetLogicTimeWord concat(ResetLogicTimeWord resetLogicTimeWord){
        List<ResetLogicAction> resetLogicActionList = new ArrayList<>();
        resetLogicActionList.addAll(getTimedActions());
        resetLogicActionList.addAll(resetLogicTimeWord.getTimedActions());
        return new ResetLogicTimeWord(resetLogicActionList);
    }

    public LogicTimeWord logicTimeWord(){
        List<LogicTimedAction> logicTimedActions = new ArrayList<>();
        getTimedActions().stream().forEach(e->{
            LogicTimedAction logicTimedAction = new LogicTimedAction(e.getSymbol(),e.getValue());
            logicTimedActions.add(logicTimedAction);
        });
        return new LogicTimeWord(logicTimedActions);
    }

    public LogicTimedAction getLastLogicAction(){
        ResetLogicAction resetAction = getTimedActions().get(size()-1);
        return new LogicTimedAction(resetAction.getSymbol(),resetAction.getValue());
    }

    public ResetLogicAction getLastResetAction(){
        return getTimedActions().get(size()-1);
    }

}
