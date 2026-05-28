import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import './Login.css';

const Login = () => {
  const { t } = useLang();
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [showPw, setShowPw] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Redirect if already logged in
  if (isAuthenticated) {
    navigate('/account', { replace: true });
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    const result = await login(email.trim(), password.trim());
    setLoading(false);
    if (result.success) {
      navigate(result.role === 'ADMIN' ? '/admin' : '/account');
    } else {
      setError(result.error);
    }
  };

  return (
    <div className="auth-wrap">
      {/* Breadcrumb */}
      <div className="auth-breadcrumb">
        <div className="container auth-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('login.breadcrumb')}</span>
        </div>
      </div>

      {/* Form */}
      <div className="auth-section">
        <div className="auth-card">
          <h2 className="auth-title">{t('login.title')}</h2>

          {error && <div className="auth-error">{t(error)}</div>}

          <form onSubmit={handleSubmit}>
            <div className="auth-field">
              <input
                type="email"
                placeholder={t('login.email')}
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="auth-field auth-field-pw">
              <input
                type={showPw ? 'text' : 'password'}
                placeholder={t('login.password')}
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
              <button
                type="button"
                className="auth-pw-toggle"
                onClick={() => setShowPw(!showPw)}
              >
                {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            <div className="auth-extras">
              <label className="auth-checkbox">
                <input type="checkbox" />
                <span>{t('login.remember')}</span>
              </label>
              <Link to="/forgot-password" className="auth-forgot">{t('login.forgot')}</Link>
            </div>

            <button type="submit" className="auth-submit-btn" disabled={loading}>
              {loading ? 'Đang đăng nhập...' : t('login.submit')}
            </button>
          </form>

          <p className="auth-switch">
            {t('login.noAccount')}{' '}
            <Link to="/register" className="auth-switch-link">
              {t('login.register')}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
