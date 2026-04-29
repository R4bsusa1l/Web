const { test, expect } = require("@playwright/test");
const { uniqueEmail } = require("./helpers");

test("user can register with email/password", async ({ page }) => {
  const email = uniqueEmail();
  const password = "thisIsAStrongPassword123";
  const nickname = "TestUser";

  await page.goto("/register");

  await page.locator('input[name="email"]').fill(email);
  await page.locator('input[name="password"]').fill(password);
  await page.locator('input[name="nickname"]').fill(nickname);
  await page.locator('form[action="/register"] button[type="submit"]').click();

  await expect(page).toHaveURL(/\/login\?success=/);
  await expect(page.locator(".alert.success")).toContainText("Account created");
});
