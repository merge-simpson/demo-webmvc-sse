package example.tagged.controller;

import example.common.sse.tags.EmitterTag;
import example.tagged.usecase.TaggedSseSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public final class TaggedEventSubscriptionApi {

    private final TaggedSseSubscriptionUseCase subscriptionUseCase;

    private final Map<String, EmitterTag> emitterTags_delete_if_using_db = new ConcurrentHashMap<>(
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
                .map(this.emitterTags_delete_if_using_db::get)
                .toList();

        return subscriptionUseCase.subscriptionWithTags(emitterTags);
    }
}
