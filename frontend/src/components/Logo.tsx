/**
 * 品牌标记 —— 一枚墨黑圆角方块 + 白色盾形勾。
 *
 * 旧版的 logo.svg 是 #8b5cf6 → #6366f1 的靛紫渐变加高斯发光，
 * 正好踩中这套视觉的第一条硬约束（不用渐变装饰）。这里直接画成内联 SVG：
 * 单色、可被 currentColor 继承、不需要多一次网络请求。
 *
 * 外面那个黑方块由 .ap-logo 提供（见 styles/ui.css），这里只负责里面的图形。
 */
export function LogoMark({ size = 15 }: { size?: number }) {
  return (
    <svg
      viewBox="0 0 18 18"
      width={size}
      height={size}
      fill="none"
      stroke="currentColor"
      strokeWidth="1.7"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M9 1.8 15.2 4.6v4.6c0 3.5-2.6 5.9-6.2 7-3.6-1.1-6.2-3.5-6.2-7V4.6z" />
      <path d="M6.4 8.9 8.3 10.8 11.8 7.3" />
    </svg>
  );
}

/** 侧栏用户块右侧那个 › 形箭头 */
export function ChevronRight({ size = 14 }: { size?: number }) {
  return (
    <svg viewBox="0 0 14 14" width={size} height={size} fill="none"
      stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M4.8 2.5 9.3 7l-4.5 4.5" />
    </svg>
  );
}

export default LogoMark;
