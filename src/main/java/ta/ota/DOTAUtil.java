package ta.ota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import ta.Clock;
import ta.TaLocation;
import ta.TaTransition;
import ta.TimeGuard;

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
}
