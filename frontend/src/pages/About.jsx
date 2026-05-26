import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Leaf, Headphones, Truck, Sprout, Check, ArrowRight } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { testimonialApi } from '../api/testimonialApi';
import { memberApi } from '../api/memberApi';
import './About.css';

const partnerBrands = ['VietGAP', 'Mekong Fresh', 'Nông Trại Việt', 'Đà Lạt Farm', 'Ba Vì Dairy', 'Cà Mau Seafood'];

const About = () => {
  const { t } = useLang();
  const [teamMembers, setTeamMembers] = useState([]);
  const [testimonials, setTestimonials] = useState([]);

  useEffect(() => {
    memberApi.getAll(1, 10).then(data => setTeamMembers(data?.items || [])).catch(() => {});
    testimonialApi.getAll(1, 6).then(data => setTestimonials(data?.items || [])).catch(() => {});
  }, []);

  return (
    <div className="about-wrap">
      <div className="about-breadcrumb">
        <div className="container about-bc-inner">
          <Link to="/">🏠</Link><span>›</span>
          <span className="bc-active">{t('about.breadcrumb')}</span>
        </div>
      </div>

      <section className="about-hero">
        <div className="container about-hero-inner">
          <div className="about-hero-text"><h1>{t('about.heroTitle')}</h1><p>{t('about.heroDesc')}</p></div>
          <div className="about-hero-img"><img src="https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&h=400&fit=crop" alt={t('about.heroImageAlt')}/></div>
        </div>
      </section>

      <section className="about-features">
        <div className="container about-features-inner">
          <div className="about-features-img"><img src="https://images.unsplash.com/photo-1595855759920-86582396756a?w=600&h=500&fit=crop" alt={t('about.featuresImageAlt')}/></div>
          <div className="about-features-text">
            <h2>{t('about.heroTitle')}</h2><p>{t('about.heroDesc')}</p>
            <div className="about-features-grid">
              <div className="about-feature-item"><div className="about-feature-icon"><Leaf size={22}/></div><span>{t('about.organicFood')}</span></div>
              <div className="about-feature-item"><div className="about-feature-icon"><Headphones size={22}/></div><span>{t('about.support247')}</span></div>
              <div className="about-feature-item"><div className="about-feature-icon"><Truck size={22}/></div><span>{t('about.freeDelivery')}</span></div>
              <div className="about-feature-item"><div className="about-feature-icon"><Sprout size={22}/></div><span>{t('about.freshFromFarm')}</span></div>
            </div>
          </div>
        </div>
      </section>

      <section className="about-deliver">
        <div className="container about-deliver-inner">
          <div className="about-deliver-text">
            <h2>{t('about.deliverTitle')}</h2><p>{t('about.deliverDesc')}</p>
            <div className="about-deliver-features">
              {[1,2,3,4,5,6].map(i => (
                <div key={i} className="about-deliver-feat"><span className="check-icon"><Check size={12}/></span><span>{t(`about.deliverFeature${i}`)}</span></div>
              ))}
            </div>
            <Link to="/shop" className="about-deliver-btn">{t('home.shopNow')} <ArrowRight size={16}/></Link>
          </div>
          <div className="about-deliver-img"><img src="https://images.unsplash.com/photo-1589923188651-268a9765e432?w=600&h=500&fit=crop" alt={t('about.deliveryImageAlt')}/></div>
        </div>
      </section>

      <section className="about-team">
        <div className="container">
          <h2>{t('about.teamTitle')}</h2><p>{t('about.teamDesc')}</p>
          <div className="about-team-grid">
            {teamMembers.map((m) => (
              <div key={m.id} className="about-team-card">
                <img className="about-team-photo" src={m.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(m.name)}&size=200&rounded=true`} alt={m.name}/>
                <div className="about-team-info">
                  <div className="about-team-name">{m.name}</div>
                  <div className="about-team-role">{m.role || m.position}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="about-testimonials">
        <div className="container">
          <h2>{t('about.testimonialTitle')}</h2>
          <div className="about-testimonial-grid">
            {testimonials.map((item) => (
              <div key={item.id} className="about-testimonial-card">
                <span className="about-testimonial-quote">"</span>
                <p>{item.content}</p>
                <div className="about-testimonial-author">
                  <img className="about-testimonial-avatar" src={item.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(item.name)}&size=60&rounded=true`} alt={item.name}/>
                  <div>
                    <div className="about-testimonial-name">{item.name}</div>
                    <div className="about-testimonial-role">{item.role || t('about.customerRole')}</div>
                  </div>
                  <span className="about-testimonial-stars">★★★★★</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="about-brands">
        <div className="container about-brands-inner">
          {partnerBrands.map((brand, i) => (
            <span key={i} className={`about-brand-logo ${i === 0 ? 'active' : ''}`}>{brand}</span>
          ))}
        </div>
      </section>
    </div>
  );
};

export default About;
