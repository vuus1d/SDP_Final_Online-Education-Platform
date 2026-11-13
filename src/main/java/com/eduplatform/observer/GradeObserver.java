package com.eduplatform.observer;

import com.eduplatform.model.Grade;

/** Observer: receives an update every time a grade is published. */
public interface GradeObserver {

    void onGradePublished(Grade grade);
}
