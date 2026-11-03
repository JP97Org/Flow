import { chromium } from 'playwright';

const browser = await chromium.launch({
  executablePath: '/usr/bin/chromium',
  args: ['--no-sandbox', '--disable-dev-shm-usage'],
});
const page = await browser.newPage({ viewport: { width: 1400, height: 900 } });

const logs = [];
page.on('console', m => logs.push(`[${m.type()}] ${m.text()}`));
page.on('pageerror', e => logs.push(`[PAGEERROR] ${e.message}`));

const chart = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
for (const m of chart.modules ?? []) {
  await page.request.delete(`http://localhost:8080/api/flow/${chart.id}/modules/${m.id}`);
}

await page.goto('http://localhost:4200/', { waitUntil: 'networkidle' });
await page.waitForTimeout(800);

// Instrument: count CDK drag events and preview presence
await page.evaluate(() => {
  window.__events = { paletteStart: 0, paletteMoved: 0, paletteEnded: 0 };
  document.addEventListener('cdkDragStarted', () => { window.__events.paletteStart++; }, true);
});

const palette = page.locator('app-module-palette [cdkDrag]').first();
const pbox = await palette.boundingBox();
const canvas = page.locator('.grid-canvas');
const cbox = await canvas.boundingBox();
console.log('palette box:', JSON.stringify(pbox), 'canvas box:', JSON.stringify(cbox));

const sx = pbox.x + pbox.width / 2;
const sy = pbox.y + pbox.height / 2;
const tx = cbox.x + 130;
const ty = cbox.y + 130;
await page.mouse.move(sx, sy);
await page.mouse.down();
let previewSeen = false;
let previewBoxes = [];
for (let i = 1; i <= 20; i++) {
  const mx = sx + ((tx - sx) * i) / 20;
  const my = sy + ((ty - sy) * i) / 20;
  await page.mouse.move(mx, my);
  await page.waitForTimeout(25);
  const p = page.locator('.module-preview');
  if (await p.count() > 0) {
    previewSeen = true;
    previewBoxes.push(await p.first().boundingBox());
  }
}
// read drag class on palette element
const paletteDragClass = await palette.getAttribute('class');
console.log('palette class during drag:', paletteDragClass);
console.log('previewSeen:', previewSeen, 'preview sample:', JSON.stringify(previewBoxes.slice(-2)));
await page.mouse.up();
await page.waitForTimeout(1000);

const after = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('positions after drop:', JSON.stringify(after.modules.map(m => [m.id, m.position])));
const cards = await page.locator('.module-card').count();
console.log('cards:', cards);
console.log('LOGS:', logs.filter(l => !l.includes('vite') && !l.includes('Angular is running')).join('\n') || '(none)');
await browser.close();
