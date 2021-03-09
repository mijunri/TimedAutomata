package timedAction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DelayTimedAction extends TimedAction{

    public DelayTimedAction(String symbol, Double value) {
        super(symbol, value);
    }
}
