package ex.org.project.reportservice.service;

import ex.org.project.reportservice.mapper.UserPopulationMapper;
import ex.org.project.reportservice.model.ViewUserPopulation;
import ex.org.project.reportservice.model.dto.*;
import ex.org.project.reportservice.model.populationMetrics.*;
import ex.org.project.reportservice.repositories.UserLoginRepository;
import ex.org.project.reportservice.repositories.ViewUserPopulationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPopulationMetricsServiceTests {

    private ViewUserPopulationRepository viewUserPopulationRepository;

    private UserLoginRepository userLoginRepository;

    private UserPopulationMetricsAggregator userPopulationMetricsAggregator;

    private UserPopulationMetricsService userPopulationMetricsService;
    private UserPopulationMapper userPopulationMapper;

    @BeforeEach
    void setup(){
        userPopulationMapper = Mappers.getMapper(UserPopulationMapper.class);
        viewUserPopulationRepository = mock(ViewUserPopulationRepository.class);
        userLoginRepository = mock(UserLoginRepository.class);
        userPopulationMetricsAggregator = new UserPopulationMetricsAggregator(viewUserPopulationRepository, userLoginRepository);
        userPopulationMetricsService = new UserPopulationMetricsService(userPopulationMetricsAggregator);
    }

    @Test
    void testGetUserMetricsByAggregateType(){
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setInstitutionType("TestInstitutionType");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        List<ViewUserPopulation> institutionTypeList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsTypeDto dto = new UserMetricsTypeDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setInstitutionType("TestInstitutionType");

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionTypeList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        UserMetricsResponse result = userPopulationMetricsService.getUserMetricsByAggregate("type", "2021-12-01", "2023-12-31");

        List<String> columnList = result.columnNames();
        Assertions.assertTrue(columnList.contains("Institution: Type"));
        Assertions.assertTrue(columnList.contains("Registered Users"));
        Assertions.assertTrue(columnList.contains("Active Users"));
        List<UserMetricsTypeDto> resultList = (List<UserMetricsTypeDto>) result.aggDtos();
        Assertions.assertEquals(dto.getInstitutionType(), resultList.get(0).getInstitutionType());
        Assertions.assertEquals(dto.getActiveUsers(), resultList.get(0).getActiveUsers());
        Assertions.assertEquals(dto.getRegisteredUsers(), resultList.get(0).getRegisteredUsers());
    }


    @Test
    void testUserPopulationTypeCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setInstitutionType("TestInstitutionType");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setWorkspaceCount(1);
        List<ViewUserPopulation> institutionTypeList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsTypeDto dto = new UserMetricsTypeDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setInstitutionType("TestInstitutionType");
        dto.setWorkspaceCount(1);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionTypeList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);
        userPopulationMetricsService.getUserPopulationMetricsCSV(response, "type", "2021-04-01", "2023-12-31");

        String s = "\"INSTITUTION: TYPE\",\"REGISTERED USERS\",\"ACTIVE USERS\",\"WORKSPACE COUNT\"\n" +
                "\"TestInstitutionType\",\"1\",\"1\",\"1\"\n";
        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"User_Population_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        Assertions.assertEquals(s, response.getContentAsString());
    }

    @Test
    void testGetUserMetricsByAggregateLocation(){
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setCountry("United States");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsLocationDto dto = new UserMetricsLocationDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setInstitutionLocation("United States");

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);
        UserMetricsResponse result = userPopulationMetricsService.getUserMetricsByAggregate("location", "2021-12-01", "2023-12-31");

        List<String> columnList = result.columnNames();
        Assertions.assertTrue(columnList.contains("Institution: Location"));
        Assertions.assertTrue(columnList.contains("Registered Users"));
        Assertions.assertTrue(columnList.contains("Active Users"));
        List<UserMetricsLocationDto> resultList = (List<UserMetricsLocationDto>) result.aggDtos();
        Assertions.assertEquals(dto.getInstitutionLocation(), resultList.get(0).getInstitutionLocation());
        Assertions.assertEquals(dto.getActiveUsers(), resultList.get(0).getActiveUsers());
        Assertions.assertEquals(dto.getRegisteredUsers(), resultList.get(0).getRegisteredUsers());
    }

    @Test
    void testUserPopulationLocationCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setCountry("United States");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setWorkspaceCount(1);
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsLocationDto dto = new UserMetricsLocationDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setInstitutionLocation("United States");
        dto.setWorkspaceCount(1);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        userPopulationMetricsService.getUserPopulationMetricsCSV(response, "location", "2021-04-01", "2023-12-31");

        String s = "\"INSTITUTION: LOCATION\",\"REGISTERED USERS\",\"ACTIVE USERS\",\"WORKSPACE COUNT\"\n" +
                "\"United States\",\"1\",\"1\",\"1\"\n";
        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"User_Population_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        Assertions.assertEquals(s, response.getContentAsString());
    }

    @Test
    void testGetUserMetricsByAggregateProfit(){
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setIsForProfit(true);
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsProfitDto dto = new UserMetricsProfitDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setProfitNotForProfit("Profit");

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        UserMetricsResponse result = userPopulationMetricsService.getUserMetricsByAggregate("profit", "2021-12-01", "2023-12-31");

        List<String> columnList = result.columnNames();
        Assertions.assertTrue(columnList.contains("Institution: Profit/Not for Profit"));
        Assertions.assertTrue(columnList.contains("Registered Users"));
        Assertions.assertTrue(columnList.contains("Active Users"));
        List<UserMetricsProfitDto> resultList = (List<UserMetricsProfitDto>) result.aggDtos();
        Assertions.assertEquals(dto.getProfitNotForProfit(), resultList.get(0).getProfitNotForProfit());
        Assertions.assertEquals(dto.getActiveUsers(), resultList.get(0).getActiveUsers());
        Assertions.assertEquals(dto.getRegisteredUsers(), resultList.get(0).getRegisteredUsers());
    }

    @Test
    void testUserPopulationProfitCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setIsForProfit(true);
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setWorkspaceCount(1);
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsProfitDto dto = new UserMetricsProfitDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setProfitNotForProfit("Profit");
        dto.setWorkspaceCount(1);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        userPopulationMetricsService.getUserPopulationMetricsCSV(response, "profit", "2021-04-01", "2023-12-31");

        String s = "\"INSTITUTION: PROFIT/NOT FOR PROFIT\",\"REGISTERED USERS\",\"ACTIVE USERS\",\"WORKSPACE COUNT\"\n" +
                "\"Profit\",\"1\",\"1\",\"1\"\n";
        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"User_Population_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        Assertions.assertEquals(s, response.getContentAsString());
    }

    @Test
    void testGetUserMetricsByAggregateLevel(){
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setUserResearchLevel("Grandmaster");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsLevelDto dto = new UserMetricsLevelDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setUserResearcherLevel("Grandmaster");

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        UserMetricsResponse result = userPopulationMetricsService.getUserMetricsByAggregate("level", "2021-12-01", "2023-12-31");

        List<String> columnList = result.columnNames();
        Assertions.assertTrue(columnList.contains("User: Researcher Level"));
        Assertions.assertTrue(columnList.contains("Registered Users"));
        Assertions.assertTrue(columnList.contains("Active Users"));
        List<UserMetricsLevelDto> resultList = (List<UserMetricsLevelDto>) result.aggDtos();
        Assertions.assertEquals(dto.getUserResearcherLevel(), resultList.get(0).getUserResearcherLevel());
        Assertions.assertEquals(dto.getActiveUsers(), resultList.get(0).getActiveUsers());
        Assertions.assertEquals(dto.getRegisteredUsers(), resultList.get(0).getRegisteredUsers());
    }

    @Test
    void testUserPopulationLevelCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setUserResearchLevel("Grandmaster");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setWorkspaceCount(1);
        List<ViewUserPopulation> institutionLocationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsLevelDto dto = new UserMetricsLevelDto();
        dto.setActiveUsers(1);
        dto.setRegisteredUsers(1);
        dto.setUserResearcherLevel("Grandmaster");
        dto.setWorkspaceCount(1);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(institutionLocationList);
        when(userLoginRepository.findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(Mockito.any(), Mockito.any()))
                .thenReturn(userLoginIds);

        userPopulationMetricsService.getUserPopulationMetricsCSV(response, "level", "2021-04-01", "2023-12-31");

        String s = "\"USER: RESEARCHER LEVEL\",\"REGISTERED USERS\",\"ACTIVE USERS\",\"WORKSPACE COUNT\"\n" +
                "\"Grandmaster\",\"1\",\"1\",\"1\"\n";
        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"User_Population_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        Assertions.assertEquals(s, response.getContentAsString());
    }

    @Test
    void testGetUserMetricsByEmail(){
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setName("Test McTestington");
        userPopulation.setEmailAddress("test@bah.com");
        userPopulation.setUserResearchLevel("Grandmaster");
        userPopulation.setInstitutionName("BAH");
        userPopulation.setInstitutionType("Commercial Company");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setState("VA");
        userPopulation.setCountry("United States");
        userPopulation.setDownloadedData(true);
        userPopulation.setHasWorkbench(true);
        userPopulation.setInternalUser(true);
        userPopulation.setJobTitle("researcher");
        userPopulation.setTotalLogin(5);
        userPopulation.setOrcidId("0000-0001-9593-8074");
        userPopulation.setLastLogin(LocalDateTime.of(2022, 01, 15, 0, 0));

        List<ViewUserPopulation> userPopulationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsEmailDto dto = userPopulationMapper.toDTO(userPopulation);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(userPopulationList);

        UserMetricsResponse result = userPopulationMetricsService.getUserMetricsByAggregate("email", "2021-12-01", "2023-12-31");

        List<String> columnList = result.columnNames();
        List<String> expectedColumns = List.of(NAME, EMAIL, ORCID_ID, JOB_TITLE, INSTITUTION, INSTITUTION_TYPE, USER_LOCATION_STATE, USER_LOCATION_COUNTRY, USER_LEVEL, CREATED_AT, LAST_LOGIN, TOTAL_LOGIN, INTERNAL_USER, DOWNLOADED_DATA, HAS_WORKBENCH);
        for(var column : expectedColumns) {
            Assertions.assertTrue(columnList.contains(column));
        }
        List<UserMetricsEmailDto> resultList = (List<UserMetricsEmailDto>) result.aggDtos();
        Assertions.assertEquals(dto.getName(), resultList.get(0).getName());
        Assertions.assertEquals(dto.getEmail(), resultList.get(0).getEmail());
        Assertions.assertEquals(dto.getOrcidId(), resultList.get(0).getOrcidId());
        Assertions.assertEquals(dto.getInstitution(), resultList.get(0).getInstitution());
        Assertions.assertEquals(dto.getInstitutionType(), resultList.get(0).getInstitutionType());
        Assertions.assertEquals(dto.getJobTitle(), resultList.get(0).getJobTitle());
        Assertions.assertEquals(dto.getCreatedAt(), resultList.get(0).getCreatedAt());
        Assertions.assertEquals(dto.getUserState(), resultList.get(0).getUserState());
        Assertions.assertEquals(dto.getUserCountry(), resultList.get(0).getUserCountry());
        Assertions.assertEquals(dto.getUserResearcherLevel(), resultList.get(0).getUserResearcherLevel());
        Assertions.assertEquals(dto.getLastLogin(), resultList.get(0).getLastLogin());
        Assertions.assertEquals(dto.getTotalLogin(), resultList.get(0).getTotalLogin());
        Assertions.assertEquals(dto.getInternalUser(), resultList.get(0).getInternalUser());
        Assertions.assertEquals(dto.getDownloadedData(), resultList.get(0).getDownloadedData());
        Assertions.assertEquals(dto.getHasWorkbench(), resultList.get(0).getHasWorkbench());
    }

    @Test
    void testUserPopulationEmailCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ViewUserPopulation userPopulation = new ViewUserPopulation();
        userPopulation.setId(1);
        userPopulation.setName("Test McTestington");
        userPopulation.setEmailAddress("test@bah.com");
        userPopulation.setUserResearchLevel("Grandmaster");
        userPopulation.setInstitutionName("BAH");
        userPopulation.setInstitutionType("Commercial Company");
        userPopulation.setJobTitle("Captain");
        userPopulation.setCreatedAt(LocalDateTime.of(2022, 01, 01, 0, 0));
        userPopulation.setState("VA");
        userPopulation.setCountry("United States");
        userPopulation.setOrcidId("0000-0001-9593-8074");
        userPopulation.setTotalLogin(5);
        userPopulation.setDownloadedData(true);
        userPopulation.setHasWorkbench(true);
        userPopulation.setInternalUser(true);
        userPopulation.setWorkspaceCount(2);
        userPopulation.setLastLogin(LocalDateTime.of(2022, 01, 15, 0, 0));
        List<ViewUserPopulation> userPopulationList = List.of(userPopulation);
        List<Integer> userLoginIds = List.of(1);
        UserMetricsEmailDto dto = userPopulationMapper.toDTO(userPopulation);

        when(viewUserPopulationRepository.findByCreatedAtLessThan(Mockito.any()))
                .thenReturn(userPopulationList);

        userPopulationMetricsService.getUserPopulationMetricsCSV(response, "email", "2021-04-01", "2023-12-31");
        String s ="\"NAME\",\"EMAIL ADDRESS\",\"ORCID ID\",\"JOB/TITLE\",\"ORGANIZATION\",\"INSTITUTION: TYPE\",\"USER LOCATION: STATE\",\"USER LOCATION: COUNTRY\""
                + ",\"USER: RESEARCHER LEVEL\",\"REGISTRATION DATE\",\"LAST LOGIN\",\"TOTAL LOGINS\",\"INTERNAL USER?\",\"DOWNLOADED DATA?\",\"HAS WORKBENCH?\",\"WORKSPACE COUNT\"\n"
                + "\"Test McTestington\",\"test@bah.com\",\"0000-0001-9593-8074\",\"Captain\",\"BAH\",\"Commercial Company\",\"VA\",\"United States\",\"Grandmaster\""
                + ",\"2022-01-01T00:00\",\"2022-01-15T00:00\",\"5\",\"true\",\"true\",\"true\",\"2\"\n";
        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"User_Population_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        Assertions.assertEquals(s, response.getContentAsString());
    }

    @Test
    void testGetUserMetricsFromListByAggregate(){
        UserMetricsAggregatesDto result = UserMetricsByProfit.getUserMetricsProfitFromListByAggregate(getProfitDtoList(), "Profit");
        Assertions.assertEquals(result.getActiveUsers(), 10);
        Assertions.assertEquals(result.getRegisteredUsers(), 15);

        result = UserMetricsByProfit.getUserMetricsProfitFromListByAggregate(getProfitDtoList(), "Not for Profit");
        Assertions.assertEquals(result.getActiveUsers(), 20);
        Assertions.assertEquals(result.getRegisteredUsers(), 25);

        result = UserMetricsByInstitutionType.getUserMetricsTypeFromListByAggregate(getTypeDtoList(), "type1");
        Assertions.assertEquals(result.getActiveUsers(), 1);
        Assertions.assertEquals(result.getRegisteredUsers(), 5);

        result = UserMetricsByInstitutionType.getUserMetricsTypeFromListByAggregate(getTypeDtoList(), "type2");
        Assertions.assertEquals(result.getActiveUsers(), 30);
        Assertions.assertEquals(result.getRegisteredUsers(), 35);

        result = UserMetricsByLocation.getUserMetricsLocationFromListByAggregate(getLocationDtoList(), "location1");
        Assertions.assertEquals(result.getActiveUsers(), 40);
        Assertions.assertEquals(result.getRegisteredUsers(), 45);

        result = UserMetricsByLocation.getUserMetricsLocationFromListByAggregate(getLocationDtoList(), "location2");
        Assertions.assertEquals(result.getActiveUsers(), 50);
        Assertions.assertEquals(result.getRegisteredUsers(), 55);

        result = UserMetricsByResearcherLevel.getUserMetricsLevelFromListByAggregate(getLevelDtoList(), "level1");
        Assertions.assertEquals(result.getActiveUsers(), 60);
        Assertions.assertEquals(result.getRegisteredUsers(), 65);

        result = UserMetricsByResearcherLevel.getUserMetricsLevelFromListByAggregate(getLevelDtoList(), "level2");
        Assertions.assertEquals(result.getActiveUsers(), 70);
        Assertions.assertEquals(result.getRegisteredUsers(), 75);
    }


    private List<UserMetricsTypeDto> getTypeDtoList(){
        List<UserMetricsTypeDto> dtoList = new ArrayList<>();
        UserMetricsTypeDto dto1 = new UserMetricsTypeDto();
        dto1.setInstitutionType("type1");
        dto1.setActiveUsers(1);
        dto1.setRegisteredUsers(5);
        dtoList.add(dto1);
        UserMetricsTypeDto dto2 = new UserMetricsTypeDto();
        dto2.setInstitutionType("type2");
        dto2.setActiveUsers(30);
        dto2.setRegisteredUsers(35);
        dtoList.add(dto2);
        return dtoList;
    }

    private List<UserMetricsLocationDto> getLocationDtoList(){
        List<UserMetricsLocationDto> dtoList = new ArrayList<>();
        UserMetricsLocationDto dto1 = new UserMetricsLocationDto();
        dto1.setInstitutionLocation("location1");
        dto1.setActiveUsers(40);
        dto1.setRegisteredUsers(45);
        dtoList.add(dto1);
        UserMetricsLocationDto dto2 = new UserMetricsLocationDto();
        dto2.setInstitutionLocation("location2");
        dto2.setActiveUsers(50);
        dto2.setRegisteredUsers(55);
        dtoList.add(dto2);
        return dtoList;
    }

    private List<UserMetricsLevelDto> getLevelDtoList(){
        List<UserMetricsLevelDto> dtoList = new ArrayList<>();
        UserMetricsLevelDto dto1 = new UserMetricsLevelDto();
        dto1.setUserResearcherLevel("level1");
        dto1.setActiveUsers(60);
        dto1.setRegisteredUsers(65);
        dtoList.add(dto1);
        UserMetricsLevelDto dto2 = new UserMetricsLevelDto();
        dto2.setUserResearcherLevel("level2");
        dto2.setActiveUsers(70);
        dto2.setRegisteredUsers(75);
        dtoList.add(dto2);
        return dtoList;
    }

    private List<UserMetricsProfitDto> getProfitDtoList(){
        List<UserMetricsProfitDto> dtoList = new ArrayList<>();
        UserMetricsProfitDto dto1 = new UserMetricsProfitDto();
        dto1.setProfitNotForProfit("Profit");
        dto1.setActiveUsers(10);
        dto1.setRegisteredUsers(15);
        dtoList.add(dto1);
        UserMetricsProfitDto dto2 = new UserMetricsProfitDto();
        dto2.setProfitNotForProfit("Not for Profit");
        dto2.setActiveUsers(20);
        dto2.setRegisteredUsers(25);
        dtoList.add(dto2);
        return dtoList;
    }


    @Test
    void testIncrementUserMetrics(){
        UserMetricsAggregatesDto dto = new UserMetricsAggregatesDto();
        dto.setRegisteredUsers(0);
        dto.setActiveUsers(0);
        LocalDateTime startDate = LocalDate.of(2023, 12, 15).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2023, 12, 20).atStartOfDay();
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ViewUserPopulation user = new ViewUserPopulation();
        user.setCreatedAt(LocalDate.of(2023, 12, 17).atStartOfDay());
        user.setId(1);

        //Send request with user registered during the given time period.
        UserPopulationMetrics.incrementUserMetrics(user, dto, startDate, endDate, ids);
        Assertions.assertEquals(1, dto.getRegisteredUsers());
        Assertions.assertEquals(1, dto.getActiveUsers());

        //Send request with user only active during the given time
        user.setCreatedAt(LocalDate.of(2023, 12, 10).atStartOfDay());
        UserPopulationMetrics.incrementUserMetrics(user, dto, startDate, endDate, ids);
        Assertions.assertEquals(1, dto.getRegisteredUsers());
        Assertions.assertEquals(2, dto.getActiveUsers());

        //Send request that adds the dto to the list
        UserPopulationMetrics.incrementUserMetrics(user, dto, startDate, endDate, ids);
        Assertions.assertEquals(1, dto.getRegisteredUsers());
        Assertions.assertEquals(3, dto.getActiveUsers());

        //Send request which increments registered and also adds dto to list
        user.setCreatedAt(LocalDate.of(2023, 12, 17).atStartOfDay());
        UserPopulationMetrics.incrementUserMetrics(user, dto, startDate, endDate, ids);
        Assertions.assertEquals(2, dto.getRegisteredUsers());
        Assertions.assertEquals(4, dto.getActiveUsers());
    }
}
