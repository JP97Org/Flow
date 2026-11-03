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

// Reset: delete all modules, then create two separated modules
const chart = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
for (const m of chart.modules ?? []) {
  await page.request.delete(`http://localhost:8080/api/flow/${chart.id}/modules/${m.id}`);
}
const mk = async (x, y) => {
  const r = await page.request.post(`http://localhost:8080/api/flow/${chart.id}/modules`, {
    data: { className: 'org.jojo.flow.model.util.DynamicObjectLoader$MockModule', position: { x, y } },
  });
  return r.json();
};
const a = await mk(2, 2);
const b = await mk(20, 2);
console.log('created:', a.id, a.position, '|', b.id, b.position);

await page.goto('http://localhost:4200/', { waitUntil: 'networkidle' });
await page.waitForTimeout(800);

async function dragCard(cardIndex, dx, dy, { checkPreview = false } = {}) {
  const card = page.locator('.module-card').nth(cardIndex);
  const box = await card.boundingBox();
  const sx = box.x + box.width / 2;
  const sy = box.y + box.height / 2;
  await page.mouse.move(sx, sy);
  await page.mouse.down();
  let previewSeen = false;
  for (let i = 1; i <= 12; i++) {
    await page.mouse.move(sx + (dx * i) / 12, sy + (dy * i) / 12);
    await page.waitForTimeout(25);
    if (checkPreview) {
      const p = await page.locator('.module-preview').count();
      if (p > 0) previewSeen = true;
    }
  }
  await page.mouse.up();
  await page.waitForTimeout(700);
  return { previewSeen };
}

const cardCount = await page.locator('.module-card').count();
console.log('card count:', cardCount);

// 1) valid move of module a by +2 cells x (40px)
reqs.length = 0;
const r1 = await dragCard(0, 40, 0, { checkPreview: true });
const after1 = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('TEST1 valid move previewSeen=', r1.previewSeen, 'pos=', JSON.stringify(after1.modules.map(m => [m.id, m.position])), 'reqs=', reqs.length);

// 2) invalid move: drag module a onto module b (overlap) - use a big dx to reach x=20
reqs.length = 0;
const r2 = await dragCard(0, 340, 0);
const after2 = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('TEST2 overlap pos=', JSON.stringify(after2.modules.map(m => [m.id, m.position])), 'reqs=', reqs.length);

// 3) tiny move (< half cell) should snap back and not emit
reqs.length = 0;
const r3 = await dragCard(0, 5, 5);
const after3 = await page.request.get('http://localhost:8080/api/flow/main').then(r => r.json());
console.log('TEST3 tiny pos=', JSON.stringify(after3.modules.map(m => [m.id, m.position])), 'reqs=', reqs.length);

// 4) verify card visually moved: compare boundingBox left/top vs module pos * 20
const boxes = await page.locator('.module-card').evaluateAll(els => els.map(e => ({ id: e.getAttribute('data-id'), left: e.style.left, top: e.style.top })));
console.log('TEST4 card styles:', JSON.stringify(boxes));

console.log('LOGS:', logs.filter(l => !l.includes('vite') && !l.includes('Angular is running')).join('\n') || '(none)');
await browser.close();
