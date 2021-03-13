package ta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaTransition {
    private TaLocation sourceLocation;
    private TaLocation targetLocation;
    private String symbol;
    private Map<Clock, TimeGuard> clockTimeGuardMap;
    private Set<Clock> resetClockSet;


    public TimeGuard getTimeGuard(Clock clock){
        return clockTimeGuardMap.get(clock);
    }


    public boolean isReset(Clock clock){
        return resetClockSet.contains(clock);
    }

    public boolean isPass(String symbol, Map<Clock, Double> clockValueMap){
        if(StringUtils.equals(symbol, this.symbol)){
            for(Clock clock: clockValueMap.keySet()){
                double value = clockValueMap.get(clock);
                TimeGuard timeGuard = getTimeGuard(clock);
                if (!timeGuard.isPass(value)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(symbol).append(",");
        for(Map.Entry<Clock, TimeGuard> entry: clockTimeGuardMap.entrySet()){
            sb.append(entry.getKey().getName()).append("-").append(entry.getValue()).append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }

    public String getSourceId() {
        return sourceLocation.getId();
    }

    public String getTargetId(){
        return targetLocation.getId();
    }

    public int getLowerBound(Clock clock) {
        return getTimeGuard(clock).getLowerBound();
    }

    public int getUpperBound(Clock clock) {
        return getTimeGuard(clock).getUpperBound();
    }
}
