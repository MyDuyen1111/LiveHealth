import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminBrandApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const AdminBrands = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const { addToast } = useToast();
  const { t } = useLang();

  const load = () => { 
    setLoading(true); 
    adminBrandApi.getAll(1, 100)
      .then(d => setItems(d?.items || []))
      .catch(() => {})
      .finally(() => setLoading(false)); 
  };
  
  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm({ name: '', description: '' }); setModal('create'); setMsg(''); };
  const openEdit = (item) => { setForm({ name: item.name || '', description: item.description || '' }); setModal(item); setMsg(''); };
  const close = () => setModal(null);

  const handleSave = async () => {
    setSaving(true);
    try { 
      if (modal === 'create') {
        await adminBrandApi.create(form);
        addToast(t('admin.addSuccess'));
      } else {
        await adminBrandApi.update(modal.id, form);
        addToast(t('admin.updateSuccess'));
      }
      close(); 
      load(); 
    } catch (e) { 
      addToast(e.message, 'error'); 
    }
    setSaving(false);
  };

  const handleDelete = async (id) => { 
    if (!confirm(t('admin.deleteConfirm'))) return; 
    try { 
      await adminBrandApi.delete(id); 
      addToast(t('admin.deleteSuccess')); 
      load(); 
    } catch (e) { 
      addToast(e.message, 'error'); 
    } 
  };

  const handleLogo = async (id, file) => {
    try { 
      await adminBrandApi.uploadLogo(id, file); 
      addToast(t('admin.uploadSuccess')); 
      load(); 
    } catch (e) { 
      addToast(e.message, 'error'); 
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.brands')}</h1>
        <button className="adm-btn adm-btn-primary" onClick={openCreate}>
          <Plus size={16}/> {t('admin.add')} {t('admin.brand')}
        </button>
      </div>
      <div className="adm-card">
        {loading ? (
          <div className="adm-empty">{t('admin.loading')}</div>
        ) : items.length === 0 ? (
          <div className="adm-empty">{t('admin.noData')}</div>
        ) : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>{t('admin.logo')}</th>
                <th>{t('admin.name')}</th>
                <th>{t('admin.description')}</th>
                <th>{t('admin.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.logoUrl ? <img src={item.logoUrl} className="adm-table-img" alt=""/> : '—'}</td>
                  <td><strong>{item.name}</strong></td>
                  <td>{item.description || '—'}</td>
                  <td>
                    <div className="adm-table-actions">
                      <label className="adm-btn adm-btn-outline adm-btn-sm" style={{cursor:'pointer'}}>
                        📷
                        <input type="file" hidden accept="image/*" onChange={e => e.target.files[0] && handleLogo(item.id, e.target.files[0])}/>
                      </label>
                      <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openEdit(item)}><Pencil size={14}/></button>
                      <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={() => handleDelete(item.id)}><Trash2 size={14}/></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      {modal && (
        <div className="adm-modal-overlay" onClick={close}>
          <div className="adm-modal" onClick={e => e.stopPropagation()}>
            <h3 className="adm-modal-title">
              {modal === 'create' ? `${t('admin.add')} ${t('admin.brand')}` : `${t('admin.edit')} ${t('admin.brand')}`}
            </h3>
            {msg && <div className="adm-msg adm-msg-error">{msg}</div>}
            <div className="adm-form-group">
              <label>{t('admin.name')}</label>
              <input value={form.name} onChange={e => setForm({...form, name: e.target.value})}/>
            </div>
            <div className="adm-form-group">
              <label>{t('admin.description')}</label>
              <textarea value={form.description} onChange={e => setForm({...form, description: e.target.value})}/>
            </div>
            <div className="adm-modal-actions">
              <button className="adm-btn adm-btn-outline" onClick={close}>{t('admin.cancel')}</button>
              <button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>
                {saving ? t('admin.saving') : t('admin.save')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminBrands;
