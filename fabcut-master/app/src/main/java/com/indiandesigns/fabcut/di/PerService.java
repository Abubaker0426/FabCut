package com.indiandesigns.fabcut.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Defines a new scope for Dagger
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerService {
}

