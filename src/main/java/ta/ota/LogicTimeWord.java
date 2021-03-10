package ta.ota;

import timedWord.TimedWord;

import java.util.List;

//只针对OTA
public class LogicTimeWord extends TimedWord<LogicTimedAction> {

    public LogicTimeWord(List<LogicTimedAction> timedActions) {
        super(timedActions);
    }
}
