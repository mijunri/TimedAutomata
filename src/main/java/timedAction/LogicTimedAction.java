package timedAction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogicTimedAction extends TimedAction{

    public LogicTimedAction(String symbol, Double value) {
        super(symbol, value);
    }
}
