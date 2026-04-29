const { expect } = require("@playwright/test");

function uniqueEmail() {
  return `user_${Date.now()}_${Math.floor(Math.random() * 10000)}@example.com`;
}

async function registerUser(page, email, password, nickname) {
  await page.goto("/register");
  await page.locator('input[name="email"]').fill(email);
  await page.locator('input[name="password"]').fill(password);
  await page.locator('input[name="nickname"]').fill(nickname);
  await page.locator('form[action="/register"] button[type="submit"]').click();
  await expect(page).toHaveURL(/\/login\?success=/);
}

module.exports = {
  uniqueEmail,
  registerUser,
};
