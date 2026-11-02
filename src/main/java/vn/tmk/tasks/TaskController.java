package vn.tmk.tasks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {
    private final TaskRepository repository;
    public TaskController(TaskRepository repository) { this.repository = repository; }
    public record TaskPage(List<Task> content, int page, int size, long totalElements, int totalPages) {}
    @GetMapping
    public TaskPage list(@RequestParam(required = false) Task.Status status,
                         @RequestParam(defaultValue = "0") @Min(0) int page,
                         @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        var result = status == null ? repository.findAll(pageable) : repository.findByStatus(status, pageable);
        return new TaskPage(result.getContent(), page, size, result.getTotalElements(), result.getTotalPages());
    }
    @GetMapping("/{id}")
    public Task get(@PathVariable long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        var task = repository.save(new Task(request));
        return ResponseEntity.created(URI.create("/api/tasks/" + task.getId())).body(task);
    }
    @PutMapping("/{id}")
    public Task update(@PathVariable long id, @Valid @RequestBody TaskRequest request) {
        var task = get(id); task.update(request); return repository.save(task);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) { repository.delete(get(id)); }
}
