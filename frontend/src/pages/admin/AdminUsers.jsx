import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminUserApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const empty = { firstName: '', lastName: '', email: '', phone: '', password: '', role: 'USER' };

const AdminUsers = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(empty);
  const [saving, setSaving] = useState(false);
  const { addToast } = useToast();
  const { t } = useLang();

  const load = () => {
    setLoading(true);
    adminUserApi.getAll(1, 100)
      .then(d => setItems(d?.items || []))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const openCreate = () => {
    setForm({ ...empty });
    setModal('create');
  };

  const openEdit = (item) => {
    setForm({
      firstName: item.firstName || '',
      lastName: item.lastName || '',
      email: item.email || '',
      phone: item.phone || '',
      password: '',
      role: item.role || 'USER'
    });
    setModal(item);
  };

  const close = () => setModal(null);

  const handleSave = async () => {
    setSaving(true);
    try {
      if (modal === 'create') {
        await adminUserApi.create(form);
        addToast(t('admin.userAddSuccess'));
      } else {
        // Exclude password if empty in update
        const updateData = { ...form };
        if (!updateData.password) {
          delete updateData.password;
        }
        await adminUserApi.update(modal.id, updateData);
        addToast(t('admin.userUpdateSuccess'));
      }
      close();
      load();
    } catch (e) {
      addToast(e.message || t('admin.saveFailed'), 'error');
    }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    if (!confirm(t('admin.userDeleteConfirm'))) return;
    try {
      await adminUserApi.delete(id);
      addToast(t('admin.userDeleteSuccess'));
      load();
    } catch (e) {
      addToast(e.message || t('admin.deleteFailed'), 'error');
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.usersTitle')}</h1>
        <button className="adm-btn adm-btn-primary" onClick={openCreate}>
          <Plus size={16} /> {t('admin.addUser')}
        </button>
      </div>
      <div className="adm-card">
        {loading ? (
          <div className="adm-empty">{t('admin.userLoading')}</div>
        ) : items.length === 0 ? (
          <div className="adm-empty">{t('admin.userEmpty')}</div>
        ) : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>{t('admin.userFullName')}</th>
                <th>{t('admin.userEmail')}</th>
                <th>{t('admin.userPhone')}</th>
                <th>{t('admin.userRole')}</th>
                <th>{t('admin.userActions')}</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>
                    <strong>{item.firstName} {item.lastName}</strong>
                  </td>
                  <td>{item.email}</td>
                  <td>{item.phone || '—'}</td>
                  <td>
                    <span className={`adm-status ${item.role === 'ADMIN' ? 'adm-status-delivered' : 'adm-status-pending'}`}>
                      {item.role}
                    </span>
                  </td>
                  <td>
                    <div className="adm-table-actions">
                      <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openEdit(item)}>
                        <Pencil size={14} />
                      </button>
                      <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={() => handleDelete(item.id)}>
                        <Trash2 size={14} />
                      </button>
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
              {modal === 'create' ? t('admin.addUser') : t('admin.editUser')}
            </h3>
            <div className="adm-form-row">
              <div className="adm-form-group">
                <label>{t('admin.userFirstName')}</label>
                <input value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} />
              </div>
              <div className="adm-form-group">
                <label>{t('admin.userLastName')}</label>
                <input value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} />
              </div>
            </div>
            <div className="adm-form-group">
              <label>{t('admin.userEmail')}</label>
              <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} disabled={modal !== 'create'} />
            </div>
            <div className="adm-form-group">
              <label>{t('admin.userPhone')}</label>
              <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
            </div>
            <div className="adm-form-group">
              <label>
                {t('admin.userPassword')}{' '}
                {modal !== 'create' && t('admin.userPasswordPlaceholder')}
              </label>
              <input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} />
            </div>
            <div className="adm-form-group">
              <label>{t('admin.userRole')}</label>
              <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
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

export default AdminUsers;
