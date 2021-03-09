package timedWord;

import timedAction.ResetDelayAction;

import java.util.List;

public class ResetDelayTimeWord extends TimedWord<ResetDelayAction>{

    public ResetDelayTimeWord(List<ResetDelayAction> timedActions) {
        super(timedActions);
    }
}
