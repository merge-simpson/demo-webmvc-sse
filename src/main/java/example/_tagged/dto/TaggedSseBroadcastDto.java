package example._tagged.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

public final class TaggedSseBroadcastDto {

    @Builder
    public record TaggedEnBlocSseBroadcastRequestDto(
            @NotEmpty
            List<String> tagIds
    ) {}

    @Builder
    public record TaggedEachSseBroadcastRequestDto(
            @NotEmpty
            List<String> tagIds
    ) {}

    @Builder
    public record TaggedEnBlocSseBroadcastResponseDto(
            String taskId
    ) {}

    @Builder
    public record TaggedEachSseBroadcastResponseDto(
            String taskId
    ) {}

    private TaggedSseBroadcastDto() {}
}
