/**
 * AdminPro · Ant Design 5 主题配置（经典企业商务风）
 *
 * - 主色调：科技蓝 #1677ff
 * - 画布：#f0f2f5
 * - 纯净浅色侧栏与纯白卡片，结合精致微阴影
 * - 规范统一的 6px/8px 圆角与标准企业级排版
 */
import type { ThemeConfig } from 'antd';

export const antdTheme: ThemeConfig = {
  token: {
    /* --- 核心色彩 --- */
    colorPrimary: '#1677ff',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError:   '#ff4d4f',
    colorInfo:    '#1677ff',

    colorLink:          '#1677ff',
    colorLinkHover:     '#4096ff',
    colorPrimaryHover:  '#4096ff',
    colorPrimaryActive: '#0958d9',

    /* --- 画布与背景 --- */
    colorBgLayout:       '#f0f2f5',
    colorBgContainer:    '#ffffff',
    colorBgElevated:     '#ffffff',
    colorFillQuaternary: '#f5f5f5',
    colorFillTertiary:   '#f5f5f5',
    colorFillSecondary:  '#e8e8e8',

    /* --- 边框与线 --- */
    colorBorder:          '#d9d9d9',
    colorBorderSecondary: '#f0f0f0',

    /* --- 字体与文本 --- */
    colorText:           '#1f1f1f',
    colorTextSecondary:  '#595959',
    colorTextTertiary:   '#8c8c8c',
    colorTextQuaternary: '#bfbfbf',

    fontFamily:
      "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif",
    fontFamilyCode: "'JetBrains Mono','Cascadia Mono',Consolas,monospace",
    fontSize: 14,
    fontSizeHeading1: 26,
    fontSizeHeading2: 20,
    fontSizeHeading3: 16,

    /* --- 圆角与尺寸 --- */
    borderRadius:   6,
    borderRadiusLG: 8,
    borderRadiusSM: 4,
    borderRadiusXS: 2,
    controlHeight:   36,
    controlHeightLG: 40,
    controlHeightSM: 28,

    /* --- 阴影 --- */
    boxShadow:          '0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02)',
    boxShadowTertiary:  '0 1px 3px 0 rgba(0, 0, 0, 0.04), 0 1px 2px -1px rgba(0, 0, 0, 0.02)',
    boxShadowSecondary: '0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12)',

    motionDurationMid:  '0.2s',
    motionDurationSlow: '0.25s',
    wireframe: false,
  },

  components: {
    Layout: {
      bodyBg:        '#f0f2f5',
      headerBg:      '#ffffff',
      siderBg:       '#ffffff',
      headerHeight:  56,
      headerPadding: '0 24px',
    },

    /* 纯净浅色侧栏导航 */
    Menu: {
      itemBg:             'transparent',
      subMenuItemBg:      'transparent',
      itemColor:          '#595959',
      itemHoverColor:     '#1677ff',
      itemHoverBg:        '#f5f5f5',
      itemSelectedColor:  '#1677ff',
      itemSelectedBg:     '#e6f4ff',
      itemBorderRadius:   6,
      itemHeight:         40,
      itemMarginInline:   8,
      itemMarginBlock:    4,
      iconSize:           16,
      collapsedIconSize:  16,
      groupTitleColor:    '#8c8c8c',
      groupTitleFontSize: 12,
    },

    /* 标准企业按钮规范 */
    Button: {
      fontWeight:         400,
      borderRadius:       6,
      borderRadiusLG:     6,
      borderRadiusSM:     4,
      paddingInline:      16,
      defaultBorderColor: '#d9d9d9',
      textHoverBg:        '#f5f5f5',
    },

    /* 规范标准数据表格 */
    Table: {
      headerBg:           '#fafafa',
      headerColor:        '#262626',
      headerSplitColor:   '#f0f0f0',
      borderColor:        '#f0f0f0',
      rowHoverBg:         '#f6faff',
      rowSelectedBg:      '#e6f4ff',
      rowSelectedHoverBg: '#bae0ff',
      headerBorderRadius: 0,
      cellPaddingBlock:   12,
      cellPaddingInline:  16,
    },

    Card: {
      paddingLG:         20,
      headerHeight:      52,
      headerFontSize:    16,
      headerBg:          '#ffffff',
      borderRadiusLG:    8,
    },

    Input: {
      borderRadius: 6,
      controlHeight: 36,
      activeShadow: '0 0 0 2px rgba(22, 119, 255, 0.15)',
    },
    InputNumber: {
      borderRadius: 6,
      controlHeight: 36,
      activeShadow: '0 0 0 2px rgba(22, 119, 255, 0.15)',
    },
    Select: {
      borderRadius: 6,
      controlHeight: 36,
      optionSelectedBg: '#e6f4ff',
      optionSelectedColor: '#1677ff',
    },
    DatePicker: {
      borderRadius: 6,
      controlHeight: 36,
      activeShadow: '0 0 0 2px rgba(22, 119, 255, 0.15)',
    },
    TreeSelect: {
      borderRadius: 6,
      controlHeight: 36,
    },

    Tag: {
      borderRadiusSM: 4,
    },

    Modal: {
      headerBg:       '#ffffff',
      contentBg:      '#ffffff',
      footerBg:       '#ffffff',
      borderRadiusLG: 8,
    },
    Drawer: {
      footerPaddingBlock: 16,
    },
    Tooltip: {
      colorBgSpotlight: '#1f1f1f',
      borderRadius:     4,
    },
    Segmented: {
      itemSelectedBg:    '#ffffff',
      trackBg:           '#f5f5f5',
      borderRadius:      6,
      itemSelectedColor: '#1677ff',
    },
    Tabs: {
      itemSelectedColor: '#1677ff',
      inkBarColor:       '#1677ff',
      itemHoverColor:    '#4096ff',
    },
    Pagination: {
      itemActiveBg:    '#ffffff',
      itemActiveColor: '#1677ff',
      borderRadius:    6,
    },
  },
};

export default antdTheme;
