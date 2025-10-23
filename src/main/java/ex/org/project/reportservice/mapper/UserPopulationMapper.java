package ex.org.project.reportservice.mapper;

import ex.org.project.reportservice.model.ViewUserPopulation;
import ex.org.project.reportservice.model.dto.UserMetricsEmailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserPopulationMapper {

    @Mapping(source = "emailAddress", target = "email")
    @Mapping(source = "institutionName", target = "institution")
    @Mapping(source = "state", target = "userState")
    @Mapping(source = "country", target = "userCountry")
    @Mapping(source = "orcidId", target = "orcidId")
    @Mapping(source = "institutionType", target = "institutionType")
    @Mapping(source = "totalLogin", target = "totalLogin")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName ="stringMapper")
    @Mapping(source = "lastLogin", target = "lastLogin", qualifiedByName ="stringMapper")
    @Mapping(source = "userResearchLevel", target = "userResearcherLevel")
    @Mapping(source = "internalUser", target = "internalUser", qualifiedByName ="stringMapper")
    @Mapping(source = "hasWorkbench", target = "hasWorkbench", qualifiedByName ="stringMapper")
    @Mapping(source = "workspaceCount", target = "workspaceCount")
    @Mapping(source = "downloadedData", target = "downloadedData", qualifiedByName ="stringMapper")
    UserMetricsEmailDto toDTO(ViewUserPopulation viewUserPopulation);

    @Named("stringMapper")
    public static String mapString(Object value){
        if(value == null){
            return "";
        }
        return value.toString();
    }

}
