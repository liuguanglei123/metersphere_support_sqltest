<template>
  <div id="terminal"></div>
</template>
<script setup lang="ts">
  import { getGenerateId } from '@/utils';

  import '@xterm/xterm/css/xterm.css';
  import { FitAddon } from '@xterm/addon-fit';
  import { WebLinksAddon } from '@xterm/addon-web-links';
  import { Terminal } from '@xterm/xterm';

  let term: Terminal;
  // let ws: WebSocket;

  const id = getGenerateId();
  const ws = new WebSocket(`ws://127.0.0.1:8081/ws/terminal/ssh/${id}`);

  let heartbeatInterval: number; // 心跳定时器 ID

  onBeforeUnmount(() => {
    // clearInterval(heartbeatInterval); // 清理心跳定时器
    // ... 其他清理代码 ...
  });

  ws.onopen = () => {
    // 心跳检测：每 30 秒发送 ping
    // heartbeatInterval =
    setInterval(() => {
      if (ws?.readyState === WebSocket.OPEN) {
        ws.send('__ping__');
      }
    }, 150000); // 10 秒
  };

  ws.onclose = (e) => {
    console.log(e, '连接关闭');
  };

  ws.onmessage = (e) => {
    if (e.data === '__pong__') {
      console.log('Received pong'); // 收到 pong，确认连接活跃
      return;
    }
    term.write(e.data);
  };

  onMounted(() => {
    term = new Terminal({
      rows: 60,
      cols: 160,
      convertEol: true,
      cursorBlink: true,
      cursorStyle: "bar", // 光标样式  'block' | 'underline' | 'bar' | null
    });
    term.open(document.getElementById('terminal') as HTMLElement);
    term.loadAddon(new WebLinksAddon());
    term.loadAddon(new FitAddon());
    term.focus()
    // 自动聚焦
    term.write('connecting...\n')
    // 一开始显示连接中
    // 每一次输入都实时传输到后端和服务器通信
    term.onData(data => {
      ws.send(JSON.stringify({
        from: '1',
        to: '1',
        content: data
      }))
    })
  });
</script>
<style scoped></style>