import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Mail, CheckCircle } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import './Register.css';

// Step: 'form' → 'done'

const EMAIL_PATTERN = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)+$/;

const Register = () => {
  const { t } = useLang();
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [step, setStep] = useState('form');
  const [showPw, setShowPw] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPw, setConfirmPw] = useState('');
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    navigate('/account', { replace: true });
    return null;
  }

  // ── Step 1: Register ──────────────────────────────────────────
  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    if (!EMAIL_PATTERN.test(email.trim())) {
      setError('invalid.general.format');
      return;
    }
    if (!acceptedTerms) {
      setError('register.termsRequired');
      return;
    }
    if (password !== confirmPw) {
      setError('register.pwMismatch');
      return;
    }
    setLoading(true);
    const result = await register({
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      email: email.trim(),
      password,
      reEnterPassword: confirmPw,
      acceptedTerms,
    });
    setLoading(false);
    if (result.success) {
      setStep('done');
    } else {
      setError(result.error);
    }
  };

  const STEPS = ['form', 'done'];
  const stepIndex = STEPS.indexOf(step);

  const StepBar = () => (
    <div className="reg-steps-bar">
      {[
        { icon: <Mail size={15}/>, label: t('register.stepInfo') || 'Thông tin' },
        { icon: <CheckCircle size={15}/>, label: t('register.stepDone') || 'Hoàn tất' },
      ].map((s, i) => (
        <div key={i} className={`reg-step ${i < stepIndex ? 'done' : i === stepIndex ? 'active' : ''}`}>
          <div className="reg-step-circle">
            {i < stepIndex ? '✓' : s.icon}
          </div>
          <span className="reg-step-label">{s.label}</span>
          {i < 1 && <div className={`reg-step-line ${i < stepIndex ? 'done' : ''}`} />}
        </div>
      ))}
    </div>
  );

  return (
    <div className="auth-wrap">
      {/* Breadcrumb */}
      <div className="auth-breadcrumb">
        <div className="container auth-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('register.breadcrumb')}</span>
        </div>
      </div>

      <div className="auth-section">
        <div className="auth-card reg-card">

          {/* Step indicator */}
          <StepBar />

          {/* ── Done ── */}
          {step === 'done' && (
            <div className="reg-done">
              <div className="reg-done-icon">
                <CheckCircle size={48} strokeWidth={1.8} />
              </div>
              <h2 className="auth-title" style={{ marginBottom: 10 }}>
                {t('register.doneTitle') || '🎉 Đăng ký thành công!'}
              </h2>
              <p style={{ color: 'var(--gray-500)', fontSize: 14, marginBottom: 28, lineHeight: 1.6 }}>
                {t('register.doneDesc') || 'Tài khoản đã được tạo. Bạn có thể đăng nhập ngay bây giờ.'}
              </p>
              <button className="auth-submit-btn" onClick={() => navigate('/login')}>
                {t('register.goLogin') || 'Đăng nhập ngay'}
              </button>
            </div>
          )}

          {/* ── Register form ── */}
          {step === 'form' && (
            <>
              <h2 className="auth-title">{t('register.title')}</h2>
              {error && <div className="auth-error">{t(error)}</div>}

              <form onSubmit={handleRegister}>
                <div className="reg-name-row">
                  <div className="auth-field">
                    <input
                      type="text"
                      placeholder={t('register.firstName')}
                      value={firstName}
                      onChange={e => { setFirstName(e.target.value); setError(''); }}
                      required
                    />
                  </div>

                  <div className="auth-field">
                    <input
                      type="text"
                      placeholder={t('register.lastName')}
                      value={lastName}
                      onChange={e => { setLastName(e.target.value); setError(''); }}
                      required
                    />
                  </div>
                </div>

                <div className="auth-field">
                  <input
                    type="email"
                    placeholder={t('register.email')}
                    value={email}
                    onChange={e => { setEmail(e.target.value); setError(''); }}
                    required
                  />
                </div>

                <div className="auth-field auth-field-pw">
                  <input
                    type={showPw ? 'text' : 'password'}
                    placeholder={t('register.password')}
                    value={password}
                    onChange={e => { setPassword(e.target.value); setError(''); }}
                    required
                  />
                  <button type="button" className="auth-pw-toggle" onClick={() => setShowPw(!showPw)}>
                    {showPw ? <EyeOff size={18}/> : <Eye size={18}/>}
                  </button>
                </div>

                <div className="auth-field auth-field-pw">
                  <input
                    type={showConfirm ? 'text' : 'password'}
                    placeholder={t('register.confirmPassword')}
                    value={confirmPw}
                    onChange={e => { setConfirmPw(e.target.value); setError(''); }}
                    required
                  />
                  <button type="button" className="auth-pw-toggle" onClick={() => setShowConfirm(!showConfirm)}>
                    {showConfirm ? <EyeOff size={18}/> : <Eye size={18}/>}
                  </button>
                </div>

                <label className="auth-terms">
                  <input
                    type="checkbox"
                    checked={acceptedTerms}
                    onChange={e => { setAcceptedTerms(e.target.checked); setError(''); }}
                    required
                  />
                  <span>{t('register.acceptTerms')}</span>
                </label>

                <button type="submit" className="auth-submit-btn" disabled={loading}>
                  {loading ? (t('register.registering') || 'Đang đăng ký...') : t('register.submit')}
                </button>
              </form>

              <p className="auth-switch">
                {t('register.hasAccount')}{' '}
                <Link to="/login" className="auth-switch-link">
                  {t('register.login')}
                </Link>
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Register;
