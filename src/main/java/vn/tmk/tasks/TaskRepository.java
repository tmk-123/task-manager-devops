package vn.tmk.tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(Task.Status status, Pageable pageable);
}
