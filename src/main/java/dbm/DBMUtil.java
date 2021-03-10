package dbm;


import ta.TimeGuard;

import java.util.ArrayList;
import java.util.List;

public final class DBMUtil {

    public static List<ActionGuard> transfer(List<TransitionState> states){
        //初始化区间范围
        List<ActionGuard> actionGuards = new ArrayList<>();

        for(TransitionState state : states){
            String action = state.getSymbol();
            Value lower = state.getDbm().getMatrix()[0][1];
            Value upper = state.getDbm().getMatrix()[1][0];
            TimeGuard guard = new TimeGuard(!lower.isEqual(), !upper.isEqual(),
                    lower.getValue(),upper.getValue());
            ActionGuard actionGuard = new ActionGuard(action, guard);
            actionGuards.add(actionGuard);
        }

        return actionGuards;
    }
}
