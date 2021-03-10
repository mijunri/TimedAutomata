package ta.ota;

import timedWord.TimedWord;

import java.util.ArrayList;
import java.util.List;

//只针对OTA
public class LogicTimeWord extends TimedWord<LogicTimedAction> {

    public LogicTimeWord(List<LogicTimedAction> timedActions) {
        super(timedActions);
    }

    public LogicTimeWord concat(LogicTimeWord logicTimeWord){
        List<LogicTimedAction> logicActionList = new ArrayList<>();
        logicActionList.addAll(getTimedActions());
        logicActionList.addAll(logicTimeWord.getTimedActions());
        return new LogicTimeWord(logicActionList);
    }
}
