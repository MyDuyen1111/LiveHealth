import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Minus } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import './Faq.css';

const faqData = [
  {
    questionKey: 'faq.q1',
    answerKey: 'faq.a1',
  },
  {
    questionKey: 'faq.q2',
    answerKey: 'faq.a2',
  },
  {
    questionKey: 'faq.q3',
    answerKey: 'faq.a3',
  },
  {
    questionKey: 'faq.q4',
    answerKey: 'faq.a4',
  },
  {
    questionKey: 'faq.q5',
    answerKey: 'faq.a5',
  },
];

const Faq = () => {
  const { t } = useLang();
  const [openIndex, setOpenIndex] = useState(0);

  const toggle = (index) => {
    setOpenIndex(openIndex === index ? -1 : index);
  };

  return (
    <div className="faq-wrap">
      {/* Breadcrumb */}
      <div className="faq-breadcrumb">
        <div className="container faq-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span className="bc-active">{t('faq.breadcrumb')}</span>
        </div>
      </div>

      <div className="container faq-section">
        <div className="faq-content">
          <div className="faq-text-side">
            <h1 className="faq-title">{t('faq.title')}</h1>

            <div className="faq-accordion">
              {faqData.map((item, index) => (
                <div
                  key={index}
                  className={`faq-item ${openIndex === index ? 'open' : ''}`}
                >
                  <button className="faq-question" onClick={() => toggle(index)}>
                    <span>{t(item.questionKey)}</span>
                    {openIndex === index ? <Minus size={18} /> : <Plus size={18} />}
                  </button>
                  {openIndex === index && (
                    <div className="faq-answer">
                      <p>{t(item.answerKey)}</p>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="faq-image-side">
            <img
              src="https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&q=80"
              alt="FAQ illustration"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Faq;
