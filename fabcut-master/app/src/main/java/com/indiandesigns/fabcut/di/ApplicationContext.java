package com.indiandesigns.fabcut.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Used by Dagger to distinguish between same return types while providing
 *
 * Return type of provideContext() in {@link com.indiandesigns.fabcut.di.module.ApplicationModule}
 * clashes with provideContext() in {@link com.indiandesigns.fabcut.di.module.ActivityModule}
 *
 * Both return Context
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationContext {
}
