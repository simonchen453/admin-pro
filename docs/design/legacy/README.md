# 旧版标识（靛紫渐变）

`public/` 下的这两个文件被换成墨黑版之后，原件移到了这里。

放在 `public/` 里等于跟着 `dist/` 一起发布出去 —— 一个已经不用的图标不该
出现在生产站点上。归档在设计目录里，需要时随时拿得到；
真要找更早的版本，`git show 2162547:frontend/public/logo.svg` 也在。

`logo-large-legacy-indigo.svg` 是同一批的大尺寸版本，全仓没有任何地方引用它，
一并挪过来。

## 还没处理的一个

`frontend/public/favicon.ico` 仍是旧的靛紫图标。`index.html` 只指向 `/favicon.svg`，
但浏览器会自己去请求 `/favicon.ico` —— 被固定到任务栏或收藏夹时可能还是旧标识。
需要用 `logo.svg` 重新导出一个 .ico 覆盖它。
