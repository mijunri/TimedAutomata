package ta.ota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    public static void completeDOTA(DOTA dota){

        Clock clock = dota.getClock();

        List<TaTransition> transitionList = dota.getTransitions();
        List<TaTransition> complementaryTranList = new ArrayList<>();
        List<TaLocation> locationList = dota.getLocations();
        Set<String> sigma = dota.getSigma();

        TaLocation sink = new TaLocation(String.valueOf(dota.size()+1),"sink", false, false);
        for(TaLocation location: locationList){
            for(String symbol: sigma){
                List<TaTransition> transitions = dota.getTransitions(location,symbol,null);
                if (transitions.isEmpty()){
                    Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
                    clockTimeGuardMap.put(clock, new TimeGuard("[0,+)"));
                    Set<Clock> resetClocks = new HashSet<>();
                    resetClocks.add(clock);
                    TaTransition transition  = TaTransition.builder()
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

        if (complementaryTranList.isEmpty()){
            return;
        }

        Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
        clockTimeGuardMap.put(clock, new TimeGuard("[0,+)"));
        Set<Clock> resetClocks = new HashSet<>();
        resetClocks.add(clock);
        for (String symbol : sigma){
            TaTransition transition  = TaTransition.builder()
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

    private static List<TaTransition> complementary(List<TaTransition> transitionList, TaLocation targetLocation, Clock clock){

        List<TimeGuard> timeGuardList = obtainGuardList(transitionList, clock);
        List<TimeGuard> complementaryGuardList = TimeGuardUtil.complementary(timeGuardList);

        TaTransition pre = transitionList.get(0);
        String symbol = pre.getSymbol();
        TaLocation sourceLocation = pre.getSourceLocation();

        List<TaTransition> complementaryTranList = new ArrayList<>();
        for(TimeGuard timeGuard: complementaryGuardList){
            Map<Clock, TimeGuard> clockTimeGuardMap = new HashMap<>();
            clockTimeGuardMap.put(clock, timeGuard);
            Set<Clock> resetClocks = new HashSet<>();
            resetClocks.add(clock);
            TaTransition t = new TaTransition(sourceLocation, targetLocation,symbol, clockTimeGuardMap, resetClocks);
            complementaryTranList.add(t);
        }

        return complementaryTranList;
    }

    private static List<TimeGuard> obtainGuardList(List<TaTransition> transitionList, Clock clock){
        List<TimeGuard> timeGuardList = new ArrayList<>();
        for(TaTransition transition: transitionList){
            timeGuardList.add(transition.getTimeGuard(clock));
        }
        return timeGuardList;
    }

}
