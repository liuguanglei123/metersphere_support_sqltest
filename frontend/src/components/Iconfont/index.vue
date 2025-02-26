<template>
  <div
    v-if="props.box"
    v-bind="remainingProps"
    :style="{
      '--icon-box-size': `${boxSize}px`,
      '--icon-size': `${size}px`,
    }"
    :class="[classNameBox, 'iconBox', { ['activeIconBox']: active }]"
  >
    <i :class="[className,'iconfont']">{props.code}</i>
  </div>
  <div v-else>
    <i
      :style="{
        '--icon-size': `${size}px`,
      }"
      :class="[className,'iconfont']"
      v-bind="remainingProps"
    >
      {props.code}
    </i>
  </div>
</template>

<script setup lang="ts">
  const props = defineProps<{
    code: string;
    box?: boolean;
    boxSize?: number;
    size?: number;
    className?: string;
    classNameBox?: string;
    active?: boolean;
  }>();

  const remainingProps = computed(() => {
    const { box, boxSize, size, className, classNameBox, active, ...args } = props;
    return args;
  });
</script>

<style scoped></style>
