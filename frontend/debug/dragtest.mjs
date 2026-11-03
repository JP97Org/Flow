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

// ensure at least one module exists
const chart = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('modules at start:', JSON.stringify(chart.modules));

if (!chart.modules || chart.modules.length === 0) {
  const res = await page.request.post(`http://localhost:8080/api/flow/${chart.id}/modules`, {
    data: { className: 'org.jojo.flow.model.util.DynamicObjectLoader$MockModule', position: { x: 5, y: 5 } },
  });
  console.log('created module:', res.status(), JSON.stringify(await res.json()));
}

await page.goto('http://localhost:4200/', { waitUntil: 'networkidle' });
await page.waitForTimeout(800);

const card = page.locator('.module-card').first();
const count = await page.locator('.module-card').count();
console.log('module-card count:', count);
if (count === 0) { console.log('NO CARDS'); await browser.close(); process.exit(1); }

const box = await card.boundingBox();
console.log('card box:', JSON.stringify(box));

// Drag the card by +60px x, +40px y
const sx = box.x + box.width / 2;
const sy = box.y + box.height / 2;
await page.mouse.move(sx, sy);
await page.mouse.down();
for (let i = 1; i <= 10; i++) {
  await page.mouse.move(sx + (60 * i) / 10, sy + (40 * i) / 10);
  await page.waitForTimeout(20);
}
await page.mouse.up();
await page.waitForTimeout(800);

const after = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('modules after drag:', JSON.stringify(after.modules?.map(m => ({ id: m.id, pos: m.position }))));
console.log('REQUESTS:', reqs.join('\n') || '(none)');
console.log('LOGS:', logs.join('\n') || '(none)');

await browser.close();
