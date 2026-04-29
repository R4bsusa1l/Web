const { test, expect } = require("@playwright/test");
const { uniqueEmail, registerUser } = require("./helpers");

test("user can login after registration", async ({ page }) => {
  const email = uniqueEmail();
  const password = "thisIsAStrongPassword123";
  const nickname = "LoginFlowUser";

  await registerUser(page, email, password, nickname);

  await page.locator('input[name="email"]').fill(email);
  await page.locator('input[name="password"]').fill(password);
  await page.locator('form[action="/login"] button[type="submit"]').click();

  await expect(page).toHaveURL(/\/profile/);
  await expect(page.locator("h1")).toContainText("Your profile");
  await expect(page.locator(".profile-data")).toContainText(email);
});

test("invalid login shows an error", async ({ page }) => {
  await page.goto("/login");
  await page.locator('input[name="email"]').fill("noone@example.com");
  await page.locator('input[name="password"]').fill("wrong-password");
  await page.locator('form[action="/login"] button[type="submit"]').click();

  await expect(page).toHaveURL(/\/login\?error=/);
  await expect(page.locator(".alert.error")).toContainText("Invalid email or password");
});
