package pl.owolny.oauth2service.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public abstract class RedisService<T, ID> {

    private final CrudRepository<T, ID> repository;

    public RedisService(CrudRepository<T, ID> repository) {
        this.repository = repository;
    }

    public Optional<T> find(ID id) {
        return this.repository.findById(id);
    }

    public void save(T T) {
        this.repository.save(T);
    }

    public void delete(ID id) {
        this.repository.deleteById(id);
    }
}