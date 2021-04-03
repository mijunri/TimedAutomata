package ta.twoClockTA;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ta.Clock;
import ta.ota.LogicTimedAction;
import timedAction.TimedAction;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoClockResetLogicAction {
    private String symbol;
    private Set<Clock> resetClockSet;
    private Map<Clock, Double> clockValueMap;

    public boolean isReset(Clock clock){
        return resetClockSet.contains(clock);
    }

    public double getValue(Clock clock) {
        return clockValueMap.get(clock);
    }
}
