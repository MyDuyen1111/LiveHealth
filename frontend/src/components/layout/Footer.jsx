import { Link } from 'react-router-dom';
import { Leaf, Mail, Phone } from 'lucide-react';
import { useLang } from '../../context/LanguageContext';
import './Footer.css';

const Footer = () => {
  const { t } = useLang();

  return (
    <footer className="eco-footer">
      <div className="ft-newsletter">
        <div className="container ft-nl-inner">
          <div className="ft-nl-left">
            <span className="ft-logo-mark"><Leaf size={22} /></span>
            <span className="ft-nl-brand">LiveHealth</span>
          </div>
          <div className="ft-nl-center">
            <span>{t('newsletter.title')}</span>
          </div>
          <div className="ft-nl-right">
            <div className="ft-nl-form">
              <input type="email" placeholder={t('newsletter.placeholder')} />
              <button>{t('newsletter.subscribe')}</button>
            </div>
          </div>
        </div>
      </div>

      <div className="ft-main">
        <div className="container ft-grid">
          <div className="ft-col ft-col-about">
            <h4>{t('footer.aboutUs')}</h4>
            <p>{t('footer.aboutDesc')}</p>
            <div className="ft-contact">
              <span><Phone size={15} /> (028) 9876 5432</span>
              <span><Mail size={15} /> hello@livehealth.vn</span>
            </div>
          </div>
          <div className="ft-col">
            <h4>{t('footer.myAccount')}</h4>
            <ul>
              <li><Link to="/account">{t('footer.myAccount')}</Link></li>
              <li><Link to="/account/orders">{t('footer.myOrders')}</Link></li>
              <li><Link to="/cart">{t('footer.shoppingCart')}</Link></li>
            </ul>
          </div>
          <div className="ft-col">
            <h4>{t('footer.helpCenter')}</h4>
            <ul>
              <li><Link to="/contact">{t('footer.contact')}</Link></li>
              <li><Link to="/faq">{t('footer.faq')}</Link></li>
              <li><Link to="/about">{t('footer.terms')}</Link></li>
              <li><Link to="/about">{t('footer.privacy')}</Link></li>
            </ul>
          </div>
          <div className="ft-col">
            <h4>{t('footer.proxy')}</h4>
            <ul>
              <li><Link to="/">{t('header.home')}</Link></li>
              <li><Link to="/shop">{t('header.shop')}</Link></li>
              <li><Link to="/shop">{t('footer.returns')}</Link></li>
              <li><Link to="/about">{t('footer.privacy')}</Link></li>
            </ul>
          </div>
          <div className="ft-col ft-col-note">
            <h4>{t('footer.freshProduceTitle')}</h4>
            <p>{t('footer.freshProduceDesc')}</p>
            <Link to="/health-ai" className="ft-care-link">{t('header.healthAI')}</Link>
          </div>
        </div>
      </div>

      <div className="ft-bottom">
        <div className="container ft-bottom-inner">
          <span>{t('footer.copyright')}</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
