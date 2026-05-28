import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminPaymentApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import './Admin.css';

const empty = { name:'', description:'' };
const AdminPayment = () => {
  const [items,setItems]=useState([]); const [loading,setLoading]=useState(true);
  const [modal,setModal]=useState(null); const [form,setForm]=useState(empty);
  const [saving,setSaving]=useState(false); const [msg,setMsg]=useState('');
  const { addToast } = useToast();

  const load=()=>{setLoading(true);adminPaymentApi.getAll(1,50).then(d=>setItems(d?.items||[])).catch(()=>{}).finally(()=>setLoading(false));};
  useEffect(()=>{load();},[]);

  const openCreate=()=>{setForm({...empty});setModal('create');setMsg('');};
  const openEdit=(item)=>{setForm({name:item.name||'',description:item.description||''});setModal(item);setMsg('');};
  const close=()=>setModal(null);
  const handleSave=async()=>{setSaving(true);try{modal==='create'?await adminPaymentApi.create(form):await adminPaymentApi.update(modal.id,form);close();load();}catch(e){addToast(e.message, 'error');}setSaving(false);};
  const handleDelete=async(id)=>{if(!confirm('Xóa?'))return;try{await adminPaymentApi.delete(id);addToast('Đã xóa!');load();}catch(e){addToast(e.message, 'error');}};

  return (
    <div>
      <div className="adm-page-header"><h1 className="adm-page-title">Payment Methods</h1><button className="adm-btn adm-btn-primary" onClick={openCreate}><Plus size={16}/> Add</button></div>
      <div className="adm-card">
        {loading?<div className="adm-empty">Loading...</div>:items.length===0?<div className="adm-empty">No items.</div>:(
          <table className="adm-table"><thead><tr><th>Name</th><th>Description</th><th>Actions</th></tr></thead><tbody>{items.map(item=>(
            <tr key={item.id}><td><strong>{item.name}</strong></td><td>{item.description||'—'}</td>
            <td><div className="adm-table-actions"><button className="adm-btn adm-btn-outline adm-btn-sm" onClick={()=>openEdit(item)}><Pencil size={14}/></button><button className="adm-btn adm-btn-danger adm-btn-sm" onClick={()=>handleDelete(item.id)}><Trash2 size={14}/></button></div></td>
            </tr>))}</tbody></table>
        )}
      </div>
      {modal&&(<div className="adm-modal-overlay" onClick={close}><div className="adm-modal" onClick={e=>e.stopPropagation()}>
        <h3 className="adm-modal-title">{modal==='create'?'Add':'Edit'} Payment Method</h3>{msg&&<div className="adm-msg adm-msg-error">{msg}</div>}
        <div className="adm-form-group"><label>Name</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></div>
        <div className="adm-form-group"><label>Description</label><textarea value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></div>
        <div className="adm-modal-actions"><button className="adm-btn adm-btn-outline" onClick={close}>Cancel</button><button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
      </div></div>)}
    </div>
  );
};
export default AdminPayment;
