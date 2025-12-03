import React from 'react';
import { Button, Dropdown } from 'antd';
import { GlobalOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import type { MenuProps } from 'antd';

const LanguageSwitcher: React.FC = () => {
    const { i18n } = useTranslation();

    const items: MenuProps['items'] = [
        {
            key: 'zh',
            label: '简体中文',
            onClick: () => i18n.changeLanguage('zh'),
        },
        {
            key: 'en',
            label: 'English',
            onClick: () => i18n.changeLanguage('en'),
        },
    ];

    return (
        <div>
            <Dropdown menu={{ items }} placement="bottomRight">
                <Button
                    type="text"
                    icon={<GlobalOutlined style={{ fontSize: '1.25rem', color: '#94a3b8' }} />}
                    style={{ color: '#94a3b8' }}
                />
            </Dropdown>
        </div>
    );
};

export default LanguageSwitcher;
