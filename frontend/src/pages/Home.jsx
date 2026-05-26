import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ProductCard from '../components/product/ProductCard';
import { productApi } from '../api/productApi';
import { categoryApi } from '../api/categoryApi';
import { testimonialApi } from '../api/testimonialApi';
import { useLang } from '../context/LanguageContext';
import { Truck, HeadphonesIcon, ShieldCheck, RotateCcw, ArrowRight, Star, Quote, Play } from 'lucide-react';
import './Home.css';

const Home = () => {
  const { t } = useLang();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [testimonials, setTestimonials] = useState([]);

  useEffect(() => {
    productApi.getAll(1, 15).then(data => {
      setProducts(data?.items || []);
    }).catch(() => { });

    categoryApi.getAll(1, 12).then(data => {
      setCategories(data?.items || []);
    }).catch(() => { });

    testimonialApi.getAll(1, 6).then(data => {
      setTestimonials(data?.items || []);
    }).catch(() => { });
  }, []);

  const featured = products.slice(0, 5);
  const popular = products.slice(0, 10);
  const newest = products.slice(5, 10);

  const categoryIcons = [
    'https://cdn-icons-png.flaticon.com/512/3194/3194591.png',
    'https://cdn-icons-png.flaticon.com/512/2153/2153788.png',
    'https://cdn-icons-png.flaticon.com/512/3143/3143643.png',
    'https://cdn-icons-png.flaticon.com/512/3050/3050116.png',
    'https://cdn-icons-png.flaticon.com/512/2985/2985311.png',
    'https://cdn-icons-png.flaticon.com/512/2738/2738730.png',
    'https://cdn-icons-png.flaticon.com/512/3014/3014502.png',
    'https://cdn-icons-png.flaticon.com/512/2966/2966327.png',
    'https://cdn-icons-png.flaticon.com/512/3174/3174880.png',
    'https://cdn-icons-png.flaticon.com/512/2515/2515183.png',
    'https://cdn-icons-png.flaticon.com/512/3050/3050159.png',
    'https://cdn-icons-png.flaticon.com/512/5787/5787016.png',
  ];

  const instagrams = [
    'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=200&q=80',
    'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200&q=80',
    'https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=200&q=80',
    'https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=200&q=80',
    'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=200&q=80',
    'https://images.unsplash.com/photo-1543362906-acfc16c67564?w=200&q=80',
  ];

  const heroLines = t('home.heroTitle').split('\n');

  return (
    <div className="hp">

      {/* ━━ 1. HERO ━━ */}
      <section className="hp-hero container">
        <div className="hp-hero-grid">
          <div className="hp-hero-main">
            <div className="hp-hero-content">
              <span className="hp-hero-tag">{t('home.heroTag')}</span>
              <h1>{heroLines.map((line, i) => <span key={i}>{line}{i < heroLines.length - 1 && <br />}</span>)}</h1>
              <p className="hp-hero-sale">
                {t('home.heroSale')} <span className="hp-hero-discount">30% OFF</span>
              </p>
              <p className="hp-hero-free">{t('home.heroFree')}</p>
              <Link to="/shop" className="hp-btn-green">{t('home.shopNow')} <ArrowRight size={16} /></Link>
            </div>
          </div>
          <div className="hp-hero-side">
            <div className="hp-hero-side-card hp-side-summer">
              <span>{t('home.summerSale')}</span>
              <h3>75% OFF</h3>
              <Link to="/shop" className="hp-side-link">{t('home.shopNow')} →</Link>
            </div>
            <div className="hp-hero-side-card hp-side-deal">
              <span>{t('home.specialProducts').split('\n').map((line, i) => <span key={i}>{line}{i === 0 && <br />}</span>)}</span>
              <Link to="/shop" className="hp-side-link white">{t('home.shopNow')} →</Link>
            </div>
          </div>
        </div>
      </section>

      {/* ━━ 2. FEATURES ━━ */}
      <section className="hp-features">
        <div className="container hp-features-grid">
          <div className="hp-feat"><Truck size={40} color="#00B207" /><div><h4>{t('home.freeShipping')}</h4><p>{t('home.freeShippingDesc')}</p></div></div>
          <div className="hp-feat"><HeadphonesIcon size={40} color="#00B207" /><div><h4>{t('home.support')}</h4><p>{t('home.supportDesc')}</p></div></div>
          <div className="hp-feat"><ShieldCheck size={40} color="#00B207" /><div><h4>{t('home.securePayment')}</h4><p>{t('home.securePaymentDesc')}</p></div></div>
          <div className="hp-feat"><RotateCcw size={40} color="#00B207" /><div><h4>{t('home.moneyBack')}</h4><p>{t('home.moneyBackDesc')}</p></div></div>
        </div>
      </section>

      {/* ━━ 3. POPULAR CATEGORIES ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head">
          <h2>{t('home.popularCategories')}</h2>
          <Link to="/shop" className="hp-view-all">{t('home.viewAll')}</Link>
        </div>
        <div className="hp-cat-grid">
          {categories.map((c, i) => (
            <Link to={`/shop?category=${c.id}`} key={c.id} className="hp-cat-card">
              <img src={categoryIcons[i % categoryIcons.length]} alt={c.name} />
              <span className="hp-cat-name">{c.name}</span>
            </Link>
          ))}
        </div>
      </section>

      {/* ━━ 4. POPULAR PRODUCTS ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head">
          <h2>{t('home.popularProducts')}</h2>
          <Link to="/shop" className="hp-view-all">{t('home.viewAll')}</Link>
        </div>
        <div className="hp-products-grid">
          {popular.map(p => <ProductCard key={p.id} product={p} />)}
        </div>
      </section>

      {/* ━━ 5. PROMO BANNERS ━━ */}
      <section className="hp-promos container">
        <div className="hp-promo hp-promo-blue">
          <div className="hp-promo-timer">
            <div className="hp-timer-box"><span>00</span><small>{t('home.day')}</small></div>
            <div className="hp-timer-box"><span>02</span><small>{t('home.hour')}</small></div>
            <div className="hp-timer-box"><span>18</span><small>{t('home.min')}</small></div>
            <div className="hp-timer-box"><span>46</span><small>{t('home.sec')}</small></div>
          </div>
          <h3>{t('home.saleOfMonth')}</h3>
          <Link to="/shop" className="hp-btn-green sm">{t('home.shopNow')} <ArrowRight size={14} /></Link>
        </div>
        <div className="hp-promo hp-promo-dark">
          <span className="hp-promo-label">{t('home.lowFatMeat')}</span>
          <h3>{t('home.lowFatMeatTitle')}</h3>
          <p>{t('home.startFrom')} <strong>150.000đ</strong></p>
          <Link to="/shop" className="hp-btn-green sm">{t('home.shopNow')} <ArrowRight size={14} /></Link>
        </div>
        <div className="hp-promo hp-promo-yellow">
          <span className="hp-promo-label dark">{t('home.freshFruit')}</span>
          <h3 className="dark-text">{t('home.freshFruitTitle')}</h3>
          <p className="dark-text">{t('home.discountUp')} <strong>64%</strong></p>
          <Link to="/shop" className="hp-btn-green sm">{t('home.shopNow')} <ArrowRight size={14} /></Link>
        </div>
      </section>

      {/* ━━ 6. HOT DEALS ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head">
          <h2>{t('home.hotDeals')}</h2>
          <Link to="/shop" className="hp-view-all">{t('home.viewAll')}</Link>
        </div>
        <div className="hp-hotdeals">
          <div className="hp-hotdeal-big">
            <img src="https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=600&q=80" alt="Hot deal" />
            <div className="hp-hotdeal-overlay">
              <span className="hp-hd-badge">Sale 50%</span>
              <h3>{t('home.freshFruitDaily')}</h3>
              <Link to="/shop" className="hp-btn-green sm">{t('home.shopNow')} <ArrowRight size={14} /></Link>
            </div>
          </div>
          <div className="hp-hotdeal-grid">
            {featured.map(p => <ProductCard key={p.id} product={p} />)}
          </div>
        </div>
      </section>

      {/* ━━ 7. FEATURED PRODUCTS ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head">
          <h2>{t('home.featuredProducts')}</h2>
          <Link to="/shop" className="hp-view-all">{t('home.viewAll')}</Link>
        </div>
        <div className="hp-products-grid five">
          {newest.map(p => <ProductCard key={p.id} product={p} />)}
        </div>
      </section>

      {/* ━━ 8. DISCOUNT BANNER ━━ */}
      <section className="hp-discount-banner container">
        <div className="hp-disc-inner">
          <div className="hp-disc-left">
            <img src="https://images.unsplash.com/photo-1500595046743-cd271d694d30?w=500&q=80" alt="farmer" />
          </div>
          <div className="hp-disc-right">
            <span>{t('home.discountBanner.save')}</span>
            <h2>{t('home.discountBanner.title')}</h2>
            <Link to="/shop" className="hp-btn-green">{t('home.shopNow')} <ArrowRight size={16} /></Link>
          </div>
        </div>
      </section>

      {/* ━━ 9. ORGANIC FARM BANNER ━━ */}
      <section className="hp-farm-banner">
        <div className="container hp-farm-inner">
          <h2>{t('home.farmBanner')}</h2>
          <div className="hp-farm-play">
            <Play size={28} fill="white" color="white" />
          </div>
        </div>
      </section>

      {/* ━━ 10. TESTIMONIALS ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head center">
          <h2>{t('home.testimonials')}</h2>
        </div>
        <div className="hp-testi-grid">
          {testimonials.map((tItem, i) => (
            <div key={tItem.id || i} className="hp-testi-card">
              <Quote size={32} color="#00B207" className="hp-testi-quote" />
              <p>{tItem.content}</p>
              <div className="hp-testi-footer">
                <img src={tItem.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(tItem.name)}&background=e8f5e9&color=2C742F&size=60&rounded=true`} alt={tItem.name} />
                <div>
                  <strong>{tItem.name}</strong>
                  <div className="hp-testi-stars">
                    {[...Array(5)].map((_, j) => (
                      <Star key={j} size={12} fill={j < 5 ? '#FF8A00' : '#DADADA'} color={j < 5 ? '#FF8A00' : '#DADADA'} />
                    ))}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ━━ 11. INSTAGRAM ━━ */}
      <section className="hp-section container">
        <div className="hp-sec-head center">
          <h2>{t('home.followInstagram')}</h2>
        </div>
        <div className="hp-insta-grid">
          {instagrams.map((src, i) => (
            <a key={i} href="#" className="hp-insta-item">
              <img src={src} alt="instagram" />
            </a>
          ))}
        </div>
      </section>

    </div>
  );
};

export default Home;
