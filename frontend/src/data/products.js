export const products = [
  { id: 1,  name: 'Táo Xanh',         category: 'fruit', price: 48000,  img: 'https://images.unsplash.com/photo-1568702846914-96b305d2ebb1?w=400&q=80', rating: 4, oldPrice: 0 },
  { id: 2,  name: 'Cà Chua Bi',        category: 'veg',   price: 36000,  img: 'https://images.unsplash.com/photo-1546470427-e26264be0b11?w=400&q=80', rating: 5, oldPrice: 0 },
  { id: 3,  name: 'Bông Cải Trắng',    category: 'veg',   price: 39000,  img: 'https://images.unsplash.com/photo-1568702846914-96b305d2ebb1?w=400&q=80', rating: 4, oldPrice: 0 },
  { id: 4,  name: 'Bắp Ngô Tươi',      category: 'veg',   price: 15000,  img: 'https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=400&q=80', rating: 5, oldPrice: 0 },
  { id: 5,  name: 'Cam Sành',          category: 'fruit', price: 56000,  img: 'https://images.unsplash.com/photo-1547514701-42782101795e?w=400&q=80', rating: 5, oldPrice: 70000 },
  { id: 6,  name: 'Cà Tím',            category: 'veg',   price: 21000,  img: 'https://images.unsplash.com/photo-1528505086635-4c69d5f10908?w=400&q=80', rating: 3, oldPrice: 0 },
  { id: 7,  name: 'Xà Lách Romaine',   category: 'veg',   price: 19000,  img: 'https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400&q=80', rating: 5, oldPrice: 24000 },
  { id: 8,  name: 'Ớt Chuông Đỏ',      category: 'veg',   price: 45000,  img: 'https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=400&q=80', rating: 4, oldPrice: 54000 },
  { id: 9,  name: 'Thịt Bò Hữu Cơ',   category: 'meat',  price: 250000, img: 'https://images.unsplash.com/photo-1603048297059-4da4265146c9?w=400&q=80', rating: 5, oldPrice: 280000 },
  { id: 10, name: 'Cá Hồi Na Uy',      category: 'fish',  price: 350000, img: 'https://images.unsplash.com/photo-1599084924170-ce170f25c9ba?w=400&q=80', rating: 5, oldPrice: 0 },
  { id: 11, name: 'Sữa Tươi Tiệt Trùng', category: 'dairy', price: 35000,  img: 'https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&q=80', rating: 4, oldPrice: 40000 },
  { id: 12, name: 'Trứng Gà Ta',       category: 'dairy', price: 40000,  img: 'https://images.unsplash.com/photo-1506976785307-8732e854ad03?w=400&q=80', rating: 4, oldPrice: 0 },
  { id: 13, name: 'Cà Rốt Đà Lạt',    category: 'veg',   price: 25000,  img: 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&q=80', rating: 5, oldPrice: 30000 },
  { id: 14, name: 'Thịt Lợn Sạch',     category: 'meat',  price: 150000, img: 'https://images.unsplash.com/photo-1432139555190-58524dae6a55?w=400&q=80', rating: 4, oldPrice: 160000 },
  { id: 15, name: 'Bông Cải Xanh',     category: 'veg',   price: 32000,  img: 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=400&q=80', rating: 5, oldPrice: 0 },
];

export const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
};
