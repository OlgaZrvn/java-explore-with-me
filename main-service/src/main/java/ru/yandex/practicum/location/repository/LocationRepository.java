package ru.yandex.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.location.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
}
