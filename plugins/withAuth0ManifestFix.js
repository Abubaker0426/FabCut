const { withAndroidManifest } = require('@expo/config-plugins');

module.exports = function withAuth0ManifestFix(config) {
  return withAndroidManifest(config, (config) => {
    const application = config.modResults.manifest.application[0];
    const activities  = application.activity ?? [];

    // 1. Remove com.auth0.android.provider.RedirectActivity entirely
    application.activity = activities.filter(
      (a) => a.$?.['android:name'] !== 'com.auth0.android.provider.RedirectActivity'
    );

    // 2. Add xyza:// intent-filter to MainActivity
    const mainActivity = application.activity.find(
      (a) => a.$?.['android:name'] === '.MainActivity'
    );
    if (mainActivity) {
      const filters = mainActivity['intent-filter'] ?? [];
      // Avoid duplicates
      const alreadyAdded = filters.some((f) =>
        (f.data ?? []).some((d) => d.$?.['android:scheme'] === 'xyza')
      );
      if (!alreadyAdded) {
        filters.push({
          action:   [{ $: { 'android:name': 'android.intent.action.VIEW' } }],
          category: [
            { $: { 'android:name': 'android.intent.category.DEFAULT' } },
            { $: { 'android:name': 'android.intent.category.BROWSABLE' } },
          ],
          data: [{
            $: {
              'android:scheme':     'xyza',
              'android:host':       'dev-q9u-izlr.auth0.com',
              'android:pathPrefix': '/android/com.idflo.FabCut/callback',
            },
          }],
        });
        mainActivity['intent-filter'] = filters;
      }
    }

    return config;
  });
};
