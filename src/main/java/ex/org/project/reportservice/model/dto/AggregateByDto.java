package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregateByDto {

    String aggregate;
    Integer registeredUsers;
    Integer activeUsers;

}

