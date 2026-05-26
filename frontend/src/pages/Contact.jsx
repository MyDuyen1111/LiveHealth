import { Link } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import './Contact.css';

const Contact = () => {
  const { t } = useLang();

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

            <form onSubmit={(e) => e.preventDefault()}>
              <div className="contact-form-row">
                <input type="text" placeholder={t('contact.name')} />
                <input type="email" placeholder="zakirsoft@gmail.com" />
              </div>
              <div className="contact-form-row full">
                <input type="text" defaultValue={t('contact.message')} />
              </div>
              <div className="contact-form-row full">
                <textarea placeholder={t('contact.subject')} rows="4"></textarea>
              </div>
              <button type="submit" className="contact-send-btn">
                {t('contact.send')}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contact;
