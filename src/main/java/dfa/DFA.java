package dfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DFA {
    private String name;
    private Set<String> sigma;
    private List<DfaLocation> locations;
    private List<DfaTransition> transitions;

    public int size() {
        return locations.size();
    }

    public boolean containsSymbol(String symbol) {
        return sigma.contains(symbol);
    }

    public List<DfaTransition> getTransitions(String symbol) {
        List<DfaTransition> transitions1 = new ArrayList<>();
        transitions.stream().forEach(e->{
            if (StringUtils.equals(e.getSymbol(),symbol)){
                transitions1.add(e);
            }
        });
        return transitions1;
    }
}
