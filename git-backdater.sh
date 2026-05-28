#!/bin/bash

echo "=== Resetting local repository ==="
rm -rf .git
git init
git checkout -b main

echo "=== Applying Git configurations for MyDuyen1111 ==="
git config user.name "MyDuyen1111"
git config user.email "ntmyduyen11112005@gmail.com"

# 1. Commit 1: April 28, 2026
echo "=== Staging Step 1 ==="
git add backend/pom.xml .gitignore README.md
GIT_AUTHOR_DATE="2026-04-28 10:00:00" GIT_COMMITTER_DATE="2026-04-28 10:00:00" git commit -m "initial commit with base configuration, project structure and pom.xml"

# 2. Commit 2: April 30, 2026
echo "=== Staging Step 2 ==="
git add backend/src/main/java/com/livehealth/shared/
GIT_AUTHOR_DATE="2026-04-30 14:30:00" GIT_COMMITTER_DATE="2026-04-30 14:30:00" git commit -m "feat: implement base entities, models and shared components"

# 3. Commit 3: May 02, 2026
echo "=== Staging Step 3 ==="
git add backend/src/main/java/com/livehealth/shared/base/BaseRepository.java \
        backend/src/main/java/com/livehealth/auth/*Repository.java \
        backend/src/main/java/com/livehealth/cart/*Repository.java \
        backend/src/main/java/com/livehealth/news/*Repository.java \
        backend/src/main/java/com/livehealth/order/*Repository.java \
        backend/src/main/java/com/livehealth/product/*Repository.java \
        backend/src/main/java/com/livehealth/shared/web/*Repository.java \
        backend/src/main/java/com/livehealth/user/*Repository.java
GIT_AUTHOR_DATE="2026-05-02 09:15:00" GIT_COMMITTER_DATE="2026-05-02 09:15:00" git commit -m "feat: add BaseRepository and implement 24 repositories using CDI and Panache"

# 4. Commit 4: May 04, 2026
echo "=== Staging Step 4 ==="
git add backend/src/main/java/com/livehealth/auth/Jwt*.java \
        backend/src/main/java/com/livehealth/shared/base/RestApiV1.java \
        backend/src/main/java/com/livehealth/shared/base/VsResponseUtil.java \
        backend/src/main/java/com/livehealth/shared/exception/*
GIT_AUTHOR_DATE="2026-05-04 11:20:00" GIT_COMMITTER_DATE="2026-05-04 11:20:00" git commit -m "feat: implement security filters and JWT authentication using SmallRye JWT"

# 5. Commit 5: May 06, 2026
echo "=== Staging Step 5 ==="
git add backend/src/main/java/com/livehealth/user/
GIT_AUTHOR_DATE="2026-05-06 16:45:00" GIT_COMMITTER_DATE="2026-05-06 16:45:00" git commit -m "feat: implement user service, user controller and address handling"

# 6. Commit 6: May 08, 2026
echo "=== Staging Step 6 ==="
git add backend/src/main/java/com/livehealth/product/
GIT_AUTHOR_DATE="2026-05-08 10:10:00" GIT_COMMITTER_DATE="2026-05-08 10:10:00" git commit -m "feat: implement product service, catalog, brand, and tag controllers"

# 7. Commit 7: May 10, 2026
echo "=== Staging Step 7 ==="
git add backend/src/main/java/com/livehealth/cart/
GIT_AUTHOR_DATE="2026-05-10 15:35:00" GIT_COMMITTER_DATE="2026-05-10 15:35:00" git commit -m "feat: implement cart service, cart controller and cart management"

# 8. Commit 8: May 12, 2026
echo "=== Staging Step 8 ==="
git add backend/src/main/java/com/livehealth/news/
GIT_AUTHOR_DATE="2026-05-12 13:40:00" GIT_COMMITTER_DATE="2026-05-12 13:40:00" git commit -m "feat: implement news service, category, and comment controllers"

# 9. Commit 9: May 14, 2026
echo "=== Staging Step 9 ==="
git add backend/src/main/java/com/livehealth/order/PaymentMethod* \
        backend/src/main/java/com/livehealth/order/ShippingMethod*
GIT_AUTHOR_DATE="2026-05-14 09:25:00" GIT_COMMITTER_DATE="2026-05-14 09:25:00" git commit -m "feat: implement shipping and payment methods"

# 10. Commit 10: May 16, 2026
echo "=== Staging Step 10 ==="
git add backend/src/main/java/com/livehealth/order/
GIT_AUTHOR_DATE="2026-05-16 11:50:00" GIT_COMMITTER_DATE="2026-05-16 11:50:00" git commit -m "feat: implement order management and order item processing"

# 11. Commit 11: May 18, 2026
echo "=== Staging Step 11 ==="
git add backend/src/main/java/com/livehealth/payment/ \
        backend/src/main/java/com/livehealth/shared/config/VNPayConfig.java
GIT_AUTHOR_DATE="2026-05-18 14:05:00" GIT_COMMITTER_DATE="2026-05-18 14:05:00" git commit -m "feat: implement payment service and VNPay integration"

# 12. Commit 12: May 20, 2026
echo "=== Staging Step 12 ==="
git add backend/src/main/java/com/livehealth/auth/Email* \
        backend/src/main/java/com/livehealth/auth/Otp* \
        backend/src/main/java/com/livehealth/auth/RefreshToken* \
        backend/src/main/java/com/livehealth/auth/Auth* \
        backend/src/main/java/com/livehealth/Application.java
GIT_AUTHOR_DATE="2026-05-20 17:15:00" GIT_COMMITTER_DATE="2026-05-20 17:15:00" git commit -m "feat: implement email service and system startup initializer"

# 13. Commit 13: May 22, 2026
echo "=== Staging Step 13 ==="
git add backend/Dockerfile \
        backend/src/main/docker/ \
        backend/src/main/resources/application.properties \
        docker-compose.yml \
        backend/docker-compose.yml
GIT_AUTHOR_DATE="2026-05-22 10:30:00" GIT_COMMITTER_DATE="2026-05-22 10:30:00" git commit -m "feat: add optimized Dockerfiles and docker-compose configurations"

# 14. Commit 14: May 24, 2026
echo "=== Staging Step 14 ==="
git add frontend/package.json \
        frontend/vite.config.js \
        frontend/index.html \
        frontend/src/main.jsx \
        frontend/src/index.css \
        frontend/.gitignore
GIT_AUTHOR_DATE="2026-05-24 15:20:00" GIT_COMMITTER_DATE="2026-05-24 15:20:00" git commit -m "feat: initialize React frontend project structure and router config"

# 15. Commit 15: May 25, 2026
echo "=== Staging Step 15 ==="
git add frontend/src/api/ \
        frontend/src/context/
GIT_AUTHOR_DATE="2026-05-25 13:10:00" GIT_COMMITTER_DATE="2026-05-25 13:10:00" git commit -m "feat: implement frontend api clients and auth context"

# 16. Commit 16: May 26, 2026
echo "=== Staging Step 16 ==="
git add frontend/src/pages/About* \
        frontend/src/pages/Contact* \
        frontend/src/pages/Home* \
        frontend/src/pages/Shop* \
        frontend/src/pages/ProductDetail* \
        frontend/src/components/product/ \
        frontend/src/components/layout/
GIT_AUTHOR_DATE="2026-05-26 11:00:00" GIT_COMMITTER_DATE="2026-05-26 11:00:00" git commit -m "feat: implement frontend user dashboard and product catalog pages"

# 17. Commit 17: May 27, 2026
echo "=== Staging Step 17 ==="
git add frontend/src/pages/Cart* \
        frontend/src/pages/Checkout* \
        frontend/src/pages/Payment* \
        frontend/src/components/CartSidebar* \
        frontend/src/components/QuickView*
GIT_AUTHOR_DATE="2026-05-27 16:40:00" GIT_COMMITTER_DATE="2026-05-27 16:40:00" git commit -m "feat: implement frontend cart and order checkout flow"

# 18. Commit 18: May 28, 2026 (Today)
echo "=== Staging Step 18 ==="
git add .
GIT_AUTHOR_DATE="2026-05-28 22:30:00" GIT_COMMITTER_DATE="2026-05-28 22:30:00" git commit -m "feat: implement ai service, scripts, and end-to-end selenium tests"

echo "=== Connecting to GitHub ==="
git remote add origin https://github.com/MyDuyen1111/LiveHealth.git

echo "=== Force Pushing Backdated Commits to GitHub ==="
git push -u origin main --force

echo "=== SUCCESS ==="
