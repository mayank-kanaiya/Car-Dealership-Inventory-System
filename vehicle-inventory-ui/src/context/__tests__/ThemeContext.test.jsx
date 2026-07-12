import { render, screen, act } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { ThemeProvider, useTheme } from '../ThemeContext';

function TestComponent() {
  const { theme, toggleTheme, isDark } = useTheme();
  return (
    <div>
      <span data-testid="theme">{theme}</span>
      <span data-testid="isDark">{String(isDark)}</span>
      <button onClick={toggleTheme}>Toggle</button>
    </div>
  );
}

describe('ThemeContext', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove('dark');
  });

  it('defaults to light theme', () => {
    render(
      <ThemeProvider>
        <TestComponent />
      </ThemeProvider>
    );
    expect(screen.getByTestId('theme')).toHaveTextContent('light');
    expect(screen.getByTestId('isDark')).toHaveTextContent('false');
  });

  it('toggles to dark theme', () => {
    render(
      <ThemeProvider>
        <TestComponent />
      </ThemeProvider>
    );
    act(() => {
      screen.getByRole('button', { name: /toggle/i }).click();
    });
    expect(screen.getByTestId('theme')).toHaveTextContent('dark');
    expect(screen.getByTestId('isDark')).toHaveTextContent('true');
    expect(document.documentElement.classList.contains('dark')).toBe(true);
  });

  it('persists theme in localStorage', () => {
    render(
      <ThemeProvider>
        <TestComponent />
      </ThemeProvider>
    );
    act(() => {
      screen.getByRole('button', { name: /toggle/i }).click();
    });
    expect(localStorage.getItem('theme')).toBe('dark');
  });

  it('restores theme from localStorage on mount', () => {
    localStorage.setItem('theme', 'dark');
    render(
      <ThemeProvider>
        <TestComponent />
      </ThemeProvider>
    );
    expect(screen.getByTestId('theme')).toHaveTextContent('dark');
    expect(document.documentElement.classList.contains('dark')).toBe(true);
  });

  it('throws when useTheme is used outside provider', () => {
    const spy = vi.spyOn(console, 'error').mockImplementation(() => {});
    function BadComponent() {
      useTheme();
      return null;
    }
    expect(() => render(<BadComponent />)).toThrow(/useTheme must be used within a ThemeProvider/i);
    spy.mockRestore();
  });
});
