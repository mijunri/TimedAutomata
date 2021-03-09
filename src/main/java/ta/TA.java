package ta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
                if (!StringUtils.equals(t.getSymbol(),symbol)) {
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


//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\n\t").append("\"sigma\":[");
//        for (String action : getSigma()) {
//            sb.append("\"" + action + "\",");
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
