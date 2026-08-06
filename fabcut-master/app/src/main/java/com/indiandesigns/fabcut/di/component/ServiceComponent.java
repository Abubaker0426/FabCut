package com.indiandesigns.fabcut.di.component;

import com.indiandesigns.fabcut.di.PerService;
import com.indiandesigns.fabcut.di.module.ServiceModule;
import com.indiandesigns.fabcut.service.SyncService;
import dagger.Component;

/**
 * Enables selected modules and uses them for performing dependency injection
 * Scope defined by {@link PerService}
 */

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);

}
