import { readFileSync } from 'node:fs';
import { test as baseTest, expect, Page } from '@playwright/test';
import { createMockOidcUser, injectAuthSession } from './auth';
import { setupMockApi, MockApiOptions } from './mockApi';

// The app's base path differs per deployment (ALA serves at "/", VBP at
// "/lists/"); the dev server reads it from config/.env.development, and the
// specs use it to build absolute page URLs.
const envFile = readFileSync(new URL('../../config/.env.development', import.meta.url), 'utf8');
export const basePath = envFile.match(/^VITE_BASE_PATH=(.*)$/m)?.[1]?.trim() || '/';
export const appUrl = (path: string) => `${basePath}${path.replace(/^\//, '')}`;


export interface TestFixtures {
  setupMocks: (options?: MockApiOptions) => Promise<void>;
  authenticatedPage: Page;
  adminPage: Page;
}

export const test = baseTest.extend<TestFixtures>({
  page: async ({ page }, use) => {
    // The VBP branding package defaults the UI language to Dutch via the
    // "vbp-lang" cookie; force English so role/label selectors stay stable
    // across the ALA and VBP deployments.
    await page.context().addCookies([
      { name: 'vbp-lang', value: 'en', domain: 'localhost', path: '/' },
    ]);
    // Set up mock API by default for any test page
    await setupMockApi(page);
    await use(page);
  },
  setupMocks: async ({ page }, use) => {
    await use(async (options?: MockApiOptions) => {
      await setupMockApi(page, options);
    });
  },
  authenticatedPage: async ({ page }, use) => {
    await injectAuthSession(page, createMockOidcUser({ roles: ['user'] }));
    await setupMockApi(page);
    await use(page);
  },
  adminPage: async ({ page }, use) => {
    await injectAuthSession(page, createMockOidcUser({ roles: ['admin'] }));
    await setupMockApi(page);
    await use(page);
  },
});

export { expect };
