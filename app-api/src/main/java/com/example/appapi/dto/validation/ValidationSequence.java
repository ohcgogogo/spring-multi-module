package com.example.appapi.dto.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, ValidationGroups.NotEmptyGroup.class, ValidationGroups.LengthCheckGroup.class, ValidationGroups.PatternCheckGroup.class})
public interface ValidationSequence {
}
