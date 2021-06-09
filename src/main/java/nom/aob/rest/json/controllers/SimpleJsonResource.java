package nom.aob.rest.json.controllers;

import io.vertx.core.json.JsonObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("simpleJSON")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimpleJsonResource {

    @POST
    @Path("post")
    public @NonNull JsonObject simpleProxy(
            JsonObject jsonObject
    ) {
        log.info("post object = "+jsonObject);
        JsonObject ret = jsonObject.copy();
        ret.put("processed", true);
        return ret;
    }

}
