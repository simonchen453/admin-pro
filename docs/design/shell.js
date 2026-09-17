/* ==========================================================================
   设计稿外壳：注入稿件导航条、侧栏、顶栏。
   只服务本目录的静态稿 —— 落地时由 React 组件替代，这个文件不要搬。
   页面通过 body 的 data-page / data-crumb 声明自己的位置；
   data-page="bare" 表示不套外壳（登录页用）。
   ========================================================================== */
(function () {
  'use strict';

  /* 图标统一画在 16×16 网格、1.5 描边、无填充。
     统一的构造规则本身就是设计的一部分 —— 旧版混用了多套图标库。 */
  var I = {
    home:    '<path d="M2.2 7 8 2.4 13.8 7v6.4H2.2z" stroke-linejoin="round"/>',
    board:   '<rect x="2.2" y="2.2" width="5" height="5" rx="1"/><rect x="8.8" y="2.2" width="5" height="5" rx="1"/><rect x="2.2" y="8.8" width="5" height="5" rx="1"/><rect x="8.8" y="8.8" width="5" height="5" rx="1"/>',
    user:    '<circle cx="8" cy="5.6" r="2.6"/><path d="M3 13.6c0-2.6 2.2-4.1 5-4.1s5 1.5 5 4.1" stroke-linecap="round"/>',
    role:    '<path d="M8 2 13 4.2v3.9c0 3-2.1 5-5 5.9-2.9-.9-5-2.9-5-5.9V4.2z" stroke-linejoin="round"/>',
    menu:    '<path d="M5.6 4h8M5.6 8h8M5.6 12h8M2.5 4h.01M2.5 8h.01M2.5 12h.01" stroke-linecap="round"/>',
    dept:    '<rect x="5.8" y="2.1" width="4.4" height="3.6" rx="1"/><rect x="1.8" y="10.3" width="4.4" height="3.6" rx="1"/><rect x="9.8" y="10.3" width="4.4" height="3.6" rx="1"/><path d="M8 5.7v2.5M4 10.3V8.2h8v2.1"/>',
    post:    '<rect x="2" y="4.4" width="12" height="9.2" rx="1.2"/><path d="M5.6 4.4V2.9h4.8v1.5"/>',
    domain:  '<circle cx="8" cy="8" r="6"/><path d="M2 8h12M8 2c1.6 1.7 2.4 3.8 2.4 6S9.6 12.3 8 14c-1.6-1.7-2.4-3.8-2.4-6S6.4 3.7 8 2z"/>',
    config:  '<path d="M2.4 5h4.4M9.8 5H13.6M2.4 11h2M7.4 11h6.2" stroke-linecap="round"/><circle cx="8.3" cy="5" r="1.8"/><circle cx="5.7" cy="11" r="1.8"/>',
    dict:    '<path d="M2.4 2.8h4.9c.9 0 1.5.6 1.5 1.4v9.1c0-.7-.6-1.2-1.5-1.2H2.4zM13.6 2.8H8.7c-.9 0-1.5.6-1.5 1.4v9.1c0-.7.6-1.2 1.5-1.2h4.9z" stroke-linejoin="round"/>',
    session: '<rect x="2" y="2.8" width="12" height="8.4" rx="1.2"/><path d="M5.4 14h5.2" stroke-linecap="round"/>',
    audit:   '<path d="M3.4 2h5.9L12.6 5.3v8.7H3.4z" stroke-linejoin="round"/><path d="M9.1 2v3.4h3.5M5.9 9.2h4.2M5.9 11.4h2.6" stroke-linecap="round"/>',
    server:  '<rect x="2" y="2.4" width="12" height="4.6" rx="1.1"/><rect x="2" y="9" width="12" height="4.6" rx="1.1"/><path d="M4.6 4.7h.01M4.6 11.3h.01" stroke-linecap="round"/>',
    job:     '<circle cx="8" cy="8" r="6"/><path d="M8 4.6v3.7l2.4 1.5" stroke-linecap="round"/>',
    api:     '<path d="M6 11.4 10 4.6M5 6.2 2.2 8 5 9.8M11 6.2 13.8 8 11 9.8" stroke-linecap="round"/>'
  };

  function icon(n) {
    return '<svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.5" aria-hidden="true">' +
           (I[n] || '') + '</svg>';
  }

  /* 导航结构对齐后端真实菜单树。计数是真实数据，不是装饰。
     代码生成器已按产品决定移除。 */
  var NAV = [
    { g: '概览', items: [
      { id: 'home',  label: '工作台',  icon: 'home' },
      { id: 'board', label: '数据看板', icon: 'board' }
    ]},
    { g: '系统管理', items: [
      { id: 'users',   label: '用户管理', icon: 'user',   href: 'users.html',       n: '8' },
      { id: 'roles',   label: '角色权限', icon: 'role',   href: 'permissions.html', n: '6' },
      { id: 'menus',   label: '菜单管理', icon: 'menu' },
      { id: 'depts',   label: '部门管理', icon: 'dept',   n: '3' },
      { id: 'posts',   label: '岗位管理', icon: 'post' },
      { id: 'domains', label: '用户域',   icon: 'domain' },
      { id: 'configs', label: '参数配置', icon: 'config' },
      { id: 'dicts',   label: '字典管理', icon: 'dict' }
    ]},
    { g: '监控与日志', items: [
      { id: 'sessions', label: '在线会话',  icon: 'session', n: '12' },
      { id: 'audit',    label: '审计日志',  icon: 'audit' },
      { id: 'server',   label: '服务器监控', icon: 'server' }
    ]},
    { g: '系统工具', items: [
      { id: 'jobs', label: '定时任务', icon: 'job' },
      { id: 'api',  label: '接口文档', icon: 'api' }
    ]}
  ];

  var PAGES = [
    { href: 'users.html',       label: '用户管理' },
    { href: 'permissions.html', label: '权限矩阵' }
  ];

  var here = location.pathname.split('/').pop() || 'users.html';

  function docnav() {
    return '<nav class="ap-docnav"><b>ADMINPRO / 设计稿</b>' +
      PAGES.map(function (p) {
        return '<a href="' + p.href + '"' + (p.href === here ? ' aria-current="page"' : '') +
               '>' + p.label + '</a>';
      }).join('') +
      '<span class="sp">纯白 / 墨黑 · 参照 x.ai 视觉语言</span></nav>';
  }

  function rail(active) {
    var nav = NAV.map(function (g) {
      return '<div class="ap-nav-g">' + g.g + '</div>' + g.items.map(function (it) {
        return '<a class="ap-nav-i" href="' + (it.href || '#') + '"' +
               (it.id === active ? ' aria-current="page"' : '') + '>' +
               icon(it.icon) + '<span>' + it.label + '</span>' +
               (it.n ? '<span class="ap-nav-n">' + it.n + '</span>' : '') + '</a>';
      }).join('');
    }).join('');

    return '<aside class="ap-rail">' +
      '<div class="ap-brand">' +
        '<span class="ap-logo">' +
          '<svg viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">' +
          '<path d="M9 1.8 15.2 4.6v4.6c0 3.5-2.6 5.9-6.2 7-3.6-1.1-6.2-3.5-6.2-7V4.6z"/>' +
          '<path d="M6.4 8.9 8.3 10.8 11.8 7.3"/></svg>' +
        '</span>' +
        '<span class="ap-brand-t">AdminPro</span>' +
      '</div>' +
      '<nav class="ap-nav">' + nav + '</nav>' +
      '<div class="ap-rail-sep"></div>' +
      '<div class="ap-rail-user">' +
        '<span class="ap-ru-av">超</span>' +
        '<span><span class="ap-ru-n">超级管理员</span><br>' +
        '<span class="ap-ru-r">superadmin</span></span>' +
        '<span class="ap-ru-x"><svg viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M4.8 2.5 9.3 7l-4.5 4.5"/></svg></span>' +
      '</div>' +
    '</aside>';
  }

  function topbar(crumb) {
    var parts = (crumb || '工作台').split('/').map(function (s) { return s.trim(); });
    var trail = parts.map(function (s, i) {
      return (i ? '<i>/</i>' : '') +
        (i === parts.length - 1 ? '<b>' + s + '</b>' : s);
    }).join('');

    return '<header class="ap-topbar">' +
      '<div class="ap-crumb">' + trail + '</div>' +
      '<div class="ap-topbar-r">' +
        '<div class="ap-search">' +
          '<svg viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"><circle cx="7.2" cy="7.2" r="4.6"/><path d="M10.6 10.6 14 14"/></svg>' +
          '搜索用户、角色、菜单<span class="ap-kbd">⌘K</span>' +
        '</div>' +
        '<button class="ap-icbtn" type="button" aria-label="通知，3 条未读">' +
          '<svg viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4.4 7.6a4.6 4.6 0 0 1 9.2 0c0 3.9 1.4 5 1.4 5H3s1.4-1.1 1.4-5z"/><path d="M7.3 15a2 2 0 0 0 3.4 0"/></svg>' +
          '<span class="ap-dot"></span>' +
        '</button>' +
        '<button class="ap-icbtn" type="button" aria-label="帮助">' +
          '<svg viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="9" cy="9" r="6.8"/><path d="M7.1 7.1a1.95 1.95 0 1 1 2.7 1.8c-.5.2-.8.7-.8 1.2v.4"/><path d="M9 12.9h.01"/></svg>' +
        '</button>' +
      '</div>' +
    '</header>';
  }

  /* ---- 装配 ---- */
  var body = document.body, page = body.dataset.page;
  body.insertAdjacentHTML('afterbegin', docnav());

  if (page && page !== 'bare') {
    var app = document.createElement('div');
    app.className = 'ap-app';
    app.innerHTML = rail(page) + '<div class="ap-stage">' + topbar(body.dataset.crumb) + '</div>';
    var pageEl = document.querySelector('.ap-page');
    if (pageEl) app.querySelector('.ap-stage').appendChild(pageEl);
    body.appendChild(app);
  }

  /* ---- 权限矩阵十字高亮 ---- */
  document.querySelectorAll('.ap-mx').forEach(function (t) {
    function clear() {
      t.querySelectorAll('[data-hot]').forEach(function (e) { e.removeAttribute('data-hot'); });
    }
    t.addEventListener('mouseover', function (e) {
      var c = e.target.closest('td, tbody th');
      if (!c || !t.contains(c)) return;
      clear();
      c.parentElement.setAttribute('data-hot', '');
      if (c.tagName === 'TD') {
        var i = Array.prototype.indexOf.call(c.parentElement.children, c);
        c.setAttribute('data-hot', '');
        var h = t.tHead && t.tHead.rows[0];
        if (h && h.cells[i]) h.cells[i].setAttribute('data-hot', '');
      }
    });
    t.addEventListener('mouseleave', clear);
  });
})();
