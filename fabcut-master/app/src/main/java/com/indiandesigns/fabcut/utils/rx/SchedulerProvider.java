package com.indiandesigns.fabcut.utils.rx;

import io.reactivex.Scheduler;

/**
 * Retrieves Schedulers on demand and as needed
 */

public interface SchedulerProvider {

    Scheduler ui();

    Scheduler computation();

    Scheduler io();

}
