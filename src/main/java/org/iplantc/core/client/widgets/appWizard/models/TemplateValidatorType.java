package org.iplantc.core.client.widgets.appWizard.models;

import com.sencha.gxt.widget.core.client.form.validator.MaxNumberValidator;
import com.sencha.gxt.widget.core.client.form.validator.MinNumberValidator;

public enum TemplateValidatorType {
    IntRange, /** {@link MinNumberValidator} and {@link MaxNumberValidator} */
    IntAbove, /** {@link MinNumberValidator} */
    IntBelow, /** {@link MaxNumberValidator} */
    DoubleRange, /** {@link MinNumberValidator} and {@link MaxNumberValidator} */
    DoubleAbove, /** {@link MinNumberValidator} */
    DoubleBelow, /** {@link MaxNumberValidator} */
    NonEmptyClass,
    GenotypeName,
    FileName,
    IntBelowField,
    IntAboveField,
    ClipperData,
    MustContain,
    SelectOneCheckbox,
    Regex, // TEXT
    CharacterLimit; // TEXT
    
}
