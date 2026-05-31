import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Mail, CheckCircle } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import './Register.css';

// Step: 'form' → 'done'

const EMAIL_PATTERN = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)+$/;
// Mật khẩu: bắt buộc có chữ thường, chữ hoa, số, ký tự đặc biệt, tối thiểu 8 ký tự, không khoảng trắng
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,}$/;

// ── Input sanitizers (chặn nhập ký tự không hợp lệ ngay khi gõ) ──
// Họ/Tên: chỉ cho chữ cái (kể cả tiếng Việt) + khoảng trắng, gộp space thừa,
// bỏ space đầu, tối đa 30 ký tự (chặn ký tự thứ 31).
const sanitizeName = (v) =>
  v
    .replace(/[^\p{L}\s]/gu, '') // chặn số & ký tự đặc biệt
    .replace(/\s+/g, ' ')        // trim space giữa nếu thừa
    .replace(/^\s/, '')          // trim space đầu
    .slice(0, 30);

// Sđt: chỉ 10 ký tự số (chặn chữ, ký tự đặc biệt, ký tự thứ 11)
const sanitizePhone = (v) => v.replace(/\D/g, '').slice(0, 10);

// Email: tự bỏ mọi khoảng trắng (trim đầu/cuối)
const sanitizeEmail = (v) => v.replace(/\s/g, '');

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
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPw, setConfirmPw] = useState('');
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    navigate('/account', { replace: true });
    return null;
  }

  const clearError = (field) =>
    setErrors((prev) => {
      if (!prev[field]) return prev;
      const next = { ...prev };
      delete next[field];
      return next;
    });

  // ── Validate tất cả các trường, trả về map lỗi ──
  const validate = () => {
    const e = {};
    const fn = firstName.trim();
    const ln = lastName.trim();
    const em = email.trim();
    const ph = phone.trim();

    if (!fn) e.firstName = 'Hãy nhập họ của bạn';
    if (!ln) e.lastName = 'Hãy nhập tên của bạn';

    if (!em) e.email = 'Hãy nhập email của bạn';
    else if (!EMAIL_PATTERN.test(em)) e.email = 'Email không đúng định dạng';

    if (!ph) e.phone = 'Hãy nhập số điện thoại của bạn';
    else if (ph.length !== 10) e.phone = 'Số điện thoại phải gồm 10 chữ số';

    if (!password.trim()) e.password = 'Hãy nhập mật khẩu của bạn';
    else if (!PASSWORD_PATTERN.test(password)) e.password = 'Mật khẩu không đúng định dạng';

    if (!confirmPw.trim()) e.confirmPw = 'Hãy nhập mật khẩu xác nhận';
    else if (password !== confirmPw) e.confirmPw = 'Mật khẩu không khớp';

    return e;
  };

  // ── Step 1: Register ──────────────────────────────────────────
  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');

    const eMap = validate();
    if (!acceptedTerms) eMap.terms = t('register.termsRequired');
    setErrors(eMap);
    if (Object.keys(eMap).length > 0) return;

    setLoading(true);
    const result = await register({
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      email: email.trim(),
      phone: phone.trim(),
      password,
      reEnterPassword: confirmPw,
      acceptedTerms,
    });
    setLoading(false);

    if (result.success) {
      setStep('done');
      return;
    }

    // Map lỗi từ backend về đúng trường
    const msg = result.error || '';
    if (/email.*(existed|exist|tồn tại)/i.test(msg)) {
      setErrors({ email: 'Email đã tồn tại' });
    } else if (/phone.*(existed|exist|tồn tại)/i.test(msg)) {
      setErrors({ phone: 'Số điện thoại đã tồn tại' });
    } else {
      setError(msg);
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

              <form onSubmit={handleRegister} noValidate>
                <div className="reg-name-row">
                  <div className="auth-field">
                    <input
                      type="text"
                      placeholder={t('register.firstName')}
                      value={firstName}
                      maxLength={30}
                      aria-invalid={!!errors.firstName}
                      onChange={e => { setFirstName(sanitizeName(e.target.value)); clearError('firstName'); }}
                      onBlur={() => setFirstName(v => v.trim())}
                    />
                    {errors.firstName && <p className="auth-field-error">{errors.firstName}</p>}
                  </div>

                  <div className="auth-field">
                    <input
                      type="text"
                      placeholder={t('register.lastName')}
                      value={lastName}
                      maxLength={30}
                      aria-invalid={!!errors.lastName}
                      onChange={e => { setLastName(sanitizeName(e.target.value)); clearError('lastName'); }}
                      onBlur={() => setLastName(v => v.trim())}
                    />
                    {errors.lastName && <p className="auth-field-error">{errors.lastName}</p>}
                  </div>
                </div>

                <div className="auth-field">
                  <input
                    type="text"
                    placeholder={t('register.email')}
                    value={email}
                    aria-invalid={!!errors.email}
                    onChange={e => { setEmail(sanitizeEmail(e.target.value)); clearError('email'); }}
                  />
                  {errors.email && <p className="auth-field-error">{errors.email}</p>}
                </div>

                <div className="auth-field">
                  <input
                    type="tel"
                    inputMode="numeric"
                    placeholder={t('checkout.phone')}
                    value={phone}
                    maxLength={10}
                    aria-invalid={!!errors.phone}
                    onChange={e => { setPhone(sanitizePhone(e.target.value)); clearError('phone'); }}
                  />
                  {errors.phone && <p className="auth-field-error">{errors.phone}</p>}
                </div>

                <div className="auth-field auth-field-pw">
                  <input
                    type={showPw ? 'text' : 'password'}
                    placeholder={t('register.password')}
                    value={password}
                    aria-invalid={!!errors.password}
                    onChange={e => { setPassword(e.target.value); clearError('password'); clearError('confirmPw'); }}
                  />
                  <button type="button" className="auth-pw-toggle" onClick={() => setShowPw(!showPw)}>
                    {showPw ? <EyeOff size={18}/> : <Eye size={18}/>}
                  </button>
                  {errors.password && <p className="auth-field-error">{errors.password}</p>}
                </div>

                <div className="auth-field auth-field-pw">
                  <input
                    type={showConfirm ? 'text' : 'password'}
                    placeholder={t('register.confirmPassword')}
                    value={confirmPw}
                    aria-invalid={!!errors.confirmPw}
                    onChange={e => { setConfirmPw(e.target.value); clearError('confirmPw'); }}
                  />
                  <button type="button" className="auth-pw-toggle" onClick={() => setShowConfirm(!showConfirm)}>
                    {showConfirm ? <EyeOff size={18}/> : <Eye size={18}/>}
                  </button>
                  {errors.confirmPw && <p className="auth-field-error">{errors.confirmPw}</p>}
                </div>

                <label className="auth-terms">
                  <input
                    type="checkbox"
                    checked={acceptedTerms}
                    onChange={e => { setAcceptedTerms(e.target.checked); clearError('terms'); }}
                  />
                  <span>{t('register.acceptTerms')}</span>
                </label>
                {errors.terms && <p className="auth-field-error">{errors.terms}</p>}

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
