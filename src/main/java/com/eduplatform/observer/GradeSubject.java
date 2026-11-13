package com.eduplatform.observer;

import com.eduplatform.model.Grade;

/** Subject: keeps a list of observers and notifies them about published grades. */
public interface GradeSubject {

    void subscribe(GradeObserver observer);

    void unsubscribe(GradeObserver observer);

    void notifyObservers(Grade grade);
}
