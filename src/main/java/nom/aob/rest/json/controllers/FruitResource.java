package nom.aob.rest.json.controllers;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import lombok.extern.slf4j.Slf4j;
import nom.aob.rest.json.model.Fruit;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Startup
@Path("fruits")
@Slf4j
public class FruitResource {

//    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Inject
    @ConfigProperty(name = "app.schema.create", defaultValue = "false")
    boolean schemaCreate;

    public FruitResource(PgPool client) {
        this.client = client;
        System.out.println("constructed fruit resource..");
    }

    @PostConstruct
    void config() {
        System.out.println("initialized fruit resource.. postconstruct..");
        if (schemaCreate) {
            initdb();
        } else {
            Multi<Fruit> fruits = findAll();
            fruits.onItem().invoke(f -> {
                System.out.println("Fruit = "+f.id+","+f.name);
            });
        }
    }

    private void initdb() {
        log.info("Initializing db ...");
        client.query("DROP TABLE IF EXISTS fruits").execute()
                .flatMap(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Orange')").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Pear')").execute())
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Apple')").execute())
                .await().indefinitely();
    }

    private static Fruit fromRowToFruit(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

    public static Multi<Fruit> findAll(PgPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(FruitResource::fromRowToFruit);
    }

    @GET
    @Path("all")
    public Multi<Fruit> findAll() {
        return FruitResource.findAll(client);
    }

}