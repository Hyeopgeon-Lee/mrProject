package mongo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Setter
@Getter
public class AccessLogDTO {

    private String ip; // IP
    private String reqTime; // 요청일시
    private String reqMethod; // 요청방법
    private String reqURI; // 요청URI
}

