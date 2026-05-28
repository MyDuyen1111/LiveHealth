import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import './NewsletterPopup.css';

const NewsletterPopup = () => {
  const [show, setShow] = useState(false);
  const [dontShowAgain, setDontShowAgain] = useState(false);
  const { t } = useLang();

  useEffect(() => {
    const dismissed = sessionStorage.getItem('nl_dismissed') || localStorage.getItem('nl_dismissed');
    if (!dismissed) {
      const timer = setTimeout(() => setShow(true), 3000);
      return () => clearTimeout(timer);
    }
  }, []);

  const close = () => {
    setShow(false);
    if (dontShowAgain) {
      localStorage.setItem('nl_dismissed', '1');
    } else {
      sessionStorage.setItem('nl_dismissed', '1');
    }
  };

  const updateDontShowAgain = (checked) => {
    setDontShowAgain(checked);
    if (checked) {
      localStorage.setItem('nl_dismissed', '1');
    } else {
      localStorage.removeItem('nl_dismissed');
    }
  };

  if (!show) return null;

  return (
    <div className="nl-overlay" onClick={close}>
      <div className="nl-popup" onClick={e => e.stopPropagation()}>
        <button className="nl-close" onClick={close}><X size={22}/></button>
        <div className="nl-img">
          <img src="https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&q=80" alt="newsletter"/>
        </div>
        <div className="nl-content">
          <h2>{t('newsletter.title')}</h2>
          <p>{t('newsletter.subtitle')} <span className="nl-highlight">{t('newsletter.discount')} money</span> {t('newsletter.discountText')}</p>
          <div className="nl-form">
            <input type="email" placeholder={t('newsletter.placeholder')}/>
            <button className="nl-sub-btn">{t('newsletter.subscribe')}</button>
          </div>
          <label className="nl-no-show" htmlFor="nl-no-show-again">
            <input
              id="nl-no-show-again"
              type="checkbox"
              checked={dontShowAgain}
              onChange={e => updateDontShowAgain(e.target.checked)}
            />
            {t('newsletter.dontShow')}
          </label>
        </div>
      </div>
    </div>
  );
};

export default NewsletterPopup;
