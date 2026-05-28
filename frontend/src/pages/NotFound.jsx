import { Link } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import './NotFound.css';

const NotFound = () => {
  const { t } = useLang();

  return (
    <div className="nf-wrap">
      {/* Breadcrumb */}
      <div className="nf-breadcrumb">
        <div className="container nf-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span className="bc-active">{t('notFound.breadcrumb')}</span>
        </div>
      </div>

      {/* Content */}
      <div className="nf-content">
        <div className="nf-illustration">
          <div className="nf-big-text">404</div>
          <div className="nf-question-circle">?</div>
          {/* SVG person illustration */}
          <svg className="nf-plant-person" viewBox="0 0 200 180" fill="none" xmlns="http://www.w3.org/2000/svg">
            {/* Sitting person with plant */}
            <ellipse cx="100" cy="170" rx="80" ry="10" fill="#E8F5E9" opacity="0.6"/>
            {/* Plant leaves */}
            <path d="M70 80 C60 40, 100 20, 130 50" stroke="#2C742F" strokeWidth="3" fill="#4CAF50" opacity="0.7"/>
            <path d="M90 85 C80 50, 120 30, 140 60" stroke="#2C742F" strokeWidth="3" fill="#66BB6A" opacity="0.6"/>
            <path d="M80 90 C90 50, 60 35, 50 65" stroke="#2C742F" strokeWidth="3" fill="#81C784" opacity="0.5"/>
            {/* Person body */}
            <circle cx="115" cy="100" r="15" fill="#FFD3B5"/>
            <path d="M100 115 C95 125, 90 155, 95 165 L135 165 C140 155, 135 125, 130 115 Z" fill="#546E7A"/>
            {/* Hair */}
            <path d="M100 95 C100 80, 130 80, 130 95" fill="#37474F"/>
            {/* Arms */}
            <path d="M100 120 C85 130, 80 145, 85 155" stroke="#FFD3B5" strokeWidth="6" strokeLinecap="round" fill="none"/>
            <path d="M130 120 C145 130, 148 140, 145 150" stroke="#FFD3B5" strokeWidth="6" strokeLinecap="round" fill="none"/>
            {/* Laptop */}
            <rect x="80" y="145" width="40" height="25" rx="3" fill="#78909C"/>
            <rect x="75" y="168" width="50" height="3" rx="1" fill="#90A4AE"/>
          </svg>
        </div>

        <h1>{t('notFound.title')}</h1>
        <p>{t('notFound.desc')}</p>
        <Link to="/" className="nf-btn">
          {t('notFound.backHome')}
        </Link>
      </div>
    </div>
  );
};

export default NotFound;
