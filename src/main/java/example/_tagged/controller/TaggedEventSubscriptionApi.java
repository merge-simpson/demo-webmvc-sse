package example._tagged.controller;

import example.common.sse.tags.EmitterTag;
import example._tagged.dto.TaggedSseBroadcastDto.TaggedEachSseBroadcastRequestDto;
import example._tagged.dto.TaggedSseBroadcastDto.TaggedEachSseBroadcastResponseDto;
import example._tagged.dto.TaggedSseBroadcastDto.TaggedEnBlocSseBroadcastRequestDto;
import example._tagged.dto.TaggedSseBroadcastDto.TaggedEnBlocSseBroadcastResponseDto;
import example._tagged.usecase.TaggedEnBlocSseBroadcastUseCase;
import example._tagged.usecase.TaggedSseSubscriptionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public final class TaggedEventSubscriptionApi {

    private final TaggedSseSubscriptionUseCase subscriptionUseCase;
    private final TaggedEnBlocSseBroadcastUseCase broadCastEnBlocUseCase;

    public static final Map<String, EmitterTag> EMITTER_TAGS_DELETE_IF_USING_ANOTHER = new ConcurrentHashMap<>(
            Map.of(
                    "a1", EmitterTag.builder().id("a1").name("inquiry-all").build(),
                    "a2", EmitterTag.builder().id("a2").name("inquiry-a").build(),
                    "a3", EmitterTag.builder().id("a3").name("inquiry-b").build(),
                    "a4", EmitterTag.builder().id("a4").name("inquiry-c").build(),
                    "a5", EmitterTag.builder().id("a5").name("example-tag-5").build()
            )
    );

    @GetMapping(path = "/events/tags/subscription", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeEventWithTag(@RequestParam List<String> tags) {
        // 쿼리스트링에서는 tag id를 받아 올 것.
        // TODO 이 사용자에게 각 태그에 권한이 있나 확인
        //  확인되면 그 태그들을 TaggedSseEmitter에 달아 주기 (지금은 예시로)
        List<EmitterTag> emitterTags = tags.stream()
                .map(EMITTER_TAGS_DELETE_IF_USING_ANOTHER::get)
                .toList();

        return subscriptionUseCase.subscriptionWithTags(emitterTags);
    }

    @PostMapping("/events/tags/broadcast/example")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaggedEnBlocSseBroadcastResponseDto enBlocTrigger(
            @RequestBody @Valid TaggedEnBlocSseBroadcastRequestDto body
    ) {
        List<String> tagIds = body.tagIds();
        String taskId = broadCastEnBlocUseCase.broadcastEnBlocAsync(tagIds);
        return TaggedEnBlocSseBroadcastResponseDto.builder()
                .taskId(taskId)
                .build();
    }

    @PostMapping("/events/tags/broadcast/example-each")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaggedEachSseBroadcastResponseDto eachSseTrigger(
            @RequestBody @Valid TaggedEachSseBroadcastRequestDto body
    ) {
        List<String> tagIds = body.tagIds();
        return TaggedEachSseBroadcastResponseDto.builder()
                .taskId("TODO")
                .build();
    }
}
