import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import { post } from '../api/apiClient';
import './Contact.css';

const Contact = () => {
  const { t } = useLang();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [subject, setSubject] = useState('');
  const [message, setMessage] = useState('');
  
  const [submitting, setSubmitting] = useState(false);
  const [feedback, setFeedback] = useState(null); // { type: 'success' | 'error', text: string }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !email || !subject || !message) {
      setFeedback({ type: 'error', text: 'Vui lòng điền đầy đủ tất cả thông tin.' });
      return;
    }

    setSubmitting(true);
    setFeedback(null);

    try {
      await post('/contact', { name, email, subject, message });
      setFeedback({ type: 'success', text: 'Gửi tin nhắn liên hệ thành công! Chúng tôi sẽ phản hồi sớm nhất có thể.' });
      // Reset form
      setName('');
      setEmail('');
      setSubject('');
      setMessage('');
    } catch (err) {
      setFeedback({ type: 'error', text: err.message || 'Gửi tin nhắn thất bại. Vui lòng thử lại.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="contact-wrap">
      {/* Breadcrumb */}
      <div className="contact-breadcrumb">
        <div className="container contact-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span className="bc-active">{t('contact.breadcrumb')}</span>
        </div>
      </div>

      {/* Main section */}
      <div className="contact-section">
        <div className="container">
          {/* Form only */}
          <div className="contact-form-card" style={{ maxWidth: 700, margin: '0 auto' }}>
            <h2>{t('contact.title')}</h2>
            <p>{t('contact.desc')}</p>

            {feedback && (
              <div style={{
                padding: '12px 16px',
                background: feedback.type === 'error' ? '#ffebee' : '#e8f5e9',
                color: feedback.type === 'error' ? '#c62828' : '#2e7d32',
                borderRadius: 8,
                marginBottom: 20,
                fontSize: 14,
                fontWeight: 500
              }}>
                {feedback.text}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="contact-form-row">
                <input 
                  type="text" 
                  placeholder={t('contact.name')} 
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  disabled={submitting}
                  required
                />
                <input 
                  type="email" 
                  placeholder="Email" 
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={submitting}
                  required
                />
              </div>
              <div className="contact-form-row full">
                <input 
                  type="text" 
                  placeholder={t('contact.subject')} 
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  disabled={submitting}
                  required
                />
              </div>
              <div className="contact-form-row full">
                <textarea 
                  placeholder={t('contact.message')} 
                  rows="5"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  disabled={submitting}
                  required
                ></textarea>
              </div>
              <button type="submit" className="contact-send-btn" disabled={submitting}>
                {submitting ? 'Đang gửi...' : t('contact.send')}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contact;
