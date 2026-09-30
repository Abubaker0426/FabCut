/**
 * clean.js
 * Wipes all caches that cause "missing default export" and worklets errors.
 * Run with: node scripts/clean.js
 */
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const root = path.join(__dirname, '..');

const dirs = [
  path.join(root, '.expo'),
  path.join(root, 'node_modules', '.cache'),
];

console.log('🧹 Cleaning caches...');

dirs.forEach((dir) => {
  if (fs.existsSync(dir)) {
    fs.rmSync(dir, { recursive: true, force: true });
    console.log(`  ✓ Removed ${path.relative(root, dir)}`);
  } else {
    console.log(`  – ${path.relative(root, dir)} (not found, skipping)`);
  }
});

console.log('✅ Done. Run: npx expo start --clear');
