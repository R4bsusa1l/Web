const { test, expect } = require("@playwright/test");
const { uniqueEmail, registerUser } = require("./helpers");

test("authenticated user can logout and is redirected to login", async ({ page }) => {
  const email = uniqueEmail();
  const password = "thisIsAStrongPassword123";
  const nickname = "LogoutUser";

  await registerUser(page, email, password, nickname);

  await page.locator('input[name="email"]').fill(email);
  await page.locator('input[name="password"]').fill(password);
  await page.locator('form[action="/login"] button[type="submit"]').click();
  await expect(page).toHaveURL(/\/profile/);

  await page.locator('form[action="/logout"] button[type="submit"]').click();

  await expect(page).toHaveURL(/\/login\?success=Logged\+out/);
  await expect(page.locator(".alert.success")).toContainText("Logged out");
});
