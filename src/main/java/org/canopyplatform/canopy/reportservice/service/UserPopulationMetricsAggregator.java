package org.canopyplatform.canopy.reportservice.service;

import org.canopyplatform.canopy.reportservice.model.populationMetrics.*;
import org.canopyplatform.canopy.reportservice.repositories.UserLoginRepository;
import org.canopyplatform.canopy.reportservice.repositories.ViewUserPopulationRepository;
import org.canopyplatform.canopy.reportservice.model.ViewUserPopulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPopulationMetricsAggregator {

    private final ViewUserPopulationRepository viewUserPopulationRepository;
    private final UserLoginRepository userLoginRepository;

    public UserPopulationMetrics getUserPopulationMetrics(UserPopulationAggregateType aggType, LocalDateTime startDate, LocalDateTime endDate){
        List<ViewUserPopulation> viewUserPopulationList = viewUserPopulationRepository.findByCreatedAtLessThan(endDate);
        List<Integer> userLoginIds = userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(
                OffsetDateTime.of(startDate, ZoneOffset.UTC), OffsetDateTime.of(endDate, ZoneOffset.UTC));
        return switch (aggType) {
            case TYPE -> new UserMetricsByInstitutionType(viewUserPopulationList, userLoginIds, startDate, endDate);
            case LOCATION -> new UserMetricsByLocation(viewUserPopulationList, userLoginIds, startDate, endDate);
            case FOR_PROFIT -> new UserMetricsByProfit(viewUserPopulationList, userLoginIds, startDate, endDate);
            case LEVEL -> new UserMetricsByResearcherLevel(viewUserPopulationList, userLoginIds, startDate, endDate);
            case EMAIL -> new UserMetricsByEmail(viewUserPopulationList, startDate, endDate);
        };
    }

}
