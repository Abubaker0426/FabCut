package com.indiandesigns.fabcut.di.component;

import android.app.Application;
import android.content.Context;
import com.indiandesigns.fabcut.MvpApp;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.di.ApplicationContext;
import com.indiandesigns.fabcut.di.module.ApplicationModule;
import com.indiandesigns.fabcut.service.SyncService;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Enables selected modules and uses them for performing dependency injection
 * Only instantiates once using {@link Singleton}
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MvpApp app);

    void inject(SyncService service);


    @ApplicationContext
    Context context();

    Application application();

    DataManager getDataManager();
}