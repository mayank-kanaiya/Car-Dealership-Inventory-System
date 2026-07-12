import js from '@eslint/js';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import jsxA11yPlugin from 'eslint-plugin-jsx-a11y';
import prettierConfig from 'eslint-config-prettier';

export default [
  js.configs.recommended,
  {
    files: ['**/*.{js,jsx}'],
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
      'jsx-a11y': jsxA11yPlugin,
    },
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        document: 'readonly',
        window: 'readonly',
        console: 'readonly',
        URL: 'readonly',
        crypto: 'readonly',
        HTMLInputElement: 'readonly',
        localStorage: 'readonly',
        sessionStorage: 'readonly',
        btoa: 'readonly',
        atob: 'readonly',
        navigator: 'readonly',
      },
    },
    rules: {
      'react/jsx-uses-react': 'error',
      'react/jsx-uses-vars': 'error',
      'react/prop-types': 'warn',
      'react/jsx-no-target-blank': 'error',
      'no-console': ['warn', { allow: ['error', 'warn'] }],
      'no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
  },
  {
    files: ['src/setupTests.js', 'src/mocks/**/*.{js,jsx}', 'src/**/__tests__/**/*.{js,jsx}', 'src/**/*.{test,spec}.{js,jsx}'],
    languageOptions: {
      globals: {
        beforeAll: 'readonly',
        afterAll: 'readonly',
        beforeEach: 'readonly',
        afterEach: 'readonly',
        describe: 'readonly',
        it: 'readonly',
        expect: 'readonly',
        vi: 'readonly',
      },
    },
  },
  prettierConfig,
];
