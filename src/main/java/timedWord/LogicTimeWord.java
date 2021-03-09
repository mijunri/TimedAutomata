package timedWord;

import timedAction.LogicTimedAction;

import java.util.List;

public class LogicTimeWord extends TimedWord<LogicTimedAction>{

    public LogicTimeWord(List<LogicTimedAction> timedActions) {
        super(timedActions);
    }
}
