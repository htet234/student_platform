package studentplatform.student_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studentplatform.student_platform.model.Event;
import studentplatform.student_platform.model.EventParticipation;
import studentplatform.student_platform.model.EventParticipation.ParticipationStatus;
import studentplatform.student_platform.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    List<EventParticipation> findByStudent(Student student);

    List<EventParticipation> findByEvent(Event event);

    List<EventParticipation> findByStatus(ParticipationStatus status);

    List<EventParticipation> findByEventAndStatus(Event event, ParticipationStatus status);

    List<EventParticipation> findByStudentAndStatus(Student student, ParticipationStatus status);

    Optional<EventParticipation> findByStudentAndEvent(Student student, Event event);

    boolean existsByStudentIdAndEventId(Long studentId, Long eventId);

    List<EventParticipation> findByPointsAwardedFalse();

    @Query("SELECT ep FROM EventParticipation ep WHERE ep.status = :status AND ep.pointsAwarded = false")
    List<EventParticipation> findApprovedParticipationsWithoutPoints(@Param("status") ParticipationStatus status);

    @Query("SELECT ep FROM EventParticipation ep WHERE ep.pointsAwarded = false AND (ep.event.endTime < :now OR ep.event.endTime = :now)")
    List<EventParticipation> findByPointsAwardedFalseAndEventEndTimeBeforeOrEventEndTimeEquals(@Param("now") java.time.LocalDateTime now);
    
    @Query("SELECT COUNT(ep) > 0 FROM EventParticipation ep WHERE ep.student.id = :studentId AND ep.status = :status AND ep.event.endTime > CURRENT_TIMESTAMP")
    boolean hasActiveEventParticipation(@Param("studentId") Long studentId, @Param("status") ParticipationStatus status);
}


