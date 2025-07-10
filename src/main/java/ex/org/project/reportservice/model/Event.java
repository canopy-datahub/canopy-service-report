package ex.org.project.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {

    private String eventName;
    private String eventCount;

}
