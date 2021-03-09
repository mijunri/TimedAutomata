package timedWord;

import lombok.AllArgsConstructor;
import lombok.Data;
import timedAction.DelayTimedAction;

import java.util.ArrayList;
import java.util.List;

public class DelayTimeWord extends TimedWord<DelayTimedAction>{

    public DelayTimeWord(List<DelayTimedAction> timedActions) {
        super(timedActions);
    }

}
