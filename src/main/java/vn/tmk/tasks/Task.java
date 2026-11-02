package vn.tmk.tasks;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Entity
@Table(name = "tasks")
public class Task {
    public enum Status { TODO, IN_PROGRESS, DONE }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 2000)
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Status status;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    protected Task() {}
    public Task(TaskRequest request) { update(request); createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS); }
    public void update(TaskRequest request) {
        title = request.title().trim();
        description = request.description() == null ? "" : request.description().trim();
        status = request.status();
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
