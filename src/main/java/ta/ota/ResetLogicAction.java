package ta.ota;


import lombok.Getter;
import timedAction.ResetTimedAction;

public class ResetLogicAction extends ResetTimedAction {

    @Getter
    private boolean invalid;

    public ResetLogicAction(String symbol, Double value, boolean reset) {
        super(symbol, value, reset);
    }

    public ResetLogicAction(String symbol, Double value, boolean reset, boolean invalid) {
        super(symbol, value, reset);
        this.invalid = invalid;
    }
    public LogicTimedAction logicTimedAction(){
        return new LogicTimedAction(getSymbol(),getValue());
    }


}
