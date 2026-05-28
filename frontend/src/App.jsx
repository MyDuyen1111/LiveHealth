import { Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import NewsletterPopup from './components/NewsletterPopup';
import Home from './pages/Home';
import Shop from './pages/Shop';
import ProductDetail from './pages/ProductDetail';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import HealthAI from './pages/HealthAI';
import Contact from './pages/Contact';
import About from './pages/About';
import Blog from './pages/Blog';
import BlogDetail from './pages/BlogDetail';
import NotFound from './pages/NotFound';
import Login from './pages/Login';
import Register from './pages/Register';
import AccountDashboard from './pages/AccountDashboard';
import OrderHistory from './pages/OrderHistory';
import OrderDetail from './pages/OrderDetail';
import AccountSettings from './pages/AccountSettings';
import Faq from './pages/Faq';
import PaymentPage from './pages/PaymentPage';
import PaymentReturn from './pages/PaymentReturn';
import ForgotPassword from './pages/ForgotPassword';
import CartSidebar from './components/CartSidebar';
import { useAuth } from './context/AuthContext';

// Admin
import AdminLayout from './components/admin/AdminLayout';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminProducts from './pages/admin/AdminProducts';
import AdminCategories from './pages/admin/AdminCategories';
import AdminBrands from './pages/admin/AdminBrands';
import AdminTags from './pages/admin/AdminTags';
import AdminNews from './pages/admin/AdminNews';
import AdminNewsCategories from './pages/admin/AdminNewsCategories';
import AdminNewsTags from './pages/admin/AdminNewsTags';
import AdminTestimonials from './pages/admin/AdminTestimonials';
import AdminMembers from './pages/admin/AdminMembers';
import AdminOrders from './pages/admin/AdminOrders';
import AdminShipping from './pages/admin/AdminShipping';
import AdminPayment from './pages/admin/AdminPayment';

// Protected route wrapper
function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return null;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return children;
}

// Admin route wrapper
function AdminRoute({ children }) {
  const { isAuthenticated, user, loading } = useAuth();
  if (loading) return null;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />;
  return children;
}

function App() {
  return (
    <Routes>
      {/* Admin routes (no Header/Footer) */}
      <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
        <Route index element={<AdminDashboard />} />
        <Route path="products" element={<AdminProducts />} />
        <Route path="categories" element={<AdminCategories />} />
        <Route path="brands" element={<AdminBrands />} />
        <Route path="tags" element={<AdminTags />} />
        <Route path="news" element={<AdminNews />} />
        <Route path="news-categories" element={<AdminNewsCategories />} />
        <Route path="news-tags" element={<AdminNewsTags />} />
        <Route path="testimonials" element={<AdminTestimonials />} />
        <Route path="members" element={<AdminMembers />} />
        <Route path="orders" element={<AdminOrders />} />
        <Route path="shipping" element={<AdminShipping />} />
        <Route path="payment" element={<AdminPayment />} />
      </Route>

      {/* Customer routes */}
      <Route path="*" element={
        <div className="app-layout">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/shop" element={<Shop />} />
              <Route path="/product/:id" element={<ProductDetail />} />
              <Route path="/cart" element={<ProtectedRoute><Cart /></ProtectedRoute>} />
              <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />
              <Route path="/health-ai" element={<HealthAI />} />
              <Route path="/contact" element={<Contact />} />
              <Route path="/about" element={<About />} />
              <Route path="/blog" element={<Blog />} />
              <Route path="/blog/:id" element={<BlogDetail />} />
              <Route path="/faq" element={<Faq />} />
              <Route path="/payment" element={<ProtectedRoute><PaymentPage /></ProtectedRoute>} />
              <Route path="/payment/vnpay/return" element={<ProtectedRoute><PaymentReturn /></ProtectedRoute>} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/account" element={<ProtectedRoute><AccountDashboard /></ProtectedRoute>} />
              <Route path="/account/orders" element={<ProtectedRoute><OrderHistory /></ProtectedRoute>} />
              <Route path="/account/orders/:id" element={<ProtectedRoute><OrderDetail /></ProtectedRoute>} />
              <Route path="/account/settings" element={<ProtectedRoute><AccountSettings /></ProtectedRoute>} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </main>
          <Footer />
          <CartSidebar />
          <NewsletterPopup />
        </div>
      } />
    </Routes>
  );
}

export default App;
