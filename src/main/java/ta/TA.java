package ta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TA {
    private String name;
    private Set<Clock> clockSet;
    private Set<String> sigma;
    private List<TaLocation> locations;
    private List<TaTransition> transitions;


    public TaLocation getInitLocation() {
        for (TaLocation l : locations) {
            if (l.isInit()) {
                return l;
            }
        }
        return null;
    }

    public List<TaLocation> getAcceptedLocations() {
        List<TaLocation> list = new ArrayList<>();
        for (TaLocation l : locations) {
            if (l.isAccept()) {
                list.add(l);
            }
        }
        return list;
    }

    public List<TaTransition> getTransitions(TaLocation fromLocation, String symbol, TaLocation toLocation) {
        List<TaTransition> list = new ArrayList<>(transitions);
        if (fromLocation != null) {
            Iterator<TaTransition> iterator = list.iterator();
            while (iterator.hasNext()) {
                TaTransition t = iterator.next();
                if (fromLocation != t.getSourceLocation()) {
                    iterator.remove();
                }
            }
        }

        if (symbol != null) {
            Iterator<TaTransition> iterator = list.iterator();
            while (iterator.hasNext()) {
                TaTransition t = iterator.next();
                if (!StringUtils.equals(t.getSymbol(), symbol)) {
                    iterator.remove();
                }
            }
        }

        if (toLocation != null) {
            Iterator<TaTransition> iterator = list.iterator();
            while (iterator.hasNext()) {
                TaTransition t = iterator.next();
                if (t.getTargetLocation() != toLocation) {
                    iterator.remove();
                }
            }
        }
        return list;
    }

    public boolean containsSymbol(String symbol) {
        return sigma.contains(symbol);
    }


    public Set<Clock> copyClockSet() {
        Set<Clock> newClockSet = new HashSet<>();
        clockSet.stream().forEach(e -> {
            newClockSet.add(e.copy());
        });
        return newClockSet;
    }

    public List<TaLocation> copyLocations() {
        List<TaLocation> newLocations = new ArrayList<>();
        locations.stream().forEach(e -> {
            newLocations.add(e.copy());
        });
        return newLocations;
    }

    public List<TaTransition> copyTransitions() {
        List<TaTransition> newTransitions = new ArrayList<>();
        transitions.stream().forEach(e -> {
            newTransitions.add(e.copy());
        });
        return newTransitions;
    }

    public Set<String> copySigma() {
        Set<String> newSigma = new HashSet<>();
        newSigma.addAll(sigma);
        return newSigma;
    }


    //TODO 深克隆一个TA，不会污染数据,注意clockSet和transition的关系
    public TA copy() {
        return this;
//        return new TABuilder()
//                .name(name)
//                .locations(copyLocations())
//                .transitions(copyTransitions())
//
//                .sigma(copySigma())
//                .build();
    }




//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\n\t").append("\"sigma\":[");
//        for (String symbol : getSigma()) {
//            sb.append("\"" + symbol + "\",");
//        }
//        sb.deleteCharAt(sb.length() - 1).append("],\n\t").append("\"init\":");
//        int init = getInitLocation().getId();
//        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
//        sb.append("\"s\":[");
//        for (Location l : getLocationList()) {
//            sb.append(l.getId()).append(",");
//        }
//        sb.deleteCharAt(sb.length() - 1).append("]\n\t\"tran\":{\n");
//
////        OTABuilder.sortTaTran(getTransitionList());
//        for (int i = 0; i < getTransitionList().size(); i++) {
//            TaTransition t = getTransitionList().get(i);
//            sb.append("\t\t\"").append(i).append(t.toString()).append(",\n");
//        }
//        sb.deleteCharAt(sb.length() - 2);
//        sb.append("\t},\n\t").append("\"accpted\":[");
//        for (Location l : getAcceptedLocations()) {
//            sb.append(l.getId()).append(",");
//        }
//        sb.deleteCharAt(sb.length() - 1).append("]\n}");
//        return sb.toString();
//    }
}
