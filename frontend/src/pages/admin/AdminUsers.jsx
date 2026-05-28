import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminUserApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import './Admin.css';

const empty = { firstName: '', lastName: '', email: '', phone: '', password: '', role: 'USER' };

const AdminUsers = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(empty);
  const [saving, setSaving] = useState(false);
  const { addToast } = useToast();

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
        addToast('Tạo người dùng thành công!');
      } else {
        // Exclude password if empty in update
        const updateData = { ...form };
        if (!updateData.password) {
          delete updateData.password;
        }
        await adminUserApi.update(modal.id, updateData);
        addToast('Cập nhật người dùng thành công!');
      }
      close();
      load();
    } catch (e) {
      addToast(e.message || 'Lưu thất bại', 'error');
    }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    if (!confirm('Bạn có chắc chắn muốn xóa tài khoản này không?')) return;
    try {
      await adminUserApi.delete(id);
      addToast('Xóa tài khoản thành công!');
      load();
    } catch (e) {
      addToast(e.message || 'Xóa thất bại', 'error');
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">Quản lý Tài khoản</h1>
        <button className="adm-btn adm-btn-primary" onClick={openCreate}>
          <Plus size={16} /> Thêm tài khoản
        </button>
      </div>
      <div className="adm-card">
        {loading ? (
          <div className="adm-empty">Đang tải...</div>
        ) : items.length === 0 ? (
          <div className="adm-empty">Chưa có người dùng nào.</div>
        ) : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>Họ và tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Vai trò</th>
                <th>Hành động</th>
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
              {modal === 'create' ? 'Thêm tài khoản' : 'Sửa tài khoản'}
            </h3>
            <div className="adm-form-row">
              <div className="adm-form-group">
                <label>Họ (First Name)</label>
                <input value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} />
              </div>
              <div className="adm-form-group">
                <label>Tên (Last Name)</label>
                <input value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} />
              </div>
            </div>
            <div className="adm-form-group">
              <label>Email</label>
              <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} disabled={modal !== 'create'} />
            </div>
            <div className="adm-form-group">
              <label>Số điện thoại</label>
              <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
            </div>
            <div className="adm-form-group">
              <label>Mật khẩu {modal !== 'create' && '(để trống nếu không đổi)'}</label>
              <input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} />
            </div>
            <div className="adm-form-group">
              <label>Vai trò</label>
              <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
            <div className="adm-modal-actions">
              <button className="adm-btn adm-btn-outline" onClick={close}>Hủy</button>
              <button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>
                {saving ? 'Đang lưu...' : 'Lưu'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminUsers;
