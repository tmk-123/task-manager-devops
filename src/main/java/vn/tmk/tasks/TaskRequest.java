package vn.tmk.tasks;
import jakarta.validation.constraints.*;
public record TaskRequest(
    @NotBlank @Size(max = 150) String title,
    @Size(max = 2000) String description,
    @NotNull Task.Status status
) {}
