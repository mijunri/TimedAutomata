package ta.twoClockTA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ta.Clock;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoClockLogicAction {
    private String symbol;
    private Map<Clock, Double> clockValueMap;

    public double getValue(Clock clock1) {
        return clockValueMap.get(clock1);
    }
}
