import {
  ProColumns,
  ProTable,
} from '@ant-design/pro-components';
import '@umijs/max';
import React, {useEffect, useRef} from 'react';
import {Modal} from "antd";
import {ProFormInstance} from "@ant-design/pro-form/lib";
export type Props = {
  values: API.InterfaceInfo;
  columns: ProColumns<API.InterfaceInfo>[];
  onCancel: () => void;
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  visible: boolean;
};
const UpdateModal: React.FC<Props> = (props) => {
  const  { values,visible,columns,onCancel,onSubmit } = props;
  const fromRef = useRef<ProFormInstance>();
  useEffect(()=>{
    fromRef.current?.setFieldsValue(values);
  },[values])
  return <Modal visible={visible} footer={null} onCancel={() => onCancel?.()}>
    <ProTable type="form" columns={columns}
      formRef={fromRef}
      onSubmit={async (value) => {
      onSubmit?.(value)
    }}
     />
  </Modal>;
};
export default UpdateModal;
