package com.indiandesigns.fabcut.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Used by Dagger to distinguish between same return types while providing
 *
 * Return type of both provideDatabaseName() and providePreferenceName()
 * in {@link com.indiandesigns.fabcut.di.module.ActivityModule} is String
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface PreferenceInfo {
}
