package dfa;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class DFAUtil {

    public static DFA getCartesian(DFA dfa1, DFA dfa2) {
        //存放新旧节点对应关系的map
        Map<Pair, DfaLocation> pairLocationMap = new HashMap<>();
        Map<DfaLocation, Pair> locationPairMap = new HashMap<>();

        //构造节点的笛卡尔积
        List<DfaLocation> newLocations = new ArrayList<>();
        for (DfaLocation l1 : dfa1.getLocations()) {
            for (DfaLocation l2 : dfa2.getLocations()) {
                DfaLocation newLocation = new DfaLocation.DfaLocationBuilder()
                        .id(l1.getId() + "_" + l2.getId())
                        .name(l1.getName() + "_" + l2.getName())
                        .accept(l1.isAccept() && l2.isAccept())
                        .init(l1.isInit() && l2.isInit())
                        .build();
                Pair pair = new Pair(l1, l2);
                pairLocationMap.put(pair, newLocation);
                locationPairMap.put(newLocation, pair);
                newLocations.add(newLocation);
            }
        }

        //sigma求并集
        Set<String> sigma = new HashSet<>();
        sigma.addAll(dfa1.getSigma());
        sigma.addAll(dfa2.getSigma());

        //构造迁移的笛卡尔积
        //遍历sigma，分三种情况求迁移
        List<DfaTransition> newTransitions = new ArrayList<>();
        sigma.stream().forEach(e -> {
            //第一种情况，两边都含有相同的动作,需要对其进行同步操作
            if (dfa1.containsSymbol(e) && dfa2.containsSymbol(e)) {
                for (DfaTransition t1 : dfa1.getTransitions(e)) {
                    for (DfaTransition t2 : dfa2.getTransitions(e)) {
                        Pair sourcePair = new Pair(t1.getSourceLocation(), t2.getSourceLocation());
                        Pair targetPair = new Pair(t1.getTargetLocation(), t2.getTargetLocation());
                        DfaLocation sourceLocation = pairLocationMap.get(sourcePair);
                        DfaLocation targetLocation = pairLocationMap.get(targetPair);
                        DfaTransition newTransition = new DfaTransition.DfaTransitionBuilder()
                                .sourceLocation(sourceLocation)
                                .targetLocation(targetLocation)
                                .symbol(t1.getSymbol())
                                .build();
                        newTransitions.add(newTransition);
                    }
                }
            }
            //第二种情况，只有dfa1存在的动作
            if (dfa1.containsSymbol(e) && !dfa2.containsSymbol(e)) {
                asyncTransitions(dfa1.getTransitions(),dfa2.getLocations(),pairLocationMap,newTransitions);
            }
            //第三种情况，只有dfa2存在的动作
            if (!dfa1.containsSymbol(e) && dfa2.containsSymbol(e)) {
                asyncTransitions(dfa2.getTransitions(),dfa1.getLocations(),pairLocationMap,newTransitions);
            }
        });

        //构造笛卡尔积自动机DFA
        DFA newDFA = new DFA.DFABuilder()
                .name(dfa1.getName() + "_" + dfa2.getName())
                .locations(newLocations)
                .transitions(newTransitions)
                .sigma(sigma)
                .build();
        return newDFA;
    }

    private static void asyncTransitions(List<DfaTransition> transitions,
                                  List<DfaLocation> locations,
                                  Map<Pair, DfaLocation> pairLocationMap,
                                  List<DfaTransition> newTransitions
                                  ){
        for (DfaTransition t : transitions) {
            for (DfaLocation l : locations) {
                Pair sourcePair = new Pair(t.getSourceLocation(), l);
                Pair targetPair = new Pair(t.getTargetLocation(), l);
                DfaLocation sourceLocation = pairLocationMap.get(sourcePair);
                DfaLocation targetLocation = pairLocationMap.get(targetPair);
                DfaTransition newTransition = new DfaTransition.DfaTransitionBuilder()
                        .sourceLocation(sourceLocation)
                        .targetLocation(targetLocation)
                        .symbol(t.getSymbol())
                        .build();
                newTransitions.add(newTransition);
            }
        }
    }


    @Data
    @AllArgsConstructor
    private static class Pair {
        private DfaLocation location1;
        private DfaLocation location2;
    }

}
