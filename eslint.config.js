// ESLint flat config (Expo SDK 53+ style)
const { defineConfig } = require('eslint/config');
const expoConfig = require('eslint-config-expo/flat');
const prettier = require('eslint-plugin-prettier/recommended');

module.exports = defineConfig([
  expoConfig,
  prettier,
  {
    settings: {
      react: { version: 'detect' },
    },
  },
  {
    ignores: ['dist/*', 'android/*', 'ios/*', '.expo/*', 'coverage/*', 'modules/**/android/*'],
  },
  {
    // eslint-plugin-react 7.x crashes on ESLint 10 flat config — disable until expo fixes it
    files: ['**/*.{ts,tsx}'],
    rules: Object.fromEntries(
      Object.keys(require('eslint-plugin-react').rules).map((rule) => [`react/${rule}`, 'off'])
    ),
  },
  {
    files: ['**/*.{ts,tsx}'],
    rules: {
      // Downgrade to warn — these are valid patterns in React Native (derived state in effects)
      'react-hooks/set-state-in-effect': 'warn',
      'react-hooks/immutability': 'warn',

      // Keep as warn — clean up console.logs gradually
      'no-console': ['warn', { allow: ['warn', 'error'] }],

      // Keep as warn — any types are acceptable during active development
      '@typescript-eslint/no-explicit-any': 'warn',

      // Keep as warn — unused vars should be cleaned up but don't block builds
      '@typescript-eslint/no-unused-vars': 'warn',

      // Off — false positive for axios default import
      'import/no-named-as-default-member': 'off',
    },
  },
]);
