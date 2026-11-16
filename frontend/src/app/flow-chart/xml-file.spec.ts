import { afterEach, describe, expect, it, vi } from 'vitest';
import { downloadXml, toXmlBlob } from './xml-file';

describe('xml-file', () => {
  afterEach(() => {
    vi.restoreAllMocks();
    vi.unstubAllGlobals();
  });

  it('wraps text in an XML blob', async () => {
    const blob = toXmlBlob('<flow/>');

    expect(blob).toBeInstanceOf(Blob);
    expect(blob.type).toBe('application/xml');
    expect(await blob.text()).toBe('<flow/>');
  });

  it('keeps existing blobs', () => {
    const blob = new Blob(['<flow/>'], { type: 'text/xml' });

    expect(toXmlBlob(blob)).toBe(blob);
  });

  it('downloads the XML with the given filename', () => {
    const createObjectURL = vi.fn((_blob: Blob): string => 'blob:flow');
    const revokeObjectURL = vi.fn((_url: string): void => {});
    vi.stubGlobal('URL', { createObjectURL, revokeObjectURL } as unknown as typeof URL);
    let clicked: HTMLAnchorElement | null = null;
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (
      this: HTMLAnchorElement,
    ) {
      clicked = this;
    });

    downloadXml('<flow/>', 'flow-chart-7.xml');

    expect(createObjectURL).toHaveBeenCalledOnce();
    expect(createObjectURL.mock.calls[0][0]).toBeInstanceOf(Blob);
    expect(clicked).not.toBeNull();
    expect(clicked!.download).toBe('flow-chart-7.xml');
    expect(revokeObjectURL).toHaveBeenCalledWith('blob:flow');
  });
});
