package ta.ota;

import lombok.AllArgsConstructor;
import lombok.Data;
import timedAction.TimedAction;

@Data
@AllArgsConstructor
public class LogicTimedAction extends TimedAction {

    public LogicTimedAction(String symbol, Double value) {
        super(symbol, value);
    }
}
