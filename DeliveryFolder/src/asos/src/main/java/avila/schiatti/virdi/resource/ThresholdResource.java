package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.health.HealthParameter;
import avila.schiatti.virdi.model.health.Threshold;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ThresholdResource {
    private static final ThresholdResource _instance = new ThresholdResource();

    private ArrayList<Threshold> thresholds = new ArrayList<>(9);

    private ThresholdResource(){
        thresholds.add(new Threshold(60, 65,80D, 120D, HealthParameter.HART_RATE));
        thresholds.add(new Threshold(65, 70,78D, 116D, HealthParameter.HART_RATE));
        thresholds.add(new Threshold(70, 75,75D, 113D, HealthParameter.HART_RATE));
        thresholds.add(new Threshold(75, Integer.MAX_VALUE,73D, 109D, HealthParameter.HART_RATE));
        thresholds.add(new Threshold(60, 65, 35.2D, 36.9D, HealthParameter.TEMPERATURE));
        thresholds.add(new Threshold(65, Integer.MAX_VALUE,35.6D, 36.3D, HealthParameter.TEMPERATURE));
        thresholds.add(new Threshold(Integer.MIN_VALUE, Integer.MAX_VALUE, 90D,250D, HealthParameter.SYSTOLIC));
        thresholds.add(new Threshold(Integer.MIN_VALUE, Integer.MAX_VALUE, 60D,140D, HealthParameter.DIASTOLIC));
        thresholds.add(new Threshold(Integer.MIN_VALUE, Integer.MAX_VALUE, 97D,100D, HealthParameter.BLOOD_OXYGEN));
    }

    public static ThresholdResource getInstance(){
        return _instance;
    }

    public HashMap<HealthParameter, Threshold> get(Integer age){
        return thresholds.stream()
                .filter(t -> (t.getMinAge() <= age && t.getMaxAge() >= age))
                .collect(Collectors.toMap(Threshold::getParameter, Function.identity(), (o, n) -> o, HashMap::new));
    }

    public Boolean compare(Double value, Threshold threshold){
        return (threshold.getMaxValue() < value || threshold.getMinValue() > value);
    }

}
