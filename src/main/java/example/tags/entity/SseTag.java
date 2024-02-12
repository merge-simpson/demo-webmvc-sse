package example.tags.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SseTag {
    private String id;
    private String name;
}
