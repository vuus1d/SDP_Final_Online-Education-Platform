package com.eduplatform.observer;

import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Concrete subject: stores grades and publishes each recorded grade to all subscribed observers.
 * It knows nothing about how observers react (notifications, logging, ...).
 */
public class GradeService implements GradeSubject {

    private final List<GradeObserver> observers = new ArrayList<>();
    private final List<Grade> grades = new ArrayList<>();
    private final List<ObserverFailure> observerFailures = new ArrayList<>();
    private final Set<Grade> publishedGrades = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public synchronized void subscribe(GradeObserver observer) {
        Objects.requireNonNull(observer, "Observer must not be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void unsubscribe(GradeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers(Grade grade) {
        Objects.requireNonNull(grade, "Grade must not be null");
        if (!grades.contains(grade)) {
            throw new IllegalStateException("Only a saved grade can be published");
        }
        if (!publishedGrades.add(grade)) {
            throw new IllegalStateException("This grade event was already published");
        }
        for (GradeObserver observer : List.copyOf(observers)) {
            try {
                observer.onGradePublished(grade);
            } catch (RuntimeException failure) {
                observerFailures.add(new ObserverFailure(grade, observer.getClass().getName(), failure));
            }
        }
    }

    /** Saves the grade and automatically publishes it to every subscribed observer. */
    public synchronized void recordGrade(Grade grade) {
        Objects.requireNonNull(grade, "Grade must not be null");
        if (grades.contains(grade)) throw new IllegalStateException("This grade event was already recorded");
        grades.add(grade);
        notifyObservers(grade);
    }

    public synchronized boolean isSubscribed(GradeObserver observer) {
        return observers.contains(observer);
    }

    public synchronized int getObserverCount() {
        return observers.size();
    }

    /** Returns isolated observer callback failures; a failed notification never rolls back a saved grade. */
    public synchronized List<ObserverFailure> getObserverFailures() {
        return List.copyOf(observerFailures);
    }

    public synchronized List<Grade> getAllGrades() {
        return List.copyOf(grades);
    }

    public synchronized List<Grade> getGradesForStudent(Student student) {
        return grades.stream()
                .filter(grade -> grade.getStudent().equals(student))
                .collect(Collectors.toUnmodifiableList());
    }

    public synchronized List<Grade> getGradesForCourse(Course course) {
        return grades.stream()
                .filter(grade -> grade.getCourse().equals(course))
                .collect(Collectors.toUnmodifiableList());
    }
}
