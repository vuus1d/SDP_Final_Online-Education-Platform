package com.eduplatform.decorator;

import com.eduplatform.model.Course;
import com.eduplatform.model.Student;
import com.eduplatform.model.Certificate;
import com.eduplatform.model.LearningMaterial;
import java.util.List;
import java.util.Optional;

public interface CourseComponent {
    Course course();
    List<LearningMaterial> materials();
    boolean certificateEnabled();
    Optional<Certificate> issueCertificate(Student student, int progressPercent);
}
