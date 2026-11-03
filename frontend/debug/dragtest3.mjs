import { chromium } from 'playwright';

const browser = await chromium.launch({
  executablePath: '/usr/bin/chromium',
  args: ['--no-sandbox', '--disable-dev-shm-usage'],
});
const page = await browser.newPage({ viewport: { width: 1400, height: 900 } });

const reqs = [];
page.on('request', r => {
  if (['PUT', 'POST', 'DELETE'].includes(r.method())) reqs.push(`${r.method()} ${r.url()} ${r.postData() ?? ''}`);
});
const logs = [];
page.on('console', m => logs.push(`[${m.type()}] ${m.text()}`));
page.on('pageerror', e => logs.push(`[PAGEERROR] ${e.message}`));

// clean slate
const chart = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
for (const m of chart.modules ?? []) {
  await page.request.delete(`http://localhost:8080/api/flow/${chart.id}/modules/${m.id}`);
}

await page.goto('http://localhost:4200/', { waitUntil: 'networkidle' });
await page.waitForTimeout(800);

const cardsBefore = await page.locator('.module-card').count();
console.log('cards before palette drag:', cardsBefore);

// drag from palette to grid
const palette = page.locator('app-module-palette [cdkDrag]').first();
const pbox = await palette.boundingBox();
const canvas = page.locator('.grid-canvas');
const cbox = await canvas.boundingBox();
console.log('palette box:', JSON.stringify(pbox), 'canvas box:', JSON.stringify(cbox));

const sx = pbox.x + pbox.width / 2;
const sy = pbox.y + pbox.height / 2;
const tx = cbox.x + 120; // cell 6
const ty = cbox.y + 120; // cell 6
await page.mouse.move(sx, sy);
await page.mouse.down();
for (let i = 1; i <= 15; i++) {
  await page.mouse.move(sx + ((tx - sx) * i) / 15, sy + ((ty - sy) * i) / 15);
  await page.waitForTimeout(20);
}
await page.mouse.up();
await page.waitForTimeout(1000);

const cardsAfterDrop = await page.locator('.module-card').count();
const afterDrop = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('cards after palette drop:', cardsAfterDrop, 'positions:', JSON.stringify(afterDrop.modules.map(m => [m.id, m.position])));
console.log('DROP reqs:', reqs.join('\n') || '(none)');
reqs.length = 0;

// Now drag the newly dropped module inside the grid
const card = page.locator('.module-card').first();
const box = await card.boundingBox();
console.log('dropped card box:', JSON.stringify(box));
const cx = box.x + box.width / 2;
const cy = box.y + box.height / 2;
await page.mouse.move(cx, cy);
await page.mouse.down();
for (let i = 1; i <= 12; i++) {
  await page.mouse.move(cx + (60 * i) / 12, cy + (40 * i) / 12);
  await page.waitForTimeout(25);
}
await page.mouse.up();
await page.waitForTimeout(1000);

const afterMove = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('after moving dropped module:', JSON.stringify(afterMove.modules.map(m => [m.id, m.position])));
console.log('MOVE reqs:', reqs.join('\n') || '(none)');
console.log('LOGS:', logs.filter(l => !l.includes('vite') && !l.includes('Angular is running')).join('\n') || '(none)');

await browser.close();
