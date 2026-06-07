const express = require('express');
const Database = require('better-sqlite3');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname)));

const db = new Database('shuyic.db');

const codeStore = {};

function initDatabase() {
    db.exec(`
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            phone TEXT UNIQUE,
            email TEXT UNIQUE,
            password TEXT DEFAULT '',
            role TEXT DEFAULT '普通用户',
            status TEXT DEFAULT '正常',
            points INTEGER DEFAULT 0,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS books (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            author TEXT NOT NULL,
            category TEXT,
            type TEXT DEFAULT '纸质书',
            price REAL DEFAULT 0,
            original_price REAL,
            cover_url TEXT,
            description TEXT,
            status TEXT DEFAULT '在售',
            is_vip_only INTEGER DEFAULT 0,
            views INTEGER DEFAULT 0,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS articles (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            author TEXT NOT NULL,
            category TEXT,
            content TEXT,
            status TEXT DEFAULT '已发布',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS orders (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_no TEXT UNIQUE NOT NULL,
            user_id INTEGER,
            total_amount REAL DEFAULT 0,
            status TEXT DEFAULT '待支付',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );

        CREATE TABLE IF NOT EXISTS order_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_id INTEGER,
            book_id INTEGER,
            quantity INTEGER DEFAULT 1,
            price REAL,
            FOREIGN KEY (order_id) REFERENCES orders(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );

        CREATE TABLE IF NOT EXISTS comments (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id INTEGER,
            content TEXT,
            rating INTEGER DEFAULT 5,
            status TEXT DEFAULT '正常',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );

        CREATE TABLE IF NOT EXISTS authors (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            health_score INTEGER DEFAULT 100,
            status TEXT DEFAULT '正常',
            violations INTEGER DEFAULT 0,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS admin (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS vip_members (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            level INTEGER DEFAULT 1,
            expire_date DATETIME,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );

        CREATE TABLE IF NOT EXISTS points_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            amount INTEGER,
            type TEXT,
            description TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );

        CREATE TABLE IF NOT EXISTS cart (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id INTEGER,
            quantity INTEGER DEFAULT 1,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );

        CREATE TABLE IF NOT EXISTS favorites (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id INTEGER,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );

        CREATE TABLE IF NOT EXISTS reading_progress (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id INTEGER,
            progress REAL DEFAULT 0,
            last_read DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );

        CREATE TABLE IF NOT EXISTS bookmarks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id INTEGER,
            chapter TEXT,
            position INTEGER,
            note TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (book_id) REFERENCES books(id)
        );
    `);

    const adminExists = db.prepare('SELECT COUNT(*) as count FROM admin').get();
    if (adminExists.count === 0) {
        db.prepare('INSERT INTO admin (username, password) VALUES (?, ?)').run('admin', 'admin123');
    }

    const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get();
    if (userCount.count === 0) {
        initDemoData();
    }
}

function initDemoData() {
    const insertUser = db.prepare('INSERT INTO users (username, phone, email, role, status, points) VALUES (?, ?, ?, ?, ?, ?)');
    insertUser.run('张三', '13800138001', 'zhangsan@test.com', 'VIP会员', '正常', 8888);
    insertUser.run('李四', '13800138002', 'lisi@test.com', '普通用户', '正常', 500);
    insertUser.run('王五', '13800138003', 'wangwu@test.com', '作家', '正常', 1200);
    insertUser.run('赵六', '13800138004', 'zhaoliu@test.com', '管理员', '正常', 0);

    const insertBook = db.prepare('INSERT INTO books (title, author, category, type, price, original_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)');
    insertBook.run('Python编程从入门到精通', 'Python大师', '编程技术', '电子书', 29.9, 59.9, '在售');
    insertBook.run('JavaScript高级编程', '前端专家', '编程技术', '电子书', 39.9, 79.9, '在售');
    insertBook.run('Java核心技术卷I', 'Cay S. Horstmann', '编程技术', '纸质书', 49.9, 99.9, '在售');
    insertBook.run('数据结构与算法分析', 'Robert Sedgewick', '编程技术', '纸质书', 44.9, 89.9, '在售');
    insertBook.run('百年孤独', '马尔克斯', '文学小说', '纸质书', 19.9, 39.9, '在售');
    insertBook.run('活着', '余华', '文学小说', '电子书', 24.9, 49.9, '在售');
    insertBook.run('平凡的世界', '路遥', '文学小说', '纸质书', 34.9, 69.9, '在售');
    insertBook.run('围城', '钱钟书', '文学小说', '电子书', 22.9, 45.9, '在售');
    insertBook.run('穷爸爸富爸爸', '清崎', '经管励志', '纸质书', 24.9, 49.9, '在售');
    insertBook.run('原则', '瑞·达利欧', '经管励志', '纸质书', 39.9, 79.9, '在售');
    insertBook.run('思考，快与慢', '丹尼尔·卡尼曼', '经管励志', '纸质书', 34.9, 69.9, '在售');
    insertBook.run('高效能人士的七个习惯', '史蒂芬·柯维', '经管励志', '电子书', 29.9, 59.9, '在售');
    insertBook.run('考研英语词汇速记', '新东方', '教育考试', '纸质书', 34.9, 69.9, '在售');
    insertBook.run('CPA注会通关宝典', '会计名师', '教育考试', '纸质书', 49.9, 99.9, '在售');
    insertBook.run('公务员考试行测', '华图教育', '教育考试', '纸质书', 39.9, 79.9, '在售');
    insertBook.run('雅思阅读高分技巧', '环球雅思', '教育考试', '电子书', 44.9, 89.9, '在售');

    const insertArticle = db.prepare('INSERT INTO articles (title, author, category, content, status) VALUES (?, ?, ?, ?, ?)');
    insertArticle.run('如何写出优雅的代码', '技术大师', '科技', '本文分享了一些编写高质量代码的技巧...', '已发布');
    insertArticle.run('春天的故事', '作家', '散文', '春天来了，万物复苏...', '已发布');

    const insertOrder = db.prepare('INSERT INTO orders (order_no, user_id, total_amount, status) VALUES (?, ?, ?, ?)');
    insertOrder.run('DD20260520001', 1, 49.9, '已完成');
    insertOrder.run('DD20260519002', 2, 29.9, '处理中');
    insertOrder.run('DD20260518003', 3, 69.9, '已完成');

    const insertComment = db.prepare('INSERT INTO comments (user_id, book_id, content, rating) VALUES (?, ?, ?, ?)');
    insertComment.run(1, 1, '这本书真的很棒！', 5);
    insertComment.run(2, 2, '内容很实用，推荐', 4);

    const insertAuthor = db.prepare('INSERT INTO authors (name, health_score, status, violations) VALUES (?, ?, ?, ?)');
    insertAuthor.run('王五', 100, '正常', 0);
    insertAuthor.run('技术大师', 85, '正常', 1);

    const insertVIP = db.prepare('INSERT INTO vip_members (user_id, level, expire_date) VALUES (?, ?, ?)');
    insertVIP.run(1, 5, '2027-05-20');

    console.log('演示数据初始化完成');
}

app.post('/api/admin/login', (req, res) => {
    const { username, password } = req.body;
    const admin = db.prepare('SELECT * FROM admin WHERE username = ? AND password = ?').get(username, password);
    if (admin) {
        res.json({ success: true, username: admin.username });
    } else {
        res.json({ success: false, message: '用户名或密码错误' });
    }
});

app.post('/api/user/code/send', (req, res) => {
    const { phone } = req.body;
    const code = Math.floor(100000 + Math.random() * 900000).toString();
    codeStore[phone] = { code, expire: Date.now() + 180000 };
    console.log(`验证码发送成功: ${phone} -> ${code}`);
    res.json({ success: true, message: '验证码已发送' });
});

app.post('/api/user/login', (req, res) => {
    const { account, password } = req.body;
    let user = db.prepare('SELECT * FROM users WHERE phone = ? AND password = ?').get(account, password);
    if (!user) {
        user = db.prepare('SELECT * FROM users WHERE email = ? AND password = ?').get(account, password);
    }
    if (!user) {
        user = db.prepare('SELECT * FROM users WHERE username = ? AND password = ?').get(account, password);
    }
    if (user && user.status === '正常') {
        res.json({ success: true, user: { id: user.id, username: user.username, phone: user.phone, role: user.role } });
    } else {
        res.json({ success: false, message: '账号或密码错误' });
    }
});

app.post('/api/user/login/code', (req, res) => {
    const { phone, code } = req.body;
    const stored = codeStore[phone];
    if (stored && stored.code === code && Date.now() < stored.expire) {
        let user = db.prepare('SELECT * FROM users WHERE phone = ?').get(phone);
        if (!user) {
            const result = db.prepare('INSERT INTO users (username, phone, role) VALUES (?, ?, "普通用户")').run('用户_' + phone.slice(-4), phone);
            user = { id: result.lastInsertRowid, username: '用户_' + phone.slice(-4), phone, role: '普通用户' };
        }
        res.json({ success: true, user: { id: user.id, username: user.username, phone: user.phone, role: user.role } });
    } else {
        res.json({ success: false, message: '验证码错误或已过期' });
    }
});

app.post('/api/user/register', (req, res) => {
    const { phone, code, password } = req.body;
    const stored = codeStore[phone];
    if (!stored || stored.code !== code || Date.now() >= stored.expire) {
        res.json({ success: false, message: '验证码错误或已过期' });
        return;
    }
    const exists = db.prepare('SELECT * FROM users WHERE phone = ?').get(phone);
    if (exists) {
        res.json({ success: false, message: '该手机号已注册' });
        return;
    }
    const result = db.prepare('INSERT INTO users (username, phone, password, role) VALUES (?, ?, ?, "普通用户")').run('用户_' + phone.slice(-4), phone, password);
    res.json({ success: true, id: result.lastInsertRowid });
});

app.post('/api/user/password/reset', (req, res) => {
    const { phone, code, password } = req.body;
    const stored = codeStore[phone];
    if (!stored || stored.code !== code || Date.now() >= stored.expire) {
        res.json({ success: false, message: '验证码错误或已过期' });
        return;
    }
    db.prepare('UPDATE users SET password = ? WHERE phone = ?').run(password, phone);
    res.json({ success: true });
});

app.put('/api/user/bind/phone', (req, res) => {
    const { id, phone, code } = req.body;
    const stored = codeStore[phone];
    if (!stored || stored.code !== code || Date.now() >= stored.expire) {
        res.json({ success: false, message: '验证码错误或已过期' });
        return;
    }
    db.prepare('UPDATE users SET phone = ? WHERE id = ?').run(phone, id);
    res.json({ success: true });
});

app.delete('/api/user/logout', (req, res) => {
    res.json({ success: true });
});

app.get('/api/user/info/:id', (req, res) => {
    const { id } = req.params;
    const user = db.prepare('SELECT * FROM users WHERE id = ?').get(id);
    if (user) {
        const vip = db.prepare('SELECT * FROM vip_members WHERE user_id = ?').get(id);
        res.json({ ...user, vip });
    } else {
        res.json(null);
    }
});

app.get('/api/cart/:userId', (req, res) => {
    const { userId } = req.params;
    const cart = db.prepare(`
        SELECT c.*, b.title, b.author, b.price, b.cover_url 
        FROM cart c 
        LEFT JOIN books b ON c.book_id = b.id 
        WHERE c.user_id = ?
    `).all(userId);
    res.json(cart);
});

app.post('/api/cart/add', (req, res) => {
    const { userId, bookId, quantity } = req.body;
    const exists = db.prepare('SELECT * FROM cart WHERE user_id = ? AND book_id = ?').get(userId, bookId);
    if (exists) {
        db.prepare('UPDATE cart SET quantity = quantity + ? WHERE id = ?').run(quantity || 1, exists.id);
    } else {
        db.prepare('INSERT INTO cart (user_id, book_id, quantity) VALUES (?, ?, ?)').run(userId, bookId, quantity || 1);
    }
    res.json({ success: true });
});

app.put('/api/cart/update', (req, res) => {
    const { id, quantity } = req.body;
    if (quantity <= 0) {
        db.prepare('DELETE FROM cart WHERE id = ?').run(id);
    } else {
        db.prepare('UPDATE cart SET quantity = ? WHERE id = ?').run(quantity, id);
    }
    res.json({ success: true });
});

app.delete('/api/cart/remove/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM cart WHERE id = ?').run(id);
    res.json({ success: true });
});

app.delete('/api/cart/clear/:userId', (req, res) => {
    const { userId } = req.params;
    db.prepare('DELETE FROM cart WHERE user_id = ?').run(userId);
    res.json({ success: true });
});

app.get('/api/favorites/:userId', (req, res) => {
    const { userId } = req.params;
    const favorites = db.prepare(`
        SELECT f.*, b.title, b.author, b.price, b.cover_url 
        FROM favorites f 
        LEFT JOIN books b ON f.book_id = b.id 
        WHERE f.user_id = ?
    `).all(userId);
    res.json(favorites);
});

app.post('/api/favorites/add', (req, res) => {
    const { userId, bookId } = req.body;
    const exists = db.prepare('SELECT * FROM favorites WHERE user_id = ? AND book_id = ?').get(userId, bookId);
    if (!exists) {
        db.prepare('INSERT INTO favorites (user_id, book_id) VALUES (?, ?)').run(userId, bookId);
    }
    res.json({ success: true });
});

app.delete('/api/favorites/remove/:userId/:bookId', (req, res) => {
    const { userId, bookId } = req.params;
    db.prepare('DELETE FROM favorites WHERE user_id = ? AND book_id = ?').run(userId, bookId);
    res.json({ success: true });
});

app.get('/api/reading/progress/:userId', (req, res) => {
    const { userId } = req.params;
    const progress = db.prepare(`
        SELECT r.*, b.title, b.author 
        FROM reading_progress r 
        LEFT JOIN books b ON r.book_id = b.id 
        WHERE r.user_id = ? 
        ORDER BY r.last_read DESC
    `).all(userId);
    res.json(progress);
});

app.post('/api/reading/progress/save', (req, res) => {
    const { userId, bookId, progress } = req.body;
    const exists = db.prepare('SELECT * FROM reading_progress WHERE user_id = ? AND book_id = ?').get(userId, bookId);
    if (exists) {
        db.prepare('UPDATE reading_progress SET progress = ?, last_read = CURRENT_TIMESTAMP WHERE id = ?').run(progress, exists.id);
    } else {
        db.prepare('INSERT INTO reading_progress (user_id, book_id, progress) VALUES (?, ?, ?)').run(userId, bookId, progress);
    }
    res.json({ success: true });
});

app.get('/api/bookmarks/:userId', (req, res) => {
    const { userId } = req.params;
    const bookmarks = db.prepare(`
        SELECT bm.*, b.title 
        FROM bookmarks bm 
        LEFT JOIN books b ON bm.book_id = b.id 
        WHERE bm.user_id = ? 
        ORDER BY bm.created_at DESC
    `).all(userId);
    res.json(bookmarks);
});

app.post('/api/bookmarks/add', (req, res) => {
    const { userId, bookId, chapter, position, note } = req.body;
    db.prepare('INSERT INTO bookmarks (user_id, book_id, chapter, position, note) VALUES (?, ?, ?, ?, ?)').run(userId, bookId, chapter, position, note);
    res.json({ success: true });
});

app.delete('/api/bookmarks/remove/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM bookmarks WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/search', (req, res) => {
    const { q, type } = req.query;
    let results = [];
    
    if (!type || type === 'books') {
        const books = db.prepare('SELECT * FROM books WHERE status = "在售" AND (title LIKE ? OR author LIKE ?)').all(`%${q}%`, `%${q}%`);
        results = results.concat(books.map(b => ({ ...b, type: 'book' })));
    }
    
    if (!type || type === 'articles') {
        const articles = db.prepare('SELECT * FROM articles WHERE status = "已发布" AND (title LIKE ? OR content LIKE ?)').all(`%${q}%`, `%${q}%`);
        results = results.concat(articles.map(a => ({ ...a, type: 'article' })));
    }
    
    res.json(results);
});

app.get('/api/user/orders/:userId', (req, res) => {
    const { userId } = req.params;
    const orders = db.prepare(`
        SELECT o.*, u.username 
        FROM orders o 
        LEFT JOIN users u ON o.user_id = u.id 
        WHERE o.user_id = ? 
        ORDER BY o.created_at DESC
    `).all(userId);
    
    const ordersWithItems = orders.map(order => {
        const items = db.prepare(`
            SELECT oi.*, b.title, b.author 
            FROM order_items oi 
            LEFT JOIN books b ON oi.book_id = b.id 
            WHERE oi.order_id = ?
        `).all(order.id);
        return { ...order, items };
    });
    
    res.json(ordersWithItems);
});

app.get('/api/stats', (req, res) => {
    const users = db.prepare('SELECT COUNT(*) as count FROM users').get().count;
    const books = db.prepare('SELECT COUNT(*) as count FROM books').get().count;
    const articles = db.prepare('SELECT COUNT(*) as count FROM articles').get().count;
    const comments = db.prepare('SELECT COUNT(*) as count FROM comments').get().count;
    const orders = db.prepare('SELECT COUNT(*) as count FROM orders').get().count;
    const revenue = db.prepare("SELECT COALESCE(SUM(total_amount), 0) as total FROM orders WHERE status = '已完成'").get().total;
    res.json({ users, books, articles, comments, orders, revenue });
});

app.get('/api/users', (req, res) => {
    const users = db.prepare('SELECT * FROM users ORDER BY id DESC').all();
    res.json(users);
});

app.post('/api/users', (req, res) => {
    const { username, phone, email, role, status } = req.body;
    const result = db.prepare('INSERT INTO users (username, phone, email, role, status) VALUES (?, ?, ?, ?, ?)').run(username, phone, email, role, status);
    res.json({ success: true, id: result.lastInsertRowid });
});

app.put('/api/users/:id', (req, res) => {
    const { id } = req.params;
    const { username, phone, email, role, status } = req.body;
    db.prepare('UPDATE users SET username = ?, phone = ?, email = ?, role = ?, status = ? WHERE id = ?').run(username, phone, email, role, status, id);
    res.json({ success: true });
});

app.delete('/api/users/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM users WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/books', (req, res) => {
    const books = db.prepare('SELECT * FROM books ORDER BY id DESC').all();
    res.json(books);
});

app.post('/api/books', (req, res) => {
    const { title, author, category, type, price, original_price, status } = req.body;
    const result = db.prepare('INSERT INTO books (title, author, category, type, price, original_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)').run(title, author, category, type, price, original_price, status);
    res.json({ success: true, id: result.lastInsertRowid });
});

app.put('/api/books/:id', (req, res) => {
    const { id } = req.params;
    const { title, author, category, type, price, original_price, status } = req.body;
    db.prepare('UPDATE books SET title = ?, author = ?, category = ?, type = ?, price = ?, original_price = ?, status = ? WHERE id = ?').run(title, author, category, type, price, original_price, status, id);
    res.json({ success: true });
});

app.delete('/api/books/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM books WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/articles', (req, res) => {
    const articles = db.prepare('SELECT * FROM articles ORDER BY id DESC').all();
    res.json(articles);
});

app.post('/api/articles', (req, res) => {
    const { title, author, category, content, status } = req.body;
    const result = db.prepare('INSERT INTO articles (title, author, category, content, status) VALUES (?, ?, ?, ?, ?)').run(title, author, category, content, status);
    res.json({ success: true, id: result.lastInsertRowid });
});

app.delete('/api/articles/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM articles WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/orders', (req, res) => {
    const orders = db.prepare(`
        SELECT o.*, u.username as user_name 
        FROM orders o 
        LEFT JOIN users u ON o.user_id = u.id 
        ORDER BY o.id DESC
    `).all();
    res.json(orders);
});

app.put('/api/orders/:id', (req, res) => {
    const { id } = req.params;
    const { status } = req.body;
    db.prepare('UPDATE orders SET status = ? WHERE id = ?').run(status, id);
    res.json({ success: true });
});

app.delete('/api/orders/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM order_items WHERE order_id = ?').run(id);
    db.prepare('DELETE FROM orders WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/comments', (req, res) => {
    const comments = db.prepare(`
        SELECT c.*, u.username as user_name, b.title as book_title 
        FROM comments c 
        LEFT JOIN users u ON c.user_id = u.id 
        LEFT JOIN books b ON c.book_id = b.id 
        ORDER BY c.id DESC
    `).all();
    res.json(comments);
});

app.delete('/api/comments/:id', (req, res) => {
    const { id } = req.params;
    db.prepare('DELETE FROM comments WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/authors', (req, res) => {
    const authors = db.prepare('SELECT * FROM authors ORDER BY id DESC').all();
    res.json(authors);
});

app.put('/api/authors/:id/reset', (req, res) => {
    const { id } = req.params;
    db.prepare('UPDATE authors SET health_score = 100, violations = 0, status = "正常" WHERE id = ?').run(id);
    res.json({ success: true });
});

app.get('/api/backup', (req, res) => {
    const backup = {
        users: db.prepare('SELECT * FROM users').all(),
        books: db.prepare('SELECT * FROM books').all(),
        articles: db.prepare('SELECT * FROM articles').all(),
        orders: db.prepare('SELECT * FROM orders').all(),
        comments: db.prepare('SELECT * FROM comments').all(),
        authors: db.prepare('SELECT * FROM authors').all()
    };
    res.json(backup);
});

app.post('/api/backup/restore', (req, res) => {
    const { users, books, articles, orders, comments, authors } = req.body;
    
    if (users) {
        db.exec('DELETE FROM users');
        const insert = db.prepare('INSERT INTO users (username, phone, email, role, status, points) VALUES (?, ?, ?, ?, ?, ?)');
        users.forEach(u => insert.run(u.username, u.phone, u.email, u.role, u.status, u.points || 0));
    }
    if (books) {
        db.exec('DELETE FROM books');
        const insert = db.prepare('INSERT INTO books (title, author, category, type, price, original_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)');
        books.forEach(b => insert.run(b.title, b.author, b.category, b.type, b.price, b.original_price, b.status));
    }
    if (articles) {
        db.exec('DELETE FROM articles');
        const insert = db.prepare('INSERT INTO articles (title, author, category, content, status) VALUES (?, ?, ?, ?, ?)');
        articles.forEach(a => insert.run(a.title, a.author, a.category, a.content, a.status));
    }
    if (orders) {
        db.exec('DELETE FROM orders');
        const insert = db.prepare('INSERT INTO orders (order_no, user_id, total_amount, status) VALUES (?, ?, ?, ?)');
        orders.forEach(o => insert.run(o.order_no, o.user_id, o.total_amount, o.status));
    }
    if (comments) {
        db.exec('DELETE FROM comments');
        const insert = db.prepare('INSERT INTO comments (user_id, book_id, content, rating) VALUES (?, ?, ?, ?)');
        comments.forEach(c => insert.run(c.user_id, c.book_id, c.content, c.rating));
    }
    if (authors) {
        db.exec('DELETE FROM authors');
        const insert = db.prepare('INSERT INTO authors (name, health_score, status, violations) VALUES (?, ?, ?, ?)');
        authors.forEach(a => insert.run(a.name, a.health_score, a.status, a.violations));
    }
    
    res.json({ success: true });
});

app.post('/api/backup/clear', (req, res) => {
    db.exec('DELETE FROM users');
    db.exec('DELETE FROM books');
    db.exec('DELETE FROM articles');
    db.exec('DELETE FROM orders');
    db.exec('DELETE FROM comments');
    db.exec('DELETE FROM authors');
    initDemoData();
    res.json({ success: true });
});

app.get('/api/ebook/books', (req, res) => {
    const books = db.prepare('SELECT * FROM books WHERE status = "在售" ORDER BY created_at DESC').all();
    res.json(books);
});

initDatabase();

app.listen(PORT, () => {
    console.log(`书易创服务已启动: http://localhost:${PORT}`);
    console.log(`管理后台: http://localhost:${PORT}/admin.html`);
    console.log(`电子书: http://localhost:${PORT}/ebook.html`);
});
