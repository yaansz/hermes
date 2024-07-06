package com.softawii.hermes.entity;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class PlayerModel {
    @Id
    private String id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<LocationModel> locations;

    public PlayerModel() {
    }

    public PlayerModel(String id, List<LocationModel> locations) {
        this.id = id;
        this.locations = locations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LocationModel> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationModel> locations) {
        this.locations = locations;
    }

    public String getNextDeathName() {
        String name = "Death";

        int index = 1;

        while(true) {
            boolean found = false;

            for(LocationModel location : locations) {
                // checking repeated name
                if(location.getName().equals(name + index)) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                return name + index;
            }

            index++;
        }
    }
}
