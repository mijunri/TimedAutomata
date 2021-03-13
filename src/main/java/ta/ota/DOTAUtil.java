package ta.ota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dfa.DFA;
import dfa.DfaLocation;
import dfa.DfaTransition;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import ta.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class DOTAUtil {
    public static DOTA getDOTAFromJsonFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuilder json = new StringBuilder();
        while ((str = reader.readLine()) != null) {
            json.append(str);
        }
        DOTA dota = getOTAFromJson(json.toString());
        return dota;
    }

    private static DOTA getOTAFromJson(String json) {
        //定义一个单时钟
        Clock clock = new Clock("c");

        JSONObject jsonObject = JSON.parseObject(json);
        //获取name
        String name = jsonObject.getString("name");
        //获取sigma
        JSONArray jsonArray = jsonObject.getJSONArray("sigma");
        Set<String> sigma = new HashSet<>();
        jsonArray.stream().forEach(e -> {
            sigma.add((String) e);
        });

        //获取location
        Map<String, TaLocation> idLocationMap = new HashMap<>();
        List<TaLocation> locations = new ArrayList<>();
        JSONArray locationArray = jsonObject.getJSONArray("l");
        String initId = jsonObject.getString("init");
        JSONArray acceptArray = jsonObject.getJSONArray("accept");
        Set<String> acceptSet = new HashSet<>();
        acceptArray.stream().forEach(e -> {
            acceptSet.add((String) e);
        });
        locationArray.stream().forEach(e -> {
            String id = (String) e;
            boolean isInit = StringUtils.equals(id, initId);
            boolean isAccept = acceptSet.contains(id);
            TaLocation location = new TaLocation(id, id, isInit, isAccept);
            locations.add(location);
            idLocationMap.put(id, location);
        });

        //获取迁移
        JSONObject tranJsonObject = jsonObject.getJSONObject("tran");
        int size = tranJsonObject.size();
        List<TaTransition> transitions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONArray array = tranJsonObject.getJSONArray(String.valueOf(i));
            String sourceId = array.getString(0);
            String symbol = array.getString(1);
            TimeGuard timeGuard = new TimeGuard(array.getString(2));
            String reset = array.getString(3);
            String targetId = array.getString(4);
            TaLocation sourceLocation = idLocationMap.get(sourceId);
            TaLocation targetLocation = idLocationMap.get(targetId);
            Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
            clockTimeGuardMap.put(clock, timeGuard);
            Set<Clock> resetClockSet = new HashSet<>();
            if (StringUtils.equals(reset, "r")) {
                resetClockSet.add(clock);
            }
            TaTransition transition = new TaTransition(sourceLocation, targetLocation, symbol, clockTimeGuardMap, resetClockSet);
            transitions.add(transition);
        }
        transitions.sort(new OTATranComparator(clock));
        DOTA ota = new DOTA(name, sigma, locations, transitions, clock);
        return ota;
    }

    public static void completeDOTA(DOTA dota) {

        Clock clock = dota.getClock();

        List<TaTransition> transitionList = dota.getTransitions();
        List<TaTransition> complementaryTranList = new ArrayList<>();
        List<TaLocation> locationList = dota.getLocations();
        Set<String> sigma = dota.getSigma();

        TaLocation sink = new TaLocation(String.valueOf(dota.size() + 1), "sink", false, false);
        for (TaLocation location : locationList) {
            for (String symbol : sigma) {
                List<TaTransition> transitions = dota.getTransitions(location, symbol, null);
                if (transitions.isEmpty()) {
                    Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                    clockTimeGuardMap.put(clock, new TimeGuard("[0,+)"));
                    Set<Clock> resetClocks = new HashSet<>();
                    resetClocks.add(clock);
                    TaTransition transition = TaTransition.builder()
                            .sourceLocation(location)
                            .targetLocation(sink)
                            .symbol(symbol)
                            .resetClockSet(resetClocks)
                            .clockTimeGuardMap(clockTimeGuardMap)
                            .build();
                    complementaryTranList.add(transition);
                    continue;
                }
                complementaryTranList.addAll(complementary(transitions, sink, clock));
            }
        }

        if (complementaryTranList.isEmpty()) {
            return;
        }

        Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
        clockTimeGuardMap.put(clock, new TimeGuard("[0,+)"));
        Set<Clock> resetClocks = new HashSet<>();
        resetClocks.add(clock);
        for (String symbol : sigma) {
            TaTransition transition = TaTransition.builder()
                    .sourceLocation(sink)
                    .targetLocation(sink)
                    .symbol(symbol)
                    .resetClockSet(resetClocks)
                    .clockTimeGuardMap(clockTimeGuardMap)
                    .build();
            complementaryTranList.add(transition);
        }

        transitionList.addAll(complementaryTranList);
        locationList.add(sink);
        transitionList.sort(new OTATranComparator(clock));
    }

    private static List<TaTransition> complementary(List<TaTransition> transitionList, TaLocation targetLocation, Clock clock) {

        List<TimeGuard> timeGuardList = obtainGuardList(transitionList, clock);
        List<TimeGuard> complementaryGuardList = TimeGuardUtil.complementary(timeGuardList);

        TaTransition pre = transitionList.get(0);
        String symbol = pre.getSymbol();
        TaLocation sourceLocation = pre.getSourceLocation();

        List<TaTransition> complementaryTranList = new ArrayList<>();
        for (TimeGuard timeGuard : complementaryGuardList) {
            Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
            clockTimeGuardMap.put(clock, timeGuard);
            Set<Clock> resetClocks = new HashSet<>();
            resetClocks.add(clock);
            TaTransition t = new TaTransition(sourceLocation, targetLocation, symbol, clockTimeGuardMap, resetClocks);
            complementaryTranList.add(t);
        }

        return complementaryTranList;
    }

    private static List<TimeGuard> obtainGuardList(List<TaTransition> transitionList, Clock clock) {
        List<TimeGuard> timeGuardList = new ArrayList<>();
        for (TaTransition transition : transitionList) {
            timeGuardList.add(transition.getTimeGuard(clock));
        }
        return timeGuardList;
    }

    public static DOTA getCartesian(DOTA ota, DFA dfa) {

        Clock clock = ota.getClock();
        Map<Pair, TaLocation> pairLocationMap = new HashMap<>();
        //构造节点的笛卡儿积

        for (TaLocation l1 : ota.getLocations()) {
            for (DfaLocation l2 : dfa.getLocations()) {
                String id = l1.getId() + "-" + l2.getId();
                String name = l1.getName() + "_" + l2.getName();
                boolean accept = l1.isAccept() && l2.isAccept();
                boolean init = l1.isInit() && l2.isInit();
                TaLocation taLocation = new TaLocation(id, name, accept, init);
                Pair pair = new Pair(l1, l2);
                pairLocationMap.put(pair, taLocation);
            }
        }
        List<TaLocation> newLocations = new ArrayList<>(pairLocationMap.values());

        //sigma求并集
        Set<String> sigma = new HashSet<>();
        sigma.addAll(ota.getSigma());
        sigma.addAll(dfa.getSigma());

        //构造迁移的笛卡尔积
        //遍历sigma，分三种情况求迁移
        List<TaTransition> newTransitions = new ArrayList<>();
        sigma.stream().forEach(e -> {
            //第一种情况，两边都含有相同的动作,需要对其进行同步操作
            if (ota.containsSymbol(e) && dfa.containsSymbol(e)) {
                for (TaTransition t1 : ota.getTransitions(null, e, null)) {
                    for (DfaTransition t2 : dfa.getTransitions(e)) {
                        Pair sourcePair = new Pair(t1.getSourceLocation(), t2.getSourceLocation());
                        Pair targetPair = new Pair(t1.getTargetLocation(), t2.getTargetLocation());
                        TaLocation sourceLocation = pairLocationMap.get(sourcePair);
                        TaLocation targetLocation = pairLocationMap.get(targetPair);
                        Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                        clockTimeGuardMap.put(clock, t1.getTimeGuard(clock));
                        Set<Clock> clockSet = t1.getResetClockSet();
                        TaTransition newTransition = new TaTransition(
                                sourceLocation, targetLocation, t1.getSymbol(), clockTimeGuardMap, clockSet);
                        newTransitions.add(newTransition);
                    }
                }
            }
            //第二种情况，只有ota存在的动作
            if (ota.containsSymbol(e) && !dfa.containsSymbol(e)) {
                asyncTransitions(clock, ota.getTransitions(), dfa.getLocations(), pairLocationMap, newTransitions);
            }
            //第三种情况，只有dfa2存在的动作
            if (!ota.containsSymbol(e) && dfa.containsSymbol(e)) {
                asyncDfaTransitions(clock, dfa.getTransitions(), ota.getLocations(), pairLocationMap, newTransitions);
            }
        });

        //构造笛卡尔积自动机DFA
        String name = ota.getName() + "_" + dfa.getName();
        DOTA newOTA = new DOTA(name, sigma, newLocations, newTransitions, clock);
        return newOTA;
    }


    private static void asyncDfaTransitions(
            Clock clock,
            List<DfaTransition> transitions,
            List<TaLocation> locationList,
            Map<Pair, TaLocation> pairLocationMap,
            List<TaTransition> newTransitions) {

        for (DfaTransition t : transitions) {
            for (TaLocation l : locationList) {
                Pair sourcePair = new Pair(l, t.getSourceLocation());
                Pair targetPair = new Pair(l, t.getTargetLocation());
                TaLocation sourceLocation = pairLocationMap.get(sourcePair);
                TaLocation targetLocation = pairLocationMap.get(targetPair);

                Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                clockTimeGuardMap.put(clock, new TimeGuard("[0,+)"));
                Set<Clock> clockSet = new HashSet<>();
                TaTransition newTransition = new TaTransition(
                        sourceLocation, targetLocation, t.getSymbol(), clockTimeGuardMap, clockSet);
                newTransitions.add(newTransition);
            }
        }
    }

    private static void asyncTransitions(Clock clock,
                                         List<TaTransition> transitions,
                                         List<DfaLocation> locations,
                                         Map<Pair, TaLocation> pairLocationMap,
                                         List<TaTransition> newTransitions
    ) {
        for (TaTransition t : transitions) {
            for (DfaLocation l : locations) {
                Pair sourcePair = new Pair(t.getSourceLocation(), l);
                Pair targetPair = new Pair(t.getTargetLocation(), l);
                TaLocation sourceLocation = pairLocationMap.get(sourcePair);
                TaLocation targetLocation = pairLocationMap.get(targetPair);
                Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                clockTimeGuardMap.put(clock, t.getTimeGuard(clock));
                Set<Clock> clockSet = t.getResetClockSet();
                TaTransition newTransition = new TaTransition(
                        sourceLocation, targetLocation, t.getSymbol(), clockTimeGuardMap, clockSet);
                newTransitions.add(newTransition);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class Pair {
        private TaLocation location;
        private DfaLocation dfaLocation;
    }

}
