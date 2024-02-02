package example.tagged.repository.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FutureStatus {
    RUNNING,
    DONE,
    CANCELED,
    EXCEPTION,
    NOT_FOUND;
}
