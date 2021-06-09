package nom.aob.rest.json.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Fruit {

    public Long id;

    public String name;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}