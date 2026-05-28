import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import Login from '../pages/Login';
import { AuthProvider } from '../context/AuthContext';
import { LanguageProvider } from '../context/LanguageContext';
import { authApi } from '../api/authApi';
import { userApi } from '../api/userApi';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock('../api/authApi', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    refresh: vi.fn(),
    forgotPassword: vi.fn(),
    verifyOtpResetPassword: vi.fn(),
    resetPassword: vi.fn(),
  },
}));

vi.mock('../api/userApi', () => ({
  userApi: { getProfile: vi.fn() },
}));

function renderLogin() {
  return render(
    <MemoryRouter>
      <LanguageProvider>
        <AuthProvider>
          <Login />
        </AuthProvider>
      </LanguageProvider>
    </MemoryRouter>
  );
}

const emailInput = () => screen.getByPlaceholderText('Email');
const passwordInput = () => screen.getByPlaceholderText('Mật khẩu');
const submitButton = () => screen.getByRole('button', { name: /^đăng nhập$|đang đăng nhập/i });

describe('Login - validateEmail', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders an email field that is required and uses type="email"', () => {
    renderLogin();
    const email = emailInput();
    expect(email).toBeInTheDocument();
    expect(email).toBeRequired();
    expect(email).toHaveAttribute('type', 'email');
  });

  it('trims whitespace around the email before submitting', async () => {
    authApi.login.mockResolvedValue({ accessToken: 'a', refreshToken: 'r' });
    userApi.getProfile.mockResolvedValue({ id: 1, email: 't@x.com', role: 'USER' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: '   t@x.com   ' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      expect(authApi.login).toHaveBeenCalledWith('t@x.com', 'pw');
    });
  });
});

describe('Login - validatePassword', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders a password field that is required and uses type="password" by default', () => {
    renderLogin();
    const pw = passwordInput();
    expect(pw).toBeRequired();
    expect(pw).toHaveAttribute('type', 'password');
  });

  it('toggles password visibility when the eye button is clicked', () => {
    renderLogin();
    const pw = passwordInput();
    // The toggle button is the only icon-only button next to the password field
    const toggle = pw.parentElement.querySelector('.auth-pw-toggle');
    expect(pw).toHaveAttribute('type', 'password');
    fireEvent.click(toggle);
    expect(pw).toHaveAttribute('type', 'text');
    fireEvent.click(toggle);
    expect(pw).toHaveAttribute('type', 'password');
  });

  it('trims whitespace around the password before submitting', async () => {
    authApi.login.mockResolvedValue({ accessToken: 'a', refreshToken: 'r' });
    userApi.getProfile.mockResolvedValue({ id: 1, role: 'USER' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'a@b.com' } });
    fireEvent.change(passwordInput(), { target: { value: '  secret  ' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      expect(authApi.login).toHaveBeenCalledWith('a@b.com', 'secret');
    });
  });
});

describe('Login - loading state (isLoading)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('disables the submit button and shows "Đang đăng nhập..." while the request is pending', async () => {
    let resolveLogin;
    authApi.login.mockImplementation(
      () => new Promise((resolve) => {
        resolveLogin = resolve;
      })
    );
    userApi.getProfile.mockResolvedValue({ id: 1, role: 'USER' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'a@b.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      const btn = submitButton();
      expect(btn).toBeDisabled();
      expect(btn).toHaveTextContent(/đang đăng nhập/i);
    });

    // Let the pending request finish so the test exits cleanly.
    resolveLogin({ accessToken: 'a', refreshToken: 'r' });
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/account'));
  });
});

describe('Login - error state (isError)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows an error message when authApi.login rejects', async () => {
    authApi.login.mockRejectedValue(new Error('Sai email hoặc mật khẩu'));

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'a@b.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'wrong' } });
    fireEvent.click(submitButton());

    const errorBox = await screen.findByText(/sai email hoặc mật khẩu/i);
    expect(errorBox).toBeInTheDocument();
    expect(errorBox.className).toMatch(/auth-error/);
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  it('falls back to "Login failed" when the error has no message', async () => {
    authApi.login.mockRejectedValue({});

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'a@b.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    expect(await screen.findByText(/login failed/i)).toBeInTheDocument();
  });
});

describe('Login - JWT response handling', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('stores accessToken and refreshToken in localStorage on success', async () => {
    authApi.login.mockResolvedValue({
      accessToken: 'jwt-access-123',
      refreshToken: 'jwt-refresh-456',
    });
    userApi.getProfile.mockResolvedValue({ id: 1, role: 'USER' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'a@b.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      expect(localStorage.getItem('accessToken')).toBe('jwt-access-123');
      expect(localStorage.getItem('refreshToken')).toBe('jwt-refresh-456');
    });
  });

  it('navigates a normal user to /account after successful login', async () => {
    authApi.login.mockResolvedValue({ accessToken: 'a', refreshToken: 'r' });
    userApi.getProfile.mockResolvedValue({ id: 1, role: 'USER' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'u@e.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/account');
    });
  });

  it('navigates an admin user to /admin after successful login', async () => {
    authApi.login.mockResolvedValue({ accessToken: 'a', refreshToken: 'r' });
    userApi.getProfile.mockResolvedValue({ id: 9, role: 'ADMIN' });

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'admin@e.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/admin');
    });
  });

  it('does not store any token when login fails', async () => {
    authApi.login.mockRejectedValue(new Error('nope'));

    renderLogin();
    fireEvent.change(emailInput(), { target: { value: 'u@e.com' } });
    fireEvent.change(passwordInput(), { target: { value: 'pw' } });
    fireEvent.click(submitButton());

    await screen.findByText(/nope/i);
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(localStorage.getItem('refreshToken')).toBeNull();
  });
});
