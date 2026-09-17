/**
 * AdminPro · Ant Design 5 主题配置（纯白 / 墨黑）
 *
 * 用法：复制到 frontend/src/config/theme.ts，然后在 App.tsx 里
 *   import { antdTheme } from './config/theme';
 *   <ConfigProvider locale={zhCN} theme={antdTheme}> ... </ConfigProvider>
 *
 * 色值与 docs/design/theme.css 一一对应。改配色时两边一起改，
 * 或者更彻底一点：把 theme.css 的 CSS 变量作为唯一真源，这里用
 * getComputedStyle 读取（构建期开销略大，但不会再出现两边不一致）。
 */
import type { ThemeConfig } from 'antd';

export const antdTheme: ThemeConfig = {
  token: {
    /* --- 色 ---
       强调色就是墨黑。AntD 会把 colorPrimary 铺到主按钮、链接、焦点环、
       选中行、Tabs 墨条上 —— 这正是我们要的：整套控件一起变成黑白。 */
    colorPrimary: '#0A0A0A',
    colorSuccess: '#067647',
    colorWarning: '#B54708',
    colorError:   '#B42318',
    colorInfo:    '#0A0A0A',

    colorLink:          '#0A0A0A',
    colorLinkHover:     '#2E2E2E',
    colorPrimaryHover:  '#2E2E2E',   // 黑按钮悬停往上提亮，不是压暗
    colorPrimaryActive: '#0A0A0A',

    colorBgLayout:    '#FFFFFF',     // 工作区、侧栏、卡片全部纯白
    colorBgContainer: '#FFFFFF',
    colorBgElevated:  '#FFFFFF',
    colorFillQuaternary: '#F5F5F5',  // 悬停、选中、键帽这类极浅填充
    colorFillTertiary:   '#F5F5F5',
    colorFillSecondary:  '#EBEBEB',

    colorBorder:          '#D2D2D2', // 控件描边
    colorBorderSecondary: '#E3E3E3', // 区块边界、分隔线

    colorText:           '#0A0A0A',
    colorTextSecondary:  '#5C5C5C',
    colorTextTertiary:   '#757575',
    colorTextQuaternary: '#9E9E9E',

    /* --- 字 ---
       Inter 负责界面文字，思源黑体负责中文。标题的 Inter Tight 不走
       AntD token（它只有一个 fontFamily），在 CSS 里按 .ap-* 类挂。 */
    fontFamily:
      "'Inter','Noto Sans SC','PingFang SC','Microsoft YaHei',system-ui,sans-serif",
    fontFamilyCode: "'JetBrains Mono','Cascadia Mono',Consolas,monospace",
    fontSize: 14,
    fontSizeHeading1: 30,
    fontSizeHeading2: 22,
    fontSizeHeading3: 17,

    /* --- 尺 ---
       按钮是胶囊，所以 borderRadius 给足；卡片 14，输入框在组件层单独压回 10。 */
    borderRadius:   999,
    borderRadiusLG: 14,
    borderRadiusSM: 999,
    borderRadiusXS: 6,
    controlHeight:   36,
    controlHeightLG: 44,
    controlHeightSM: 30,

    /* --- 投影 ---
       卡片、按钮、下拉一律零投影，只有浮层有一档。
       这套语言里"抬起"是靠发丝线和留白，不是靠阴影。 */
    boxShadow:          'none',
    boxShadowTertiary:  'none',
    boxShadowSecondary: '0 16px 40px rgba(0,0,0,.10), 0 2px 8px rgba(0,0,0,.05)',

    /* --- 动效：只服务状态切换的可读性 --- */
    motionDurationMid:  '0.15s',
    motionDurationSlow: '0.2s',
    wireframe: false,
  },

  components: {
    Layout: {
      bodyBg:   '#FFFFFF',
      headerBg: '#FFFFFF',
      siderBg:  '#FFFFFF',   // 侧栏不再比工作区深，靠右侧一条发丝线分开
      headerHeight: 60,
      headerPadding: '0 32px',
    },

    /* 侧栏菜单：当前项是浅灰底 + 墨黑字。
       没有投影、没有位移动画、没有右侧竖条。 */
    Menu: {
      itemBg:             'transparent',
      subMenuItemBg:      'transparent',
      itemColor:          '#5C5C5C',
      itemHoverColor:     '#0A0A0A',
      itemHoverBg:        '#F5F5F5',
      itemSelectedColor:  '#0A0A0A',
      itemSelectedBg:     '#F5F5F5',
      itemBorderRadius:   10,
      itemHeight:         36,
      itemMarginInline:   0,
      itemMarginBlock:    2,
      iconSize:           16,
      collapsedIconSize:  16,
      groupTitleColor:    '#757575',
      groupTitleFontSize: 11.5,
      activeBarWidth:     0,
    },

    /* 主按钮实心黑，次按钮白底描边，全都是胶囊。一屏最多一个主按钮。 */
    Button: {
      fontWeight:    500,
      borderRadius:   999,
      borderRadiusLG: 999,
      borderRadiusSM: 999,
      paddingInline:   18,
      primaryShadow: 'none',
      defaultShadow: 'none',
      dangerShadow:  'none',
      defaultBorderColor: '#D2D2D2',
      textHoverBg: '#F5F5F5',
    },

    /* 表头没有底色，只有一条线；行高 60，留白是这套语言的主料。 */
    Table: {
      headerBg:           '#FFFFFF',
      headerColor:        '#757575',
      headerSplitColor:   'transparent',
      borderColor:        '#EDEDED',
      rowHoverBg:         '#FAFAFA',
      rowSelectedBg:      '#F5F5F5',
      rowSelectedHoverBg: '#EBEBEB',
      headerBorderRadius: 0,
      /* 行高不在这里给：AntD 的 cellPaddingBlock 会同时作用于表头和内容，
         而设计要的是表头 44、内容 60 两个不同的值 —— 放在 styles/antd.css 里分开写。 */
    },

    Card: {
      paddingLG: 24,
      headerHeight: 64,
      headerFontSize: 17,
      headerBg: '#FFFFFF',
      boxShadowTertiary: 'none',
    },

    /* 输入类控件保持圆角矩形 —— 和胶囊按钮形状不同，
       让"可输入"和"可点击"一眼分得开。 */
    Input:       { borderRadius: 10, borderRadiusLG: 10, paddingBlock: 6, activeShadow: '0 0 0 3px #EBEBEB' },
    InputNumber: { borderRadius: 10, borderRadiusLG: 10, activeShadow: '0 0 0 3px #EBEBEB' },
    Select:      { borderRadius: 10, borderRadiusLG: 10, optionSelectedBg: '#F5F5F5', optionSelectedColor: '#0A0A0A' },
    DatePicker:  { borderRadius: 10, borderRadiusLG: 10, activeShadow: '0 0 0 3px #EBEBEB' },

    Tag: {
      defaultBg:      '#FFFFFF',
      defaultColor:   '#5C5C5C',
      borderRadiusSM: 999,
    },

    Modal:   { headerBg: '#FFFFFF', contentBg: '#FFFFFF', footerBg: '#FFFFFF', borderRadiusLG: 14 },
    Drawer:  { footerPaddingBlock: 20 },
    Tooltip: { colorBgSpotlight: '#0A0A0A', borderRadius: 10 },
    Segmented: { itemSelectedBg: '#FFFFFF', trackBg: '#F5F5F5', borderRadius: 999, itemSelectedColor: '#0A0A0A' },
    Tabs:    { itemSelectedColor: '#0A0A0A', inkBarColor: '#0A0A0A' },
    Pagination: { itemActiveBg: '#0A0A0A', itemBg: '#FFFFFF', borderRadius: 999 },
  },
};

export default antdTheme;
