package edu.platform.course;

import edu.platform.domain.LearningMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Adds materials without modifying the original Course or any other component. */
public final class AdditionalMaterialsDecorator extends CourseDecorator {
    private final List<LearningMaterial> additionalMaterials;
    public AdditionalMaterialsDecorator(CourseComponent delegate, List<LearningMaterial> materials) {
        super(delegate);
        Objects.requireNonNull(materials, "materials must not be null");
        if (materials.isEmpty()) throw new IllegalArgumentException("at least one additional material is required");
        this.additionalMaterials = List.copyOf(materials);
        if (this.additionalMaterials.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("materials must not contain null");
    }
    @Override public List<LearningMaterial> materials() {
        List<LearningMaterial> combined = new ArrayList<>(super.materials());
        combined.addAll(additionalMaterials);
        return List.copyOf(combined);
    }
}
