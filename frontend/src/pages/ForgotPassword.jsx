import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Mail, KeyRound, ShieldCheck } from 'lucide-react';
import { authApi } from '../api/authApi';
import { useLang } from '../context/LanguageContext';
import './ForgotPassword.css';

// 3 steps: 'email' → 'otp' → 'reset' → 'done'

const ForgotPassword = () => {
  const { t } = useLang();
  const navigate = useNavigate();

  const [step, setStep] = useState('email');
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [reEnter, setReEnter] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [showPw2, setShowPw2] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // ── Step 1: Send OTP to email ──────────────────────────────────
  const handleSendOtp = async (e) => {
    e.preventDefault();
    if (!email) { setError(t('forgot.emailRequired')); return; }
    setLoading(true); setError('');
    try {
      await authApi.forgotPassword(email);
      setSuccess(t('forgot.otpSent'));
      setStep('otp');
    } catch (err) {
      setError(err.message || t('forgot.sendFailed'));
    } finally { setLoading(false); }
  };

  // ── Step 2: Verify OTP ─────────────────────────────────────────
  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    if (otp.length < 4) { setError(t('forgot.otpInvalid')); return; }
    setLoading(true); setError('');
    try {
      await authApi.verifyOtpResetPassword(email, otp);
      setSuccess(t('forgot.otpVerified'));
      setStep('reset');
    } catch (err) {
      setError(err.message || t('forgot.otpWrong'));
    } finally { setLoading(false); }
  };

  // ── Step 3: Reset password ─────────────────────────────────────
  const handleResetPassword = async (e) => {
    e.preventDefault();
    if (!newPassword || newPassword.length < 6) {
      setError(t('forgot.pwTooShort')); return;
    }
    if (newPassword !== reEnter) {
      setError(t('forgot.pwMismatch')); return;
    }
    setLoading(true); setError('');
    try {
      await authApi.resetPassword(email, newPassword, reEnter);
      setStep('done');
    } catch (err) {
      setError(err.message || t('forgot.resetFailed'));
    } finally { setLoading(false); }
  };

  const STEPS = ['email', 'otp', 'reset'];
  const stepIndex = STEPS.indexOf(step);

  return (
    <div className="fp-wrap">
      {/* Breadcrumb */}
      <div className="auth-breadcrumb">
        <div className="container auth-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('forgot.breadcrumb')}</span>
        </div>
      </div>

      <div className="fp-section">
        <div className="fp-card">

          {/* Done state */}
          {step === 'done' ? (
            <div className="fp-done">
              <div className="fp-done-icon">
                <ShieldCheck size={48} strokeWidth={1.8} />
              </div>
              <h2 className="fp-done-title">{t('forgot.doneTitle')}</h2>
              <p className="fp-done-desc">{t('forgot.doneDesc')}</p>
              <button className="fp-submit-btn" onClick={() => navigate('/login')}>
                {t('forgot.goLogin')}
              </button>
            </div>
          ) : (
            <>
              {/* Step indicator */}
              <div className="fp-steps-bar">
                {[
                  { icon: <Mail size={16}/>, label: t('forgot.stepEmail') },
                  { icon: <KeyRound size={16}/>, label: t('forgot.stepOtp') },
                  { icon: <ShieldCheck size={16}/>, label: t('forgot.stepReset') },
                ].map((s, i) => (
                  <div key={i} className={`fp-step ${i < stepIndex ? 'done' : i === stepIndex ? 'active' : ''}`}>
                    <div className="fp-step-circle">
                      {i < stepIndex ? '✓' : s.icon}
                    </div>
                    <span className="fp-step-label">{s.label}</span>
                    {i < 2 && <div className={`fp-step-line ${i < stepIndex ? 'done' : ''}`} />}
                  </div>
                ))}
              </div>

              {/* Title */}
              <h2 className="fp-title">
                {step === 'email' && t('forgot.title')}
                {step === 'otp' && t('forgot.otpTitle')}
                {step === 'reset' && t('forgot.resetTitle')}
              </h2>
              <p className="fp-subtitle">
                {step === 'email' && t('forgot.subtitle')}
                {step === 'otp' && `${t('forgot.otpSubtitle')} ${email}`}
                {step === 'reset' && t('forgot.resetSubtitle')}
              </p>

              {/* Error / success */}
              {error && <div className="fp-error">{t(error)}</div>}
              {success && step === 'otp' && <div className="fp-success">{success}</div>}

              {/* ── Step 1: Email form ── */}
              {step === 'email' && (
                <form onSubmit={handleSendOtp} className="fp-form">
                  <div className="fp-field">
                    <label htmlFor="fp-email">{t('forgot.emailLabel')}</label>
                    <input
                      id="fp-email"
                      type="email"
                      placeholder={t('forgot.emailPlaceholder')}
                      value={email}
                      onChange={e => { setEmail(e.target.value); setError(''); }}
                      required
                      autoFocus
                    />
                  </div>
                  <button type="submit" className="fp-submit-btn" disabled={loading}>
                    {loading ? t('forgot.sending') : t('forgot.sendOtp')}
                  </button>
                  <p className="fp-back-link">
                    {t('forgot.rememberedPw')}{' '}
                    <Link to="/login">{t('forgot.backLogin')}</Link>
                  </p>
                </form>
              )}

              {/* ── Step 2: OTP form ── */}
              {step === 'otp' && (
                <form onSubmit={handleVerifyOtp} className="fp-form">
                  <div className="fp-field">
                    <label htmlFor="fp-otp">{t('forgot.otpLabel')}</label>
                    <input
                      id="fp-otp"
                      type="text"
                      inputMode="numeric"
                      maxLength={8}
                      placeholder={t('forgot.otpPlaceholder')}
                      value={otp}
                      onChange={e => { setOtp(e.target.value.replace(/\D/g, '')); setError(''); }}
                      required
                      autoFocus
                      className="fp-otp-input"
                    />
                  </div>
                  <button type="submit" className="fp-submit-btn" disabled={loading}>
                    {loading ? t('forgot.verifying') : t('forgot.verifyOtp')}
                  </button>
                  <p className="fp-back-link">
                    <button type="button" className="fp-resend-btn" onClick={handleSendOtp} disabled={loading}>
                      {t('forgot.resendOtp')}
                    </button>
                  </p>
                </form>
              )}

              {/* ── Step 3: New password form ── */}
              {step === 'reset' && (
                <form onSubmit={handleResetPassword} className="fp-form">
                  <div className="fp-field fp-field-pw">
                    <label htmlFor="fp-new-pw">{t('forgot.newPwLabel')}</label>
                    <input
                      id="fp-new-pw"
                      type={showPw ? 'text' : 'password'}
                      placeholder={t('forgot.newPwPlaceholder')}
                      value={newPassword}
                      onChange={e => { setNewPassword(e.target.value); setError(''); }}
                      required
                      autoFocus
                    />
                    <button type="button" className="fp-pw-toggle" onClick={() => setShowPw(!showPw)}>
                      {showPw ? <EyeOff size={18}/> : <Eye size={18}/>}
                    </button>
                  </div>
                  <div className="fp-field fp-field-pw">
                    <label htmlFor="fp-confirm-pw">{t('forgot.confirmPwLabel')}</label>
                    <input
                      id="fp-confirm-pw"
                      type={showPw2 ? 'text' : 'password'}
                      placeholder={t('forgot.confirmPwPlaceholder')}
                      value={reEnter}
                      onChange={e => { setReEnter(e.target.value); setError(''); }}
                      required
                    />
                    <button type="button" className="fp-pw-toggle" onClick={() => setShowPw2(!showPw2)}>
                      {showPw2 ? <EyeOff size={18}/> : <Eye size={18}/>}
                    </button>
                  </div>

                  {/* Password strength */}
                  {newPassword.length > 0 && (
                    <div className="fp-strength">
                      <div className={`fp-strength-bar ${newPassword.length >= 8 ? 'strong' : newPassword.length >= 6 ? 'medium' : 'weak'}`} />
                      <span className="fp-strength-label">
                        {newPassword.length >= 8 ? t('forgot.pwStrong') : newPassword.length >= 6 ? t('forgot.pwMedium') : t('forgot.pwWeak')}
                      </span>
                    </div>
                  )}

                  <button type="submit" className="fp-submit-btn" disabled={loading}>
                    {loading ? t('forgot.resetting') : t('forgot.resetPw')}
                  </button>
                </form>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
