package nom.aob.rest.json.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nom.aob.rest.json.utils.Utils;

@Data
@Builder
//@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SimpleResponse {
    private String hostString;
    private String pathString;
    private String timeString;
    private Integer randomInteger;
    private Long threadID;


    public static SimpleResponse newSimpleResponse(String path) {
        return new SimpleResponse(
                Utils.getHostname(),
                path,
                Utils.getCurrentTimeString(),
                Utils.newRandomInt(),
                Thread.currentThread().getId()
        );
    }

}
