package com.eduplatform.observer;

import com.eduplatform.model.Grade;
import java.util.Objects;

/** A failed post-save observer callback. The grade remains committed and the failure remains inspectable. */
public record ObserverFailure(Grade grade, String observerClass, RuntimeException cause) {
    public ObserverFailure {
        Objects.requireNonNull(grade, "grade must not be null");
        Objects.requireNonNull(observerClass, "observer class must not be null");
        Objects.requireNonNull(cause, "cause must not be null");
    }
}
