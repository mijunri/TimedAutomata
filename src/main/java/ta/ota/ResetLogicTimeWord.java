package ta.ota;

import timedWord.TimedWord;

import java.util.List;

//只针对OTA
public class ResetLogicTimeWord extends TimedWord<ResetLogicAction> {

    public ResetLogicTimeWord(List<ResetLogicAction> timedActions) {
        super(timedActions);
    }
}
