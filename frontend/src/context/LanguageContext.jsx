import { createContext, useState, useContext, useCallback } from 'react';
import { translations } from '../data/translations';

const LanguageContext = createContext();

export function LanguageProvider({ children }) {
  const [lang, setLang] = useState('vi'); // default Vietnamese

  const t = useCallback((key) => {
    return translations[lang]?.[key] || translations['vi']?.[key] || key;
  }, [lang]);

  const toggleLang = () => setLang(prev => prev === 'vi' ? 'en' : 'vi');

  return (
    <LanguageContext.Provider value={{ lang, setLang, toggleLang, t }}>
      {children}
    </LanguageContext.Provider>
  );
}

export const useLang = () => useContext(LanguageContext);
