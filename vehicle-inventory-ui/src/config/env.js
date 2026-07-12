export const config = Object.freeze({
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
  APP_NAME: import.meta.env.VITE_APP_NAME,
  IS_PRODUCTION: import.meta.env.PROD,
  IS_DEVELOPMENT: import.meta.env.DEV,
  ENABLE_MOCK_API: import.meta.env.VITE_ENABLE_MOCK_API === 'true',
});
