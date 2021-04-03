package ta.twoClockTA;

import lombok.Data;
import ta.*;
import ta.ota.*;

import java.util.*;

/**
 * TwoClockTA 是TA的一个子集，具有两个时钟
 */
@Data
public class TwoClockTA extends TA {

    public TwoClockTA(String name, List<Clock> clockList, Set<String> sigma, List<TaLocation> locations, List<TaTransition> transitions) {
        super(name, clockList, sigma, locations, transitions);
    }

    //给定一个重置逻辑时间字，TwoCLockTA最多一个路径
    public TaLocation reach(TwoClockResetLogicTimeWord resetLogicTimeWord) {
        Clock clock1 = getClockList().get(0);
        Clock clock2 = getClockList().get(1);
        TaLocation location = getInitLocation();
        //存储当前时钟值,初始化0
        double value = 0.0;
        Map<Clock, Double> clockDoubleMap = new HashMap<>();
        clockDoubleMap.put(clock1, 0d);
        clockDoubleMap.put(clock2, 0d);

        List<TwoClockResetLogicAction> actions = resetLogicTimeWord.getTimedActions();

        flag:
        for (TwoClockResetLogicAction action : actions) {
            List<TaTransition> transitions = getTransitions(location, action.getSymbol(), null);
            for (TaTransition transition : transitions) {
                if (transition.isReset(clock1) != action.isReset(clock1)) {
                    continue;
                }
                if (transition.isReset(clock2) != action.isReset(clock2)) {
                    continue;
                }
                TimeGuard timeGuard1 = transition.getTimeGuard(clock1);
                TimeGuard timeGuard2 = transition.getTimeGuard(clock2);
                if (timeGuard1.isPass(action.getValue(clock1))
                        && timeGuard2.isPass(action.getValue(clock2))) {
                    location = transition.getTargetLocation();
                    continue flag;
                }
            }
            return null;
        }
        return location;
    }

    //给定一个逻辑时间字，DOTA最多一个路径
    public TaLocation reach(TwoClockLogicTimeWord logicTimeWord) {

        TaLocation location = getInitLocation();
        Clock clock1 = getClockList().get(0);
        Clock clock2 = getClockList().get(1);

        //存储当前时钟值,初始化0
        double value = 0.0;
        Map<Clock, Double> clockDoubleMap = new HashMap<>();
        clockDoubleMap.put(clock1, 0.0);
        clockDoubleMap.put(clock2, 0.0);

        List<TwoClockLogicAction> actions = logicTimeWord.getTimedActions();

        flag:
        for (TwoClockLogicAction action : actions) {
            List<TaTransition> transitions = getTransitions(location, action.getSymbol(), null);
            for (TaTransition transition : transitions) {
                TimeGuard timeGuard1 = transition.getTimeGuard(clock1);
                TimeGuard timeGuard2 = transition.getTimeGuard(clock2);
                if (timeGuard1.isPass(action.getValue(clock1))
                        && timeGuard2.isPass(action.getValue(clock2))) {
                    location = transition.getTargetLocation();
                    continue flag;
                }
            }
            return null;
        }
        return location;
    }

    public TwoClockResetLogicTimeWord transferReset(TwoClockLogicTimeWord logicTimeWord) {
        TaLocation location = getInitLocation();
        Clock clock1 = getClockList().get(0);
        Clock clock2 = getClockList().get(1);

        //存储当前时钟值,初始化0
        double value = 0.0;
        Map<Clock, Double> clockDoubleMap = new HashMap<>();
        clockDoubleMap.put(clock1, 0.0);
        clockDoubleMap.put(clock2, 0.0);

        List<TwoClockLogicAction> actions = logicTimeWord.getTimedActions();
        List<TwoClockResetLogicAction> resetActions = new ArrayList<>();
        boolean end = false;
        flag:
        for (TwoClockLogicAction action : actions) {
            if (!end) {
                List<TaTransition> transitions = getTransitions(location, action.getSymbol(), null);
                for (TaTransition transition : transitions) {
                    TimeGuard timeGuard1 = transition.getTimeGuard(clock1);
                    TimeGuard timeGuard2 = transition.getTimeGuard(clock2);

                    if (timeGuard1.isPass(action.getValue(clock1))
                            && timeGuard2.isPass(action.getValue(clock2))) {
                        location = transition.getTargetLocation();
                        Set<Clock> clockResetSet = new HashSet<>();
                        clockResetSet.addAll(transition.getResetClockSet());
                        TwoClockResetLogicAction resetLogicAction = new TwoClockResetLogicAction(
                                action.getSymbol(), clockResetSet, action.getClockValueMap());
                        resetActions.add(resetLogicAction);
                        continue flag;
                    }
                }
                end = true;
            }
            if (end) {
                Set<Clock> clockResetSet = new HashSet<>();
                clockResetSet.add(clock1);
                clockResetSet.add(clock2);
                TwoClockResetLogicAction resetLogicAction = new TwoClockResetLogicAction(
                        action.getSymbol(), clockResetSet, action.getClockValueMap());
                resetActions.add(resetLogicAction);
            }
        }
        return new TwoClockResetLogicTimeWord(resetActions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t").append("\"sigma\":[");
        for (String action : getSigma()) {
            sb.append("\"" + action + "\",");
        }
        sb.deleteCharAt(sb.length() - 1).append("],\n\t").append("\"init\":");
        String init = getInitLocation().getId() + "";
        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
        sb.append("\"s\":[");
        for (TaLocation l : getLocations()) {
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append("]\n\t\"tran\":{\n");

        getTransitions().sort(new OTATranComparator(clock));
        for (int i = 0; i < getTransitions().size(); i++) {
            TaTransition t = getTransitions().get(i);
            String reset = t.getResetClockSet().contains(clock) ? "r" : "n";
            sb.append("\t\t\"").append(i).append("\":[")
                    .append(t.getSourceId()).append(",")
                    .append("\"").append(t.getSymbol()).append("\",")
                    .append("\"").append(t.getTimeGuard(clock)).append("\",")
                    .append(t.getTargetId()).append(", ").append(reset).append("]").append(",\n");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("\t},\n\t").append("\"accpted\":[");
        for (TaLocation l : getAcceptedLocations()) {
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append("]\n}");
        return sb.toString();
    }


}
