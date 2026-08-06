package com.indiandesigns.fabcut.di.module;

import android.app.Service;
import dagger.Module;

/**
 * Define classes and methods which provide dependencies
 */

@Module
public class ServiceModule {

    private final Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }
}
