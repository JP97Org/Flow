import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideApi } from './api';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideApi('')],
    })
      .compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Angular client for the Quarkus API');
  });

  it('should upload a module jar and merge the loaded module classes', async () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    const http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();

    http.expectOne((request) => request.url.endsWith('/health')).flush({
      status: 'UP',
      application: 'flow',
      version: '1.0.0',
    });
    http
      .expectOne((request) => request.url.endsWith('/module-classes'))
      .flush([{ className: 'existing.Module', displayName: 'Existing', builtIn: true }]);
    http.expectOne((request) => request.method === 'GET' && request.url.endsWith('/main')).flush({
      id: 0,
      modules: [],
      connections: [],
    });
    await fixture.whenStable();

    const file = new File(['jar'], 'modules.jar', { type: 'application/java-archive' });
    (app as unknown as { onModuleJarSelected(event: Event): void }).onModuleJarSelected({
      target: { files: [file], value: '' },
    } as unknown as Event);

    const upload = http.expectOne(
      (request) => request.method === 'POST' && request.url.endsWith('/api/flow/module-jars'),
    );
    expect(upload.request.body).toBeInstanceOf(FormData);
    upload.flush({
      jarFileName: 'modules.jar',
      moduleClasses: [{ className: 'dynamic.Module', displayName: 'Dynamic', builtIn: false }],
    });
    await fixture.whenStable();

    const classes = (
      app as unknown as { moduleClasses(): { className?: string }[] }
    ).moduleClasses();
    expect(classes.map((moduleClass) => moduleClass.className)).toContain('dynamic.Module');
    expect(classes.map((moduleClass) => moduleClass.className)).toContain('existing.Module');
  });
});
