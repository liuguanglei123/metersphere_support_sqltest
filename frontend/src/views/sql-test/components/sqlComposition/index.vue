<template>
  <!-- TODO:全文 多语言支持 -->
  <div class="request-composition flex h-full flex-col">
    <div v-if="!props.isCase" class="mb-[8px] px-[18px] pt-[8px]">
      <div class="flex flex-wrap items-baseline justify-between gap-[12px]">
        <div class="flex flex-1 flex-wrap items-center gap-[16px]">
          <a-checkbox v-model="isPreDataChecked" value="1">预置数据</a-checkbox>
          <a-button type="primary" :disabled="!isPreDataChecked">配置预置数据</a-button>
        </div>

        <div>
          <template v-if="true">
            <!--            TODO:终止sql执行的按钮，需要实现自动切换-->
            <a-button
              class="mr-[12px]"
              :disabled="requestVModel.executeLoading || !requestVModel.body.sqlContent"
              type="primary"
              @click="() => execute('serverExec')"
            >
              {{ t('sqlTestDebug.serverExec') }}
            </a-button>
          </template>
          <!-- 接口调试，支持快捷保存 -->
          <template
            v-if="
              requestVModel.isNew
                ? props.permissionMap && hasAnyPermission([props.permissionMap.create])
                : props.permissionMap && hasAnyPermission([props.permissionMap.update])
            "
          >
            <!-- 接口调试-可保存或保存为新接口定义 -->
            <a-dropdown-button
              v-if="
                props.permissionMap &&
                props.permissionMap.saveASApi &&
                hasAllPermission([props.permissionMap.create, props.permissionMap.saveASApi])
              "
              type="outline"
              class="arco-btn-group-outline--secondary"
              @click="handleSaveShortcut"
            >
              <div class="flex items-center">
                {{ t('common.save') }}
                <div class="text-[var(--color-text-4)]">(<icon-command size="14" />+S)</div>
              </div>
              <template #icon>
                <icon-down />
              </template>
              <template #content>
                <a-doption value="saveAsApi" @click="() => handleSelect('saveAsApi')">
                  {{ t('apiTestDebug.saveAsApi') }}
                </a-doption>
              </template>
            </a-dropdown-button>
            <a-button v-else type="secondary" :loading="saveLoading" @click="handleSaveShortcut">
              <div class="flex items-center">
                {{ t('common.save') }}
                <div class="text-[var(--color-text-4)]">(<icon-command size="14" />+S)</div>
              </div>
            </a-button>
          </template>
        </div>
      </div>
    </div>
    <div :class="`${!props.isCase ? 'request-tab-and-response' : ''} flex-1`">
      <div :class="`request-content-and-response ${activeLayout}`">
        <a-spin class="request" :loading="requestVModel.executeLoading">
          <sqlBody
            v-model:params="requestVModel.body"
            :is-debug="requestVModel.mode === 'debug'"
            :is-case="props.isCase"
          >
          </sqlBody>
        </a-spin>
        <response
          v-show="showResponse"
          ref="responseRef"
          v-model:active-tab="requestVModel.responseActiveTab"
          v-model:active-layout="activeLayout"
          :loading="requestVModel.executeLoading"
          class="response"
          :request-result="requestVModel.response"
        >
<!--          TODO：:request-result="requestVModel.response ? requestVModel.response : undefined"-->
        </response>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  // TODO:代码拆分，结构优化
  import { InputInstance } from '@arco-design/web-vue';

  import { SQLTabItem } from '@/components/pure/ms-editable-tab/types';
  import response from './response/index.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useShortcutSave from '@/hooks/useShortcutSave';
  import { useSqlWebsocket } from '@/hooks/useWebsocket';
  import { getGenerateId } from '@/utils';
  import { hasAllPermission, hasAnyPermission } from '@/utils/permission';

  import { ExecuteSqlRequestFullParams, SqlExecuteRequestParams } from '@/models/sqlTest/common';
  import { RequestComposition } from '@/enums/apiEnum';

  // TODO：暂不确定这里的showResponse有何作用，先临时置为true
  const showResponse = computed(() => {
    return true;
  });

  export interface RequestCustomAttr {
    mode?: 'definition' | 'debug'; // 接口定义时，展示的定义模式/调试模式（显示的 tab 不同）
    isNew: boolean;
    errorMessageInfo?: {
      [key: string]: Record<string, any>;
    };
    executeLoading: boolean; // 执行中loading
    isCopy?: boolean; // 是否是复制
    isExecute?: boolean; // 是否是执行
  }

  export type SqlRequestParam = ExecuteSqlRequestFullParams & RequestCustomAttr & SQLTabItem;

  const props = defineProps<{
    isCase?: boolean; // 是否是用例引用的组件,只显示请求参数和响应内容,响应内容默认为空且折叠
    request: SqlRequestParam; // 请求sql
    isDefinition?: boolean; // 是否是接口定义模式
    permissionMap?: {
      execute: string;
      create: string;
      update: string;
      saveASApi?: string;
    };
    executeApi?: (params: SqlExecuteRequestParams) => Promise<any>; // 执行接口
  }>();

  const saveLoading = ref(false);

  const requestVModel = defineModel<SqlRequestParam>('request', { required: true });

  const sqlBody = defineAsyncComponent(() => import('./body.vue'));

  const activeLayout = ref<'horizontal' | 'vertical'>('vertical');

  const isPreDataChecked = ref(false);

  const { t } = useI18n();

  const nameInputRef = ref<InputInstance>();

  function inputBlur() {
    nameInputRef.value?.blur();
  }

  // 需要最终提示的信息
  function getFlattenedMessages() {
    // TODO：
    // if (!requestVModel.value.errorMessageInfo) return;
    // const flattenedMessages: { label: string; messageList: string[] }[] = [];
    // const { errorMessageInfo } = requestVModel.value;
    // Object.entries(errorMessageInfo).forEach(([key, item]) => {
    //   const label = item.label || Object.values(item)[0]?.label;
    //   // 处理前后置已删除的
    //   if ([RequestComposition.POST_CONDITION as string, RequestComposition.PRECONDITION as string].includes(key)) {
    //     const processorIds = requestVModel.value.children[0][
    //       key === RequestComposition.POST_CONDITION ? 'postProcessorConfig' : 'preProcessorConfig'
    //     ].processors.map((processorItem) => String(processorItem.id));
    //     Object.entries(item).forEach(([childKey, childItem]) => {
    //       if (!processorIds.includes(childKey)) {
    //         childItem.messageList = [];
    //       }
    //     });
    //   }
    //   const messageList: string[] =
    //     item.messageList || [...new Set(Object.values(item).flatMap((child) => child.messageList))] || [];
    //   if (messageList.length) {
    //     flattenedMessages.push({ label, messageList: [...new Set(messageList)] });
    //   }
    // });
    // return flattenedMessages;
  }

  async function updateRequest() {
    // TODO：
    // try {
    //   if (!props.updateApi) return;
    //   saveLoading.value = true;
    //   const requestParams = await makeRequestParams();
    //   const res = await props.updateApi({
    //     ...requestParams,
    //     ...props.otherParams,
    //   });
    //   Message.success(t('common.updateSuccess'));
    //   requestVModel.value.updateTime = res.updateTime;
    //   requestVModel.value.unSaved = false;
    //   const parseRequestBodyResult = parseRequestBodyFiles(
    //       requestVModel.value.body,
    //       requestVModel.value.responseDefinition
    //   );
    //   requestVModel.value.uploadFileIds = parseRequestBodyResult.uploadFileIds;
    //   requestVModel.value.linkFileIds = parseRequestBodyResult.linkFileIds;
    //   emit('addDone');
    // } catch (error) {
    //   // eslint-disable-next-line no-console
    //   console.log(error);
    // } finally {
    //   saveLoading.value = false;
    // }
  }

  const saveModalVisible = ref(false);

  const saveModalForm = ref({
    name: '',
    moduleId: 'root',
  });

  /**
   * TODO:
   * 保存请求
   * @param fullParams 保存时传入的参数
   * @param silence 是否静默保存（接口定义另存为用例时要先静默保存接口）
   */
  async function realSave(fullParams?: Record<string, any>, silence?: boolean) {
    // try {
    //   if (!props.createApi) return;
    //   if (!silence) {
    //     saveLoading.value = true;
    //   }
    //   let params;
    //   const requestParams = await makeRequestParams();
    //   if (props.isDefinition) {
    //     params = {
    //       ...(fullParams || requestParams),
    //       ...props.otherParams,
    //     };
    //   } else {
    //     params = {
    //       ...(fullParams || requestParams),
    //       ...saveModalForm.value,
    //       path: isHttpProtocol.value ? saveModalForm.value.path : undefined,
    //       ...props.otherParams,
    //     };
    //   }
    //   const res = await props.createApi(params);
    //   if (!silence) {
    //     Message.success(t('common.saveSuccess'));
    //   }
    //   requestVModel.value.id = res.id;
    //   requestVModel.value.num = res.num;
    //   requestVModel.value.isNew = false;
    //   requestVModel.value.unSaved = false;
    //   requestVModel.value.name = res.name;
    //   requestVModel.value.label = res.name;
    //   requestVModel.value.url = res.path;
    //   requestVModel.value.path = res.path;
    //   requestVModel.value.moduleId = res.moduleId;
    //   if (!isHttpProtocol.value) {
    //     requestVModel.value = {
    //       ...requestVModel.value,
    //       ...fApi.value?.formData(), // 存储插件表单数据
    //       uploadFileIds: requestParams.uploadFileIds,
    //       linkFileIds: requestParams.linkFileIds,
    //     };
    //   } else {
    //     requestVModel.value.uploadFileIds = requestParams.uploadFileIds;
    //     requestVModel.value.linkFileIds = requestParams.linkFileIds;
    //   }
    //   if (!props.isDefinition) {
    //     saveModalVisible.value = false;
    //   }
    //   if (!silence) {
    //     saveLoading.value = false;
    //     emit('addDone');
    //   }
    // } catch (error) {
    //   // eslint-disable-next-line no-console
    //   console.log(error);
    //   saveLoading.value = false;
    // }
  }

  /**
   * 保存快捷键处理
   */
  async function handleSaveShortcut() {
    try {
      // TODO：
      // 检查全部的校验信息
      // if (getFlattenedMessages()?.length) {
      //   showMessage();
      //   return;
      // }
      if (!requestVModel.value.isNew) {
        // 更新接口不需要弹窗，直接更新保存
        await updateRequest();
        return;
      }
      if (!props.isDefinition) {
        // 接口调试需要弹窗保存
        saveModalForm.value = {
          name: requestVModel.value.name || '',
          moduleId: 'root',
        };
        saveModalVisible.value = true;
      } else {
        realSave();
      }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      // 校验不通过则不进行保存
      requestVModel.value.activeTab = RequestComposition.PLUGIN;
      // TODO：
      // nextTick(() => {
      //   scrollIntoView(document.querySelector('.arco-form-item-message'), { block: 'center' });
      // });
    }
  }

  async function handleSelect(value: string | number | Record<string, any> | undefined) {
    // TODO：
    // if (requestVModel.value.url === '' && requestVModel.value.protocol === 'HTTP') {
    //   isUrlError.value = true;
    //   return;
    // }
    // if (requestVModel.value.name === '') {
    //   isNameError.value = true;
    //   return;
    // }
    // isUrlError.value = false;
    // isNameError.value = false;
    // if (value === 'saveAsApi') {
    //   const params = await makeRequestParams();
    //   tempApiDetail.value = {
    //     ...params,
    //     ...params.request,
    //     polymorphicName: params.request.polymorphicName,
    //   } as unknown as RequestParam;
    //   saveNewApiModalVisible.value = true;
    //   return;
    // }
    // apiBaseFormRef.value?.formRef?.validate(async (errors) => {
    //   if (errors) {
    //     requestVModel.value.activeTab = RequestComposition.BASE_INFO;
    //   } else {
    //     // 检查全部的校验信息
    //     if (getFlattenedMessages()?.length) {
    //       showMessage();
    //       return;
    //     }
    //     switch (value) {
    //       case 'save':
    //         handleSaveShortcut();
    //         break;
    //       case 'saveAsCase':
    //         saveNewDefinition();
    //         break;
    //       default:
    //         break;
    //     }
    //   }
    // });
  }

  const { registerCatchSaveShortcut, removeCatchSaveShortcut } = useShortcutSave(() => {
    inputBlur(); // 先失焦再保存
    if (!props.isDefinition) {
      handleSaveShortcut();
    } else {
      handleSelect('save');
    }
  });

  onMounted(() => {
    if (
      !props.isCase &&
      (requestVModel.value.isNew
        ? props.permissionMap && hasAnyPermission([props.permissionMap.create])
        : props.permissionMap && hasAnyPermission([props.permissionMap.update]))
    ) {
      registerCatchSaveShortcut();
    }
  });

  const reportId = ref('');

  const temporaryResponseMap: Record<string, any> = {}; // 缓存websocket返回的报告内容，避免执行接口后切换tab导致报告丢失
  const websocket = ref<WebSocket>();

  /**
   * TODO：暂只支持serverExec的执行
   * 开启websocket监听，接收执行结果
   */
  // async function debugSocket(executeType?: 'localExec' | 'serverExec') {
  async function debugSocket(executeType?: 'serverExec') {
    const { createSocket, websocket: _websocket } = useSqlWebsocket({
      reportId: reportId.value,
      socketUrl: '',
      host: '',
      onMessage: (event) => {
        const data = JSON.parse(event.data);
        if (data.msgType === 'SQL_EXEC_RESULT') {
          if (requestVModel.value.reportId === data.reportId) {
            // 判断当前查看的tab是否是当前返回的报告的tab，是的话直接赋值
            requestVModel.value.response = data.taskResult;
            requestVModel.value.executeLoading = false;
            requestVModel.value.isExecute = false;
          } else {
            // 不是则需要把报告缓存起来，等切换到对应的tab再赋值
            temporaryResponseMap[data.reportId] = data.taskResult;
          }
        } else if (data.msgType === 'SQL_EXEC_END') {
          // 执行结束，关闭websocket
          websocket.value?.close();
          if (requestVModel.value.reportId === data.reportId) {
            requestVModel.value.executeLoading = false;
            requestVModel.value.isExecute = false;
          }
        }
      },
    });
    await createSocket();
    websocket.value = _websocket.value;
  }

  /**
   * 生成请求参数
   * @param executeType 执行类型，执行时传入
   */
  // async function makeRequestParams(executeType?: 'localExec' | 'serverExec') {
  async function makeRequestParams(executeType?: 'serverExec') {
    const isExecute = executeType === 'serverExec';
    const { sqlContent } = requestVModel.value.body;

    let parseRequestBodyResult;
    const requestParams = {
      // authConfig: requestVModel.value.authConfig,
      body: {
        ...requestVModel.value.body,
      },
      // otherConfig: requestVModel.value.otherConfig,
    };
    // 这里需要对输入的字符串进行解析，比如替换变量 $table，该部分内容待完成
    reportId.value = getGenerateId();
    requestVModel.value.reportId = reportId.value; // 存储报告ID
    // 创建websocket连接，作用是同步sql的执行结果，编码过程参考了apiTest的实现
    if (isExecute && !props.isCase) {
      await debugSocket(executeType); // 开启websocket
    }
    let requestName = '';
    let requestModuleId = '';
    const apiDefinitionParams: Record<string, any> = {};
    if (props.isDefinition) {
      // TODO：
      // 接口定义有响应内容定义
      // requestName = requestVModel.value.name;
      // requestModuleId = requestVModel.value.moduleId;
      // apiDefinitionParams = {
      //   tags: requestVModel.value.tags,
      //   description: requestVModel.value.description,
      //   status: requestVModel.value.status,
      //   response: requestVModel.value.responseDefinition?.map((e) => ({
      //     ...e,
      //     headers: filterKeyValParams(e.headers, defaultKeyValueParamItem, isExecute).validParams,
      //     body: {
      //       ...e.body,
      //       jsonBody: {
      //         jsonValue: e.body.jsonBody.jsonValue,
      //         enableJsonSchema: jsonBody.enableJsonSchema,
      //         jsonSchema: e.body.jsonBody.jsonSchemaTableData
      //             ? parseTableDataToJsonSchema(e.body.jsonBody.jsonSchemaTableData[0])
      //             : undefined,
      //       },
      //     },
      //   })),
      // };
    } else {
      requestName = requestVModel.value.isNew ? saveModalForm.value.name : requestVModel.value.name;
      requestModuleId = requestVModel.value.isNew ? saveModalForm.value.moduleId : requestVModel.value.moduleId;
    }

    // TODO：
    // 处理断言参数
    // const { assertionConfig } = requestVModel.value.children[0];
    return {
      id: requestVModel.value.id.toString(),
      reportId: reportId.value,
      // environmentId: appStore.currentEnvConfig?.id || '',
      name: requestName,
      moduleId: requestModuleId,
      num: requestVModel.value.num,
      ...apiDefinitionParams,
      // method: isHttpProtocol.value ? requestVModel.value.method : requestVModel.value.protocol,
      // path: isHttpProtocol.value ? requestVModel.value.url || requestVModel.value.path : undefined,
      request: {
        ...requestParams,
        name: requestName,
      },
      frontendDebug: false,
      isNew: requestVModel.value.isNew,
      dataSourceId: 1,
    };
  }

  /**
   * 执行调试
   * @param val 执行类型
   */
  // TODO：暂时不实现本地请求（localExec），以后再说
  // async function execute(executeType?: 'localExec' | 'serverExec') {
  async function execute(executeType?: 'serverExec') {
    try {
      if (!props.executeApi) return;
      await nextTick();
      requestVModel.value.executeLoading = true;
      // requestVModel.value.response = cloneDeep(defaultResponse);

      const res = await props.executeApi((await makeRequestParams(executeType)) as SqlExecuteRequestParams);
      requestVModel.value.executeLoading = false;
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      // websocket.value?.close();
      requestVModel.value.executeLoading = false;
    }
  }

  const responseRef = ref<InstanceType<typeof response>>();

  watch(
    () => requestVModel.value?.mode,
    (val: 'debug' | 'definition' | undefined) => {
      if (val) {
        responseRef.value?.setActiveResponse(val === 'debug' ? 'result' : 'content');
      }
    }
  );
</script>

<style lang="less" scoped>
  .exec-btn {
    margin-right: 12px;
    :deep(.arco-btn) {
      color: white !important;
      background-color: rgb(var(--primary-5)) !important;
      .btn-base-primary-hover();
      .btn-base-primary-active();
      .btn-base-primary-disabled();
    }
  }
  :deep(.no-content) {
    .arco-tabs-content {
      display: none;
    }
  }
  :deep(.arco-tabs-tab:first-child) {
    margin-left: 0;
  }
  :deep(.arco-tabs-tab) {
    @apply leading-none;
  }
  .url-input-tip {
    @apply w-full;

    margin-top: -14px;
    padding-left: 226px;
    font-size: 12px;
    color: rgb(var(--danger-6));
    line-height: 16px;
  }
  .name-input-tip {
    @apply w-full;

    margin-top: 4px;
    font-size: 12px;
    color: rgb(var(--danger-6));
    line-height: 16px;
  }
  .request-tab-and-response {
    overflow-x: hidden;
    overflow-y: auto;
    .ms-scroll-bar();
  }
  .sticky-content {
    @apply sticky;

    z-index: 101; // .arco-scrollbar-track是100
    background-color: var(--color-text-fff);
  }
  .request-content-and-response {
    display: flex;
    &.vertical {
      flex-direction: column;
      .response :deep(.response-head) {
        @apply sticky;

        top: 46px; // 请求参数tab高度(不算border-bottom)
        z-index: 11;
        background-color: var(--color-text-fff);
      }
      .request-tab-pane {
        min-height: 400px;
      }
    }
    &.horizontal {
      flex-direction: row;
      min-height: calc(100% - 49px); // 49px:请求参数tab高度
      .request {
        flex: 1;
        overflow-x: auto;
        border-right: 1px solid var(--color-text-n8);
        .ms-scroll-bar();
        .request-tab-pane {
          min-width: 800px;
        }
      }
      .response {
        width: 500px;
      }
    }
  }
</style>
