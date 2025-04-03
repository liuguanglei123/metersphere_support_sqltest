<template>
  <div class="request-composition flex h-full flex-col">
    <div v-if="!props.isCase" class="mb-[8px] px-[18px] pt-[8px]">
      <div class="flex flex-wrap items-baseline justify-between gap-[12px]">
        <div class="flex flex-1 flex-wrap items-center gap-[16px]">
          <a-checkbox v-model="isPreDataChecked" value="1">预置数据</a-checkbox>
          <a-button type="primary" :disabled="!isPreDataChecked">配置预置数据</a-button>
        </div>
        <div>
          <!-- SQL定义-调试模式下，可执行 -->
          <template v-if="props.permissionMap && hasAnyPermission([props.permissionMap.execute])">
            <!-- TODO:终止sql执行的按钮，需要实现自动切换-->
            <a-button
              class="mr-[12px]"
              :disabled="requestVModel.executeLoading || !requestVModel.body.sqlContent"
              type="primary"
              @click="() => execute('serverExec')"
            >
              服务端执行
            </a-button>
          </template>
          <!-- 接口定义-调试模式，可保存或保存为新用例 -->
          <!-- TODO：不过当前功能还没有做完，目前仅有保存按钮，这里先备注 -->
          <template
            v-if="
              props.isDefinition &&
              (requestVModel.isNew
                ? props.permissionMap && hasAnyPermission([props.permissionMap.create])
                : props.permissionMap && hasAnyPermission([props.permissionMap.update]))
            "
          >
            <!-- TODO:暂仅支持保存，另存为功能待开发 -->
            <!-- 接口定义-调试模式，可保存或保存为新用例 -->
            <a-dropdown-button
              type="outline"
              class="arco-btn-group-outline--secondary"
              :disabled="saveLoading"
              @click="() => handleSelect('save')"
            >
              {{ t('common.save') }}
              <template #icon>
                <icon-down />
              </template>
              <template #content>
                <a-doption value="saveAsCase" @click="() => handleSelect('saveAsCase')">
                  {{ t('apiTestManagement.saveAsCase') }} 未开发
                </a-doption>
              </template>
            </a-dropdown-button>
          </template>
          <!-- 接口调试，支持快捷保存 -->
          <template
            v-else-if="
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
              :disabled="!requestVModel.body.sqlContent || saveLoading"
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
          :request-result="requestVModel.response?.data"
        >
        </response>
      </div>
    </div>
  </div>
  <a-modal
    v-model:visible="saveModalVisible"
    :title="t('common.save')"
    :ok-loading="saveLoading"
    class="ms-modal-form"
    title-align="start"
    body-class="!p-0"
    @before-ok="handleSave"
    @cancel="handleCancel"
  >
    <a-form ref="saveModalFormRef" :model="saveModalForm" layout="vertical">
      <a-form-item
        field="name"
        :label="t('apiTestDebug.requestName')"
        :rules="[{ required: true, message: t('apiTestDebug.requestNameRequired') }]"
        asterisk-position="end"
      >
        <a-input
          v-model:model-value="saveModalForm.name"
          :max-length="255"
          :placeholder="t('apiTestDebug.requestNamePlaceholder')"
        />
      </a-form-item>
      <a-form-item :label="t('apiTestDebug.requestModule')" class="mb-0">
        <a-tree-select
          v-model:modelValue="saveModalForm.moduleId"
          :data="selectTree as ModuleTreeNode[]"
          :field-names="{ title: 'name', key: 'id', children: 'children' }"
          :tree-props="{
            virtualListProps: {
              height: 200,
              threshold: 200,
            },
          }"
          :filter-tree-node="filterTreeNode"
          allow-search
        >
          <template #tree-slot-title="node">
            <a-tooltip :content="`${node.name}`" position="tl">
              <div class="one-line-text w-[300px]">{{ node.name }}</div>
            </a-tooltip>
          </template>
        </a-tree-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  // TODO:代码拆分，结构优化
  import {FormInstance, InputInstance, Message} from '@arco-design/web-vue';
  import { cloneDeep } from "lodash-es";

  import { SqlTabItem } from '@/components/pure/ms-editable-tab/types';
  import response from './response/index.vue';
  import { SqlResponseItem } from '@/views/sql-test/components/sqlComposition/response/edit.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useShortcutSave from '@/hooks/useShortcutSave';
  import { useSqlWebsocket } from '@/hooks/useWebsocket';
  import useAppStore from '@/store/modules/app';
  import { filterTree, filterTreeNode, getGenerateId} from '@/utils';
  import { hasAllPermission, hasAnyPermission } from '@/utils/permission';

  import { ModuleTreeNode} from "@/models/common";
  import { ExecuteSqlRequestFullParams, SqlExecuteRequestParams, SqlRequestTaskResult } from '@/models/sqlTest/common';
  import { SqlRequestComposition } from '@/enums/sqlEnum';

  const appStore = useAppStore();

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

  export type SqlRequestParam = ExecuteSqlRequestFullParams & {
    // responseDefinition?: SqlResponseItem[];
    response: SqlRequestTaskResult;
  } & RequestCustomAttr &
      SqlTabItem;

  const props = defineProps<{
    isCase?: boolean; // 是否是用例引用的组件,只显示请求参数和响应内容,响应内容默认为空且折叠
    // request: SqlRequestParam; // 请求sql
    isDefinition?: boolean; // 是否是接口定义模式
    moduleTree?: ModuleTreeNode[]; // 模块树
    permissionMap?: {
      execute: string;
      create: string;
      update: string;
      saveASApi?: string;
    };
    executeApi?: (params: SqlExecuteRequestParams) => Promise<any>; // 执行SQL的接口
    createApi?: (...args: any) => Promise<any>; // 创建SQL调试或者用例的接口
  }>();

  const saveLoading = ref(false);

  const requestVModel = defineModel<SqlRequestParam>('request', { required: true });

  const sqlBody = defineAsyncComponent(() => import('./body.vue'));

  const activeLayout = ref<'horizontal' | 'vertical'>('vertical');

  const isPreDataChecked = ref(false);

  const { t } = useI18n();

  const emit = defineEmits<{
    (e: 'execute', executeType: 'serverExec'): void;
    (e: 'addDone'): void;
    (e: 'requestTabClick'): void;
    (e: 'import'): void;
  }>();

  const nameInputRef = ref<InputInstance>();

  function inputBlur() {
    nameInputRef.value?.blur();
  }

  // 需要最终提示的信息
  function getFlattenedMessages() {
    // TODO：
  }

  async function updateRequest() {
    // TODO：
  }

  const saveModalVisible = ref(false);
  const saveModalFormRef = ref<FormInstance>();

  function handleCancel() {
    saveModalFormRef.value?.resetFields();
  }

  watch(
    () => saveModalVisible.value,
    (val) => {
      if (!val) {
        saveModalFormRef.value?.resetFields();
      }
    }
  );

  const reportId = ref('');

  const websocket = ref<WebSocket>();

  const temporaryResponseMap: Record<string, any> = {}; // 缓存websocket返回的报告内容，避免执行接口后切换tab导致报告丢失

  const selectTree = computed(() => {
    if (
        saveModalVisible.value || (!props.isCase && props.isDefinition && saveModalVisible.value)
    ) {
      // 切换到基础信息 tab、调试模式打开保存弹窗，或者是接口定义模式下打开保存弹窗才进行计算，避免大数据量导致进入时就计算卡顿
      return filterTree(cloneDeep(props.moduleTree || []), (e) => {
        e.draggable = false;
        return e.type === 'MODULE';
      });
    }
    return [];
  });


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

  const saveModalForm = ref({
    name: '',
    // TODO：在ms的设计中，弹窗中似乎是可以选择模块的，所以在存储接口定义前，会用modal的部分值覆盖原request中的值，这部分逻辑在realsave中
    //  如果后续sql也支持在弹窗中覆盖模块id，这里要做对应的修改
    //  除了moduleid外还有tag等值
    // 这里的module为空是全部sql按钮，如果是root则为 为规划模块
    moduleId: '',
  });

  /**
   * 生成请求参数
   * @param executeType 执行类型，执行时传入
   */
  // async function makeRequestParams(executeType?: 'localExec' | 'serverExec') {
  async function makeRequestParams(executeType?: 'serverExec') {
    const isExecute = executeType === 'serverExec';
    const { sqlContent } = requestVModel.value.body;

    let parseRequestBodyResult;
    // TODO：写死SQL的协议为MsSqlCaseElement，对于后端来说目前只有MYSQL的实现
    // 但是如果以后要实现pg协议等，具体的实现可以在后端MsSqlCaseElement类中加一个protocol
    // 也可以重新定义一个新的类型比如MsPgSqlCaseElement，这个方案可以等到具体实现的时候再定
    const polymorphicName = 'MsSqlCaseElement';
    const requestParams = {
      polymorphicName,
      body: {
        ...requestVModel.value.body,
      },
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
    let apiDefinitionParams: Record<string, any> = {};

    if (props.isDefinition) {
      // 接口定义有响应内容定义
      // TODO：这里需要检查一下，如果当前sql没有response，报错不让保存
      requestName = requestVModel.value.name;
      requestModuleId = requestVModel.value.moduleId;

      apiDefinitionParams = {
        tags: requestVModel.value.tags,
        description: requestVModel.value.description,
        status: requestVModel.value.status,
        response: requestVModel.value.response,
      };
    } else {
      requestName = requestVModel.value.isNew ? saveModalForm.value.name : requestVModel.value.name;
      requestModuleId = requestVModel.value.isNew ? saveModalForm.value.moduleId : requestVModel.value.moduleId;
    }

    // TODO：
    // ms的接口测试中此处为处理断言参数，但是对于sql测试，这里需要保存完整的sql请求结果（包括headerList和data）
    // const { assertionConfig } = requestVModel.value.children[0];
    return {
      id: requestVModel.value.id.toString(),
      reportId: reportId.value,
      // environmentId: appStore.currentEnvConfig?.id || '',
      name: requestName,
      moduleId: requestModuleId,
      num: requestVModel.value.num,
      ...apiDefinitionParams,
      // TODO：sqlMethod，临时写死
      sqlMethod: "DDL",
      request: {
        ...requestParams,
        name: requestName,
      },
      frontendDebug: false,
      isNew: requestVModel.value.isNew,
      dataSourceId: 1,
      projectId: appStore.currentProjectId,
    };
  }

  /**
   * TODO:
   * 保存请求
   * @param fullParams 保存时传入的参数
   * @param silence 是否静默保存（接口定义另存为用例时要先静默保存接口）
   */
  async function realSave(fullParams?: Record<string, any>, silence?: boolean) {
    try {
      if (!props.createApi) return;
      // TODO：
      // if (!silence) {
      //   saveLoading.value = true;
      // }
      const requestParams = await makeRequestParams();

      const params = {
        ...(fullParams || requestParams),
        ...saveModalForm.value,
      };

      const res = await props.createApi(params);
      // TODO：
      //  if (!silence) {
      //   Message.success(t('common.saveSuccess'));
      // }
      requestVModel.value.id = res.id;
      requestVModel.value.num = res.num;
      requestVModel.value.isNew = false;
      requestVModel.value.unSaved = false;
      requestVModel.value.name = res.name;
      requestVModel.value.label = res.name;
      requestVModel.value.url = res.path;
      requestVModel.value.path = res.path;
      requestVModel.value.moduleId = res.moduleId;

      saveModalVisible.value = false;

      // saveModalVisible.value = false;
      // TODO：
      // if (!silence) {
      //   saveLoading.value = false;
      emit('addDone');
      // }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      saveLoading.value = false;
    }
  }

  function handleSave(done: (closed: boolean) => void) {
    saveModalFormRef.value?.validate(async (errors) => {
      if (!errors) {
        await realSave();
        done(true);
      }
    });
    done(false);
  }

  /**
   * 保存快捷键处理
   */
  async function handleSaveShortcut() {
    if (!requestVModel.value.body.sqlContent) {
      return;
    }

    try {
      if (!requestVModel.value.isNew) {
        // 更新接口不需要弹窗，直接更新保存
        updateRequest();
        return;
      }
      // 接口调试需要弹窗保存，在ms的api接口测试中，有些场景下不需要弹窗保存，但是对于sql测试来说，有些内容（比如用例名称）等实在找不到合适的位置防止input
      // 所以对于sql测试的所有保存全部改为弹窗保存
      saveModalForm.value = {
        name: '',
        moduleId: 'root',
      };
      saveModalVisible.value = true;

    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      // 校验不通过则不进行保存
      // requestVModel.value.activeTab = SqlRequestComposition.PLUGIN;
      // TODO：错误异常处理，暂不清楚作用先跳过
      // nextTick(() => {
      //   scrollIntoView(document.querySelector('.arco-form-item-message'), { block: 'center' });
      // });
    }
  }

  const isNameError = ref(false);

  async function handleSelect(value: string | number | Record<string, any> | undefined) {
    // if (requestVModel.value.name === '') {
    //   console.log("handleSelect");
    //   isNameError.value = true;
    //   return;
    // }
    // TODO：api 调试页面，另存为接口的功能，后面做另存为用例的时候可以参考
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
    // TODO：一些表单数据的验证，先略过
    switch (value) {
      case 'save':
        handleSaveShortcut();
        break;
      case 'saveAsCase':
        // saveNewDefinition();
        break;
      default:
        break;
    }
    // apiBaseFormRef.value?.formRef?.validate(async (errors) => {
    //   if (errors) {
    //     requestVModel.value.activeTab = SqlRequestComposition.BASE_INFO;
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

  /**
   * 执行调试，这里沿用了ms的api debug逻辑，点击服务端执行后，发起的executeApi请求并不会返回
   * SQL执行的真实结果会在makeRequestParams中通过websocket监听返回
   * @param val 执行类型
   */
  // TODO：暂时不实现本地请求（localExec），以后再说
  // async function execute(executeType?: 'localExec' | 'serverExec') {
  async function execute(executeType?: 'serverExec') {
    try {
      if (!props.executeApi) return;
      await nextTick();
      requestVModel.value.executeLoading = true;
      // TODO：确定下面一行的作用
      // requestVModel.value.response = cloneDeep(defaultResponse);

      const res = await props.executeApi((await makeRequestParams(executeType)) as SqlExecuteRequestParams);
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      websocket.value?.close();
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

  defineExpose({
    execute
  });
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
    height: 80%;
    &.vertical {
      flex-direction: column;
      .response :deep(.response-head) {
        @apply sticky;

        top: 0px; // 请求参数tab高度(不算border-bottom)
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
