import { useState, useEffect } from 'react';
import { adminOrderApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { formatPrice } from '../../utils/format';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const statuses = ['PENDING','PROCESSING','ON_THE_WAY','DELIVERED','COMPLETED'];

const AdminOrders = () => {
  const [items,setItems]=useState([]); const [page,setPage]=useState(1); const [totalPages,setTotalPages]=useState(1);
  const [loading,setLoading]=useState(true);
  const { addToast } = useToast();
  const { t } = useLang();

  const load=()=>{setLoading(true);adminOrderApi.getAll(page,15).then(d=>{setItems(d?.items||[]);setTotalPages(d?.meta?.totalPages||1);}).catch(()=>{}).finally(()=>setLoading(false));};
  useEffect(()=>{load();},[page]);

  const updateStatus=async(id,status)=>{
    try{await adminOrderApi.updateStatus(id,status);addToast('Cập nhật trạng thái thành công!');load();}catch(e){addToast(e.message, 'error');}
  };

  return (
    <div>
      <div className="adm-page-header"><h1 className="adm-page-title">{t('admin.orders')}</h1></div>
      <div className="adm-card">
        {loading?<div className="adm-empty">{t('admin.loading')}</div>:items.length===0?<div className="adm-empty">{t('admin.noData')}</div>:(
          <table className="adm-table">
            <thead><tr><th>{t('admin.orderNumber')}</th><th>{t('admin.date')}</th><th>{t('admin.customer')}</th><th>{t('admin.total')}</th><th>{t('admin.status')}</th><th>{t('admin.updateStatus')}</th></tr></thead>
            <tbody>{items.map(o=>(
              <tr key={o.id}>
                <td><strong>#{o.orderNumber||o.id?.substring(0,6)}</strong></td>
                <td>{o.createdAt?new Date(o.createdAt).toLocaleDateString('vi-VN'):''}</td>
                <td>{o.userEmail||'—'}</td>
                <td>{formatPrice(o.totalAmount||0)}</td>
                <td><span className={`adm-status adm-status-${(o.status||'').toLowerCase()}`}>{t(`admin.orderStatus.${o.status}`)}</span></td>
                <td><select value={o.status||''} onChange={e=>updateStatus(o.id,e.target.value)} style={{padding:'6px 10px',borderRadius:6,border:'1px solid #d0d5dd',fontSize:13}}>
                  {statuses.map(s=><option key={s} value={s}>{t(`admin.orderStatus.${s}`)}</option>)}
                </select></td>
              </tr>
            ))}</tbody>
          </table>
        )}
        {totalPages>1&&<div className="adm-pagination">{[...Array(totalPages)].map((_,i)=><button key={i} className={`adm-pg-btn ${page===i+1?'active':''}`} onClick={()=>setPage(i+1)}>{i+1}</button>)}</div>}
      </div>
    </div>
  );
};
export default AdminOrders;
