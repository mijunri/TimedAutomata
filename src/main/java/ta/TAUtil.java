package ta;

import dbm.BaseState;
import dbm.DBM;
import dbm.LocationState;
import dbm.TransitionState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public final class TAUtil {

    //时间自动机的可达性算法，判断当前自动机是否存在可达的接收状态
    public static List<TransitionState> reachable(TA ta) {
        //获取时钟数组
        List<Clock> clockList = new ArrayList<>(ta.getClockSet());
        //初始化DBM
        DBM initDbm = DBM.init(clockList);
        //获取初始节点
        TaLocation initLocation = ta.getInitLocation();
        //创建已访问的状态空间
        Set<LocationState> visited = new HashSet<>();
        //创建待访问的状态队列
        LinkedList<LocationState> waitQueue = new LinkedList<>();
        //初始状态
        LocationState initLocationState = new LocationState(initDbm, initLocation);
        //将初始状态加入到待访问队列中,这里的waitQueue中的都是以访问状态，他们的后续节点状态是未访问的
        waitQueue.offer(initLocationState);
        //遍历待访问队列，这里采用了BSF的方式
        while (!waitQueue.isEmpty()) {
            //取出一个状态
            LocationState current = waitQueue.poll();
            //将状态加入到已访问集合中
            visited.add(current);
            //获取当前状态的节点
            TaLocation location = current.getLocation();
            //判断，当前节点是否是不接收状态
            //如果是接收状态,返回状态路径
            if (location.isAccept()) {
                List<TransitionState> transitionStates = new LinkedList<>();
                BaseState state = current;
                while (null != state){
                    if (state instanceof TransitionState){
                        transitionStates.add(0, (TransitionState) state);
                    }
                    state = state.getPreState();
                }
            }
            //否则，获取当前节点的后续迁移
            List<TaTransition> taTransitions = ta.getTransitions(location, null, null);
            //对迁移进行遍历
            flag:
            for (TaTransition t : taTransitions) {
                //获取该迁移的时钟约束
                Map<Clock, TimeGuard> timeGuardClockMap = t.getClockTimeGuardMap();
                //对当前DBM进行拷贝
                DBM dbm = current.getDbm().copy();
                //求约束后的DBM
                for (Map.Entry<Clock, TimeGuard> entry : timeGuardClockMap.entrySet()) {
                    dbm.and(entry.getKey(), entry.getValue());
                }
                //最小化
                dbm.canonical();
                //判断与约束是否合理
                if (dbm.isConsistent()) {
                    //迁移状态
                    TransitionState transitionState = new TransitionState(dbm, t.getSymbol(), current);
                    dbm = dbm.copy();
                    for (Clock c : t.getResetClockSet()) {
                        dbm.reset(c);
                    }
                    dbm.canonical();
                    dbm.up();
                    LocationState newLocationState = new LocationState(dbm, t.getTargetLocation());
                    newLocationState.setPreState(transitionState);
                    for (LocationState n : visited) {
                        if (n.include(newLocationState)) {
                            continue flag;
                        }
                    }
                    visited.add(newLocationState);
                    waitQueue.offer(newLocationState);
                }
            }
        }
        return null;
    }

    //时间自动机的平行组合
    public static TA parallelCombination(TA ta1, TA ta2) {

        //存放新旧节点对应关系的map
        Map<Pair, TaLocation> pairLocationMap = new HashMap<>();

        //构造节点的笛卡尔积
        List<TaLocation> newLocations = new ArrayList<>();
        for (TaLocation l1 : ta1.getLocations()) {
            for (TaLocation l2 : ta2.getLocations()) {
                TaLocation newLocation = new TaLocation.TaLocationBuilder()
                        .id(l1.getId() + "_" + l2.getId())
                        .name(l1.getName() + "_" + l2.getName())
                        .accept(l1.isAccept() && l2.isAccept())
                        .init(l1.isInit() && l2.isInit())
                        .build();
                Pair pair = new Pair(l1, l2);
                pairLocationMap.put(pair, newLocation);
                newLocations.add(newLocation);
            }
        }

        //sigma求并集
        Set<String> sigma = new HashSet<>();
        sigma.addAll(ta1.getSigma());
        sigma.addAll(ta2.getSigma());

        //时钟求并集
        Set<Clock> clocks = new HashSet<>();
        clocks.addAll(ta1.getClockSet());
        clocks.addAll(ta2.getClockSet());

        //构造迁移的笛卡尔积
        //遍历sigma，分三种情况求迁移
        List<TaTransition> newTransitions = new ArrayList<>();
        sigma.stream().forEach(e -> {
            //第一种情况，两边都含有相同的动作,需要对其进行同步操作
            if (ta1.containsSymbol(e) && ta2.containsSymbol(e)) {
                for (TaTransition t1 : ta1.getTransitions(null, e, null)) {
                    for (TaTransition t2 : ta2.getTransitions(null, e, null)) {
                        Pair sourcePair = new Pair(t1.getSourceLocation(), t2.getSourceLocation());
                        Pair targetPair = new Pair(t1.getTargetLocation(), t2.getTargetLocation());
                        TaLocation sourceLocation = pairLocationMap.get(sourcePair);
                        TaLocation targetLocation = pairLocationMap.get(targetPair);
                        //时钟约束求并集
                        Map<Clock, TimeGuard> timeGuardClockMap = new HashMap<>();
                        timeGuardClockMap.putAll(t1.getClockTimeGuardMap());
                        timeGuardClockMap.putAll(t2.getClockTimeGuardMap());
                        //重置时钟集合求并集
                        Set<Clock> resetClocks = new HashSet<>();
                        resetClocks.addAll(t1.getResetClockSet());
                        resetClocks.addAll(t2.getResetClockSet());
                        //构建新的迁移
                        TaTransition newTransition = new TaTransition.TaTransitionBuilder()
                                .sourceLocation(sourceLocation)
                                .targetLocation(targetLocation)
                                .symbol(t1.getSymbol())
                                .clockTimeGuardMap(timeGuardClockMap)
                                .resetClockSet(resetClocks)
                                .build();
                        newTransitions.add(newTransition);
                    }
                }
            }
            //第二种情况，只有ta1存在的动作
            if (ta1.containsSymbol(e) && !ta2.containsSymbol(e)) {
                asyncTransitions(ta1.getTransitions(), ta2.getLocations(), pairLocationMap, newTransitions);
            }
            //第三种情况，只有dfa2存在的动作
            if (!ta1.containsSymbol(e) && ta2.containsSymbol(e)) {
                asyncTransitions(ta2.getTransitions(), ta1.getLocations(), pairLocationMap, newTransitions);
            }
        });

        //构造组合自动机TA
        TA newTA = new TA.TABuilder()
                .name(ta1.getName() + "_" + ta2.getName())
                .locations(newLocations)
                .transitions(newTransitions)
                .sigma(sigma)
                .clockSet(clocks)
                .build();
        return newTA;

    }

    private static void asyncTransitions(List<TaTransition> transitions,
                                         List<TaLocation> locations,
                                         Map<Pair, TaLocation> pairLocationMap,
                                         List<TaTransition> newTransitions) {
        for (TaTransition t : transitions) {
            for (TaLocation l : locations) {
                Pair sourcePair = new Pair(t.getSourceLocation(), l);
                Pair targetPair = new Pair(t.getTargetLocation(), l);
                TaLocation sourceLocation = pairLocationMap.get(sourcePair);
                TaLocation targetLocation = pairLocationMap.get(targetPair);
                TaTransition newTransition = new TaTransition.TaTransitionBuilder()
                        .sourceLocation(sourceLocation)
                        .targetLocation(targetLocation)
                        .symbol(t.getSymbol())
                        .clockTimeGuardMap(t.getClockTimeGuardMap())
                        .resetClockSet(t.getResetClockSet())
                        .build();
                newTransitions.add(newTransition);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class Pair {
        TaLocation location1;
        TaLocation location2;
    }

    //时间自动机求补
    public static TA completeTA(TA ta) {
        ta = ta.copy();
        return null;
    }

    //时间自动机取反
    public static TA negTA(TA ta) {
        TA neg = ta.copy();
        for (TaLocation l : neg.getLocations()) {
            l.setAccept(!l.isAccept());
        }
        return neg;
    }


}
