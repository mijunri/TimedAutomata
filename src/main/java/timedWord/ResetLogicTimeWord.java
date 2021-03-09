package timedWord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timedAction.ResetLogicAction;

import java.util.ArrayList;
import java.util.List;

public class ResetLogicTimeWord extends TimedWord<ResetLogicAction>{

    public ResetLogicTimeWord(List<ResetLogicAction> timedActions) {
        super(timedActions);
    }
}
