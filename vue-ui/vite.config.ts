import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  base: '/vue-ui',
  server: {
    host: "0.0.0.0",
    port: 9002,
    //https: {
    //  cert: 'C:/Users/ch4mp/.ssh/bravo-ch4mp_self_signed.pem',
    //  key: 'C:/Users/ch4mp/.ssh/bravo-ch4mp_req_key.pem',
    //},
    //open: 'https://localhost:7080/vue-ui',
  },
})
