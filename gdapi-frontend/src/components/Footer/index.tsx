import {GithubOutlined, HomeFilled} from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';
import {BlOG_SITE, GITHUB_SITE} from "@/constant";
const Footer: React.FC = () => {
  const defaultMessage = 'Gundam出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'My Blog',
          title: <><HomeFilled />个人博客</>,
          href: BlOG_SITE,
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined />Github</>,
          href: GITHUB_SITE,
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
