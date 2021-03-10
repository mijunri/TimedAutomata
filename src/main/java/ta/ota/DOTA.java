package ta.ota;

import ta.*;

import java.util.*;

/**
 * DOTA是TA的一个子集，它只有一个时钟，且是确定性的
 */
public class DOTA extends TA {

    private Clock clock;

    public DOTA(String name, Set<String> sigma, List<TaLocation> locations, List<TaTransition> transitions, Clock clock) {
        super(name, Collections.emptySet(), sigma, locations, transitions);
        getClockSet().add(clock);
        this.clock = clock;
    }

    //给定一个重置逻辑时间字，DOTA最多一个路径
    public TaLocation reach(ResetLogicTimeWord resetLogicTimeWord) {

        TaLocation location = getInitLocation();

        //存储当前时钟值,初始化0
        double value = 0.0;
        Map<Clock, Double> clockDoubleMap = new HashMap<>();
        clockDoubleMap.put(clock,0.0);

        List<ResetLogicAction> actions = resetLogicTimeWord.getTimedActions();

        flag:
        for (ResetLogicAction action : actions) {
            List<TaTransition> transitions = getTransitions(location, action.getSymbol(), null);
            for (TaTransition transition: transitions){
                TimeGuard timeGuard = transition.getTimeGuard(clock);
                if (timeGuard.isPass(action.getValue())){
                    location = transition.getTargetLocation();
                    continue flag;
                }
            }
            return null;
        }
        return location;
    }
}
