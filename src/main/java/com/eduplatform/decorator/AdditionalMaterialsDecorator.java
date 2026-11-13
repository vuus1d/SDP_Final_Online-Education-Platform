package com.eduplatform.decorator;

import com.eduplatform.model.LearningMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AdditionalMaterialsDecorator extends CourseDecorator {
    private final List<LearningMaterial> extras;
    public AdditionalMaterialsDecorator(CourseComponent delegate, List<LearningMaterial> materials) {
        super(delegate);
        Objects.requireNonNull(materials);
        if (materials.isEmpty() || materials.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("At least one valid material is required");
        extras = List.copyOf(materials);
    }
    public List<LearningMaterial> materials() {
        List<LearningMaterial> result = new ArrayList<>(super.materials());
        result.addAll(extras);
        return List.copyOf(result);
    }
}
