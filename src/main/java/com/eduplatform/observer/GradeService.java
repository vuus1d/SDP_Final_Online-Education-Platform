package com.eduplatform.observer;

import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Concrete subject: stores grades and publishes each recorded grade to all subscribed observers.
 * It knows nothing about how observers react (notifications, logging, ...).
 */
public class GradeService implements GradeSubject {

    private final List<GradeObserver> observers = new ArrayList<>();
    private final List<Grade> grades = new ArrayList<>();

    @Override
    public void subscribe(GradeObserver observer) {
        Objects.requireNonNull(observer, "Observer must not be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(GradeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Grade grade) {
        Objects.requireNonNull(grade, "Grade must not be null");
        for (GradeObserver observer : List.copyOf(observers)) {
            observer.onGradePublished(grade);
        }
    }

    /** Saves the grade and automatically publishes it to every subscribed observer. */
    public void recordGrade(Grade grade) {
        Objects.requireNonNull(grade, "Grade must not be null");
        grades.add(grade);
        notifyObservers(grade);
    }

    public boolean isSubscribed(GradeObserver observer) {
        return observers.contains(observer);
    }

    public int getObserverCount() {
        return observers.size();
    }

    public List<Grade> getAllGrades() {
        return List.copyOf(grades);
    }

    public List<Grade> getGradesForStudent(Student student) {
        return grades.stream()
                .filter(grade -> grade.getStudent().equals(student))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Grade> getGradesForCourse(Course course) {
        return grades.stream()
                .filter(grade -> grade.getCourse().equals(course))
                .collect(Collectors.toUnmodifiableList());
    }
}
