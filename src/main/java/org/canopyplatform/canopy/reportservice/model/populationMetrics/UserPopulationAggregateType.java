package org.canopyplatform.canopy.reportservice.model.populationMetrics;

import java.util.HashMap;
import java.util.Map;

public enum UserPopulationAggregateType {
    TYPE("type"),
    LOCATION("location"),
    FOR_PROFIT("profit"),
    LEVEL("level"),
    EMAIL("email");

    public final String label;

    private static final Map<String, UserPopulationAggregateType> BY_LABEL = new HashMap<>();
    static {
        for(UserPopulationAggregateType type : values()){
            BY_LABEL.put(type.label, type);
        }
    }

    UserPopulationAggregateType(String label){
        this.label = label;
    }

    public static UserPopulationAggregateType valueOfLabel(String label){
        return BY_LABEL.get(label);
    }

}
