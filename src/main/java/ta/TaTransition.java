package ta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import timedAction.TimedAction;

import java.sql.Time;
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
    private Map<Clock, TimeGuard> timeGuardClockMap;
    private Set<Clock> resetClockSet;


    public TimeGuard getTimeGuard(Clock clock){
        return timeGuardClockMap.get(clock);
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
        for(Map.Entry<Clock, TimeGuard> entry:timeGuardClockMap.entrySet()){
            sb.append(entry.getKey().getName()).append("-").append(entry.getValue()).append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }

    public Map<Clock,TimeGuard> copyTimeGuardClockMap(){
        Map<Clock,TimeGuard> newTimeGuardClockMap = new HashMap<>();
        timeGuardClockMap.keySet().stream().forEach(e->{
            newTimeGuardClockMap.put(e.copy(),timeGuardClockMap.get(e).copy());
        });
        return newTimeGuardClockMap;
    }

    public Set<Clock> copyResetClockSet(){
        Set<Clock> newClockSet= new HashSet<>();
        resetClockSet.stream().forEach(e->{
            newClockSet.add(e.copy());
        });
        return newClockSet;
    }

    public TaTransition copy() {
        return new TaTransitionBuilder()
                .sourceLocation(sourceLocation.copy())
                .targetLocation(targetLocation.copy())
                .symbol(symbol)
                .timeGuardClockMap(copyTimeGuardClockMap())
                .resetClockSet(copyResetClockSet())
                .build();
    }
}
