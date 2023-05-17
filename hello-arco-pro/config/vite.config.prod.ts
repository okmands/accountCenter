import { mergeConfig } from 'vite';
import baseConfig from './vite.config.base';
import configCompressPlugin from './plugin/compress';
import configVisualizerPlugin from './plugin/visualizer';
import configArcoResolverPlugin from './plugin/arcoResolver';
import configStyleImportPlugin from './plugin/styleImport';
import configImageminPlugin from './plugin/imagemin';

export default mergeConfig(
  {
    mode: 'production',
    server: {
      proxy: {
        '/xiyaaccounts': {
          target: 'http://192.168.7.198:8080/xiyaaccounts/',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/xiyaaccounts/, ''),
        },
        '/api': {
          target: 'http://192.168.7.198:8080/',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    plugins: [
      configCompressPlugin('gzip'),
      configVisualizerPlugin(),
      configArcoResolverPlugin(),
      configStyleImportPlugin(),
      configImageminPlugin(),
    ],
    build: {
      rollupOptions: {
        output: {
          manualChunks: {
            arco: ['@arco-design/web-vue'],
            chart: ['echarts', 'vue-echarts'],
            vue: ['vue', 'vue-router', 'pinia', '@vueuse/core', 'vue-i18n'],
          },
        },
      },
      chunkSizeWarningLimit: 2000,
    },
  },
  baseConfig
);
