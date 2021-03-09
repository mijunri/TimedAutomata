package ta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
