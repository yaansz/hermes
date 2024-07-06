package com.softawii.hermes.service;

import com.softawii.hermes.entity.LocationModel;
import com.softawii.hermes.entity.PlayerModel;
import com.softawii.hermes.exceptions.DuplicateKeyException;
import com.softawii.hermes.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    private final PlayerRepository repository;
    private final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public void savePlayer(PlayerModel player) {
        this.repository.save(player);
    }

    public void createEmptyUser(UUID id) {
        if(this.repository.findById(id.toString()).isEmpty()) {
            logger.info("Player created : " + id);
            this.repository.save(new PlayerModel(id.toString(), List.of()));
        }
    }

    public Optional<PlayerModel> getPlayerById(UUID id) {
        return this.repository.findById(id.toString());
    }

    public boolean addLocation(UUID id, LocationModel location) throws DuplicateKeyException {
        Optional<PlayerModel> modelOptional = this.getPlayerById(id);

        if(modelOptional.isPresent()) {
            PlayerModel model = modelOptional.get();

            for(LocationModel loc : model.getLocations()) {
                if(loc.getName().equals(location.getName())) {
                    throw new DuplicateKeyException();
                }
            }

            model.getLocations().add(location);
            this.savePlayer(model);
            return true;
        }
        return false;
    }

    public boolean removeLocation(UUID id, String name) {
        Optional<PlayerModel> modelOptional = this.getPlayerById(id);

        if(modelOptional.isPresent()) {
            PlayerModel model = modelOptional.get();

            for(LocationModel loc : model.getLocations()) {
                if(loc.getName().equals(name)) {
                    model.getLocations().remove(loc);
                    this.savePlayer(model);
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getMatchingLocations(UUID id, String name) {
        Optional<PlayerModel> modelOptional = this.getPlayerById(id);

        if(modelOptional.isPresent()) {
            PlayerModel model = modelOptional.get();
            List<String> locations = model.getLocations().stream()
                    .map(LocationModel::getName)
                    .filter(loc -> loc.startsWith(name))
                    .toList();
            return locations;
        }
        return List.of();
    }
}
