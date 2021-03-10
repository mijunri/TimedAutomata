package ta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Map<TimeGuard, Clock> timeGuardClockMap;
    private Set<Clock> resetClockSet;


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(symbol).append(",");
        for(Map.Entry<TimeGuard,Clock> entry:timeGuardClockMap.entrySet()){
            sb.append(entry.getKey()).append("-").append(entry.getValue().getName()).append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }

    public Map<TimeGuard, Clock> copyTimeGuardClockMap(){
        Map<TimeGuard, Clock> newTimeGuardClockMap = new HashMap<>();
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
