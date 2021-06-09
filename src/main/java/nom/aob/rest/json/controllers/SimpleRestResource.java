package nom.aob.rest.json.controllers;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nom.aob.rest.json.model.SimpleResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Slf4j
@Path(SimpleRestResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimpleRestResource {

    public static final String PATH_SIMPLE = "/simple";
    public static final String PATH_PROXY = "/proxy";
    public static final String PATH = "/rest";
    public static final String SCHEME_HTTP = "http://";

    @ConfigProperty(name = "nom.aob.quarkusbench.logstring")
    Optional<String> logString;

    final WebClient webClient;

    public SimpleRestResource(Vertx vertx) {

        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(true);

        this.webClient = WebClient.create(vertx, options);

        log.info("Constructed instance of {}. logString={}.",
                SimpleRestResource.class.getSimpleName(),
                logString);
    }

    @GET
    @Path("/hello-text")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloText() {
        return "Hello RESTEasy (Text)";
    }

    @GET
    @Path("/hello-json")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject helloJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("msg", "Hello RESTEasy (Json)");
        return jsonObject;
    }

    @GET
    @Path(PATH_SIMPLE)
    @Produces(MediaType.APPLICATION_JSON)
    public @NonNull Uni<SimpleResponse> simpleResponse(
            @HeaderParam(value = "x-b3-traceid") String traceId
    ) {

        logString.ifPresent(s -> log.info("in simpleResponse. logString = {}.",s));

        SimpleResponse simpleResponse = SimpleResponse.newSimpleResponse(PATH_SIMPLE);
        Uni<SimpleResponse> responseUni = Uni.createFrom().item(simpleResponse)
                .onItem().transform(n -> n);
//        Response response = Response.ok(simpleResponse).build();
//        return Uni.createFrom().item(response);
//        return simpleResponse;

//        numRequests.incrementAndGet();

        return responseUni;
    }



    @GET
    @Path(PATH_PROXY+"/{address}/{port}")
    @Produces(MediaType.APPLICATION_JSON)
    public @NonNull Uni<SimpleResponse> simpleProxy(
            @HeaderParam(value = "x-b3-traceid") String traceId,
            @PathParam("address") String address,
            @PathParam("port") Integer port
    ) {

        logString.ifPresent(s -> log.info("in simpleProxy. logString = {}.",s));

        // onSuccess and onFailure methods should be used, for full responsive code...
        return
                this.webClient.get(port, address, PATH + PATH_SIMPLE)
                        .as(BodyCodec.json(SimpleResponse.class))
                        .send().map(
                        resp -> {
                            if (resp.statusCode() == 200) {
                                SimpleResponse simpleResponse = resp.body();
                                return simpleResponse;
                            } else {
                                SimpleResponse simpleResponse = new SimpleResponse();
                                simpleResponse.setPathString(""+resp.statusCode());
                                simpleResponse.setRandomInteger(0);
                                return simpleResponse;
                            }
                        }
                );

    }

}