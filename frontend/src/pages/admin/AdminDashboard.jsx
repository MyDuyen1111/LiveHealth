import { LayoutDashboard } from 'lucide-react';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const AdminDashboard = () => {
  const { t } = useLang();
  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.dashboard')}</h1>
      </div>
      <div className="adm-card" style={{ padding: 40, textAlign: 'center' }}>
        <LayoutDashboard size={48} color="#00B207" />
        <h2 style={{ marginTop: 16 }}>{t('admin.welcome')}</h2>
        <p style={{ color: '#667085' }}>{t('admin.welcomeDesc')}</p>
      </div>
    </div>
  );
};

export default AdminDashboard;
