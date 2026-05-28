import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { orderApi } from '../api/orderApi';
import { formatPrice } from '../utils/format';
import AccountSidebar from '../components/AccountSidebar';
import './OrderHistory.css';

const ITEMS_PER_PAGE = 10;

const OrderHistory = () => {
  const { t } = useLang();
  const [currentPage, setCurrentPage] = useState(1);
  const [orders, setOrders] = useState([]);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    orderApi.getMyOrders(currentPage, ITEMS_PER_PAGE).then(data => {
      setOrders(data?.items || []);
      setTotalPages(data?.meta?.totalPages || 1);
    }).catch(() => setOrders([]));
  }, [currentPage]);

  const getStatusLabel = (status) => {
    switch (status) {
      case 'PROCESSING': return t('dashboard.processing');
      case 'ON_THE_WAY': return t('dashboard.onTheWay');
      case 'COMPLETED': case 'DELIVERED': return t('dashboard.completed');
      default: return status;
    }
  };

  return (
    <div className="oh-wrap">
      {/* Breadcrumb */}
      <div className="oh-breadcrumb">
        <div className="container oh-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('orderHistory.breadcrumb')}</span>
        </div>
      </div>

      <div className="container oh-layout">
        <AccountSidebar activeItem="orders" />

        <div className="oh-main">
          <div className="oh-card">
            <h2 className="oh-title">{t('orderHistory.title')}</h2>

            <div className="oh-table-wrap">
              <table className="oh-table">
                <thead>
                  <tr>
                    <th>{t('dashboard.orderId')}</th>
                    <th>{t('dashboard.date')}</th>
                    <th>{t('dashboard.total')}</th>
                    <th>{t('dashboard.status')}</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id}>
                      <td>#{order.orderNumber || order.id?.substring(0, 6)}</td>
                      <td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : ''}</td>
                      <td>{formatPrice(order.totalAmount)}</td>
                      <td>
                        <span className={`oh-status oh-status--${(order.status || '').toLowerCase()}`}>
                          {getStatusLabel(order.status)}
                        </span>
                      </td>
                      <td>
                        <Link to={`/account/orders/${order.id}`} className="oh-view-detail">
                          {t('dashboard.viewDetails')}
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="oh-pagination">
                <button
                  className="oh-page-btn"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(p => p - 1)}
                >
                  <ChevronLeft size={16} />
                </button>
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i + 1}
                    className={`oh-page-num ${currentPage === i + 1 ? 'active' : ''}`}
                    onClick={() => setCurrentPage(i + 1)}
                  >
                    {i + 1}
                  </button>
                ))}
                <button
                  className="oh-page-btn"
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage(p => p + 1)}
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderHistory;
