package dbm;

import lombok.AllArgsConstructor;
import lombok.Data;
import ta.TimeGuard;

@Data
@AllArgsConstructor
public class ActionGuard {
    private String symbol;
    private TimeGuard timeGuard;

    public Double getLowerValue(){
        if (timeGuard.isLowerBoundClose()){
            return timeGuard.getLowerBound()*1.0;
        }
        return timeGuard.getLowerBound()+0.5;
    }
}
