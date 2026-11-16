export function toXmlBlob(data: unknown): Blob {
  if (data instanceof Blob) {
    return data;
  }
  return new Blob([String(data ?? '')], { type: 'application/xml' });
}

export function downloadXml(data: unknown, fileName: string): void {
  const url = URL.createObjectURL(toXmlBlob(data));
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = fileName;
  anchor.style.display = 'none';
  document.body.appendChild(anchor);
  try {
    anchor.click();
  } finally {
    anchor.remove();
    URL.revokeObjectURL(url);
  }
}
