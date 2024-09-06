package odiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import odiro.domain.Location;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE l.dayPlan IS NULL AND l.plan.id = :planId")
    List<Location> findByPlanIdAndDayPlanIsNull(Long planId);
}
