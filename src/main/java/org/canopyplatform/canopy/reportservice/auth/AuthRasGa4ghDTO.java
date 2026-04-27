package org.canopyplatform.canopy.reportservice.auth;

import lombok.Data;

@Data
public class AuthRasGa4ghDTO {

    private String type;
    private long asserted;
    private String value;
    private String source;
    private String by;
}
