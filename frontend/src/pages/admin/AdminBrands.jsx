import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminBrandApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import './Admin.css';

const AdminBrands = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const { addToast } = useToast();

  const load = () => { setLoading(true); adminBrandApi.getAll(1, 100).then(d => setItems(d?.items || [])).catch(() => {}).finally(() => setLoading(false)); };
  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm({ name: '', description: '' }); setModal('create'); setMsg(''); };
  const openEdit = (item) => { setForm({ name: item.name || '', description: item.description || '' }); setModal(item); setMsg(''); };
  const close = () => setModal(null);

  const handleSave = async () => {
    setSaving(true);
    try { modal === 'create' ? await adminBrandApi.create(form) : await adminBrandApi.update(modal.id, form); addToast(modal === 'create' ? 'Thêm thương hiệu thành công!' : 'Cập nhật thành công!'); close(); load(); } catch (e) { addToast(e.message, 'error'); }
    setSaving(false);
  };

  const handleDelete = async (id) => { if (!confirm('Xóa?')) return; try { await adminBrandApi.delete(id); addToast('Đã xóa!'); load(); } catch (e) { addToast(e.message, 'error'); } };

  const handleLogo = async (id, file) => {
    try { await adminBrandApi.uploadLogo(id, file); addToast('Upload logo thành công!'); load(); } catch (e) { addToast(e.message, 'error'); }
  };

  return (
    <div>
      <div className="adm-page-header"><h1 className="adm-page-title">Brands</h1><button className="adm-btn adm-btn-primary" onClick={openCreate}><Plus size={16}/> Add Brand</button></div>
      <div className="adm-card">
        {loading ? <div className="adm-empty">Loading...</div> : items.length === 0 ? <div className="adm-empty">No brands yet.</div> : (
          <table className="adm-table">
            <thead><tr><th>Logo</th><th>Name</th><th>Description</th><th>Actions</th></tr></thead>
            <tbody>{items.map(item => (
              <tr key={item.id}>
                <td>{item.logoUrl ? <img src={item.logoUrl} className="adm-table-img" alt=""/> : '—'}</td>
                <td><strong>{item.name}</strong></td>
                <td>{item.description || '—'}</td>
                <td><div className="adm-table-actions">
                  <label className="adm-btn adm-btn-outline adm-btn-sm" style={{cursor:'pointer'}}>📷<input type="file" hidden accept="image/*" onChange={e => e.target.files[0] && handleLogo(item.id, e.target.files[0])}/></label>
                  <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openEdit(item)}><Pencil size={14}/></button>
                  <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={() => handleDelete(item.id)}><Trash2 size={14}/></button>
                </div></td>
              </tr>
            ))}</tbody>
          </table>
        )}
      </div>
      {modal && (
        <div className="adm-modal-overlay" onClick={close}><div className="adm-modal" onClick={e => e.stopPropagation()}>
          <h3 className="adm-modal-title">{modal === 'create' ? 'Add Brand' : 'Edit Brand'}</h3>
          {msg && <div className="adm-msg adm-msg-error">{msg}</div>}
          <div className="adm-form-group"><label>Name</label><input value={form.name} onChange={e => setForm({...form, name: e.target.value})}/></div>
          <div className="adm-form-group"><label>Description</label><textarea value={form.description} onChange={e => setForm({...form, description: e.target.value})}/></div>
          <div className="adm-modal-actions"><button className="adm-btn adm-btn-outline" onClick={close}>Cancel</button><button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>{saving ? 'Saving...' : 'Save'}</button></div>
        </div></div>
      )}
    </div>
  );
};

export default AdminBrands;
