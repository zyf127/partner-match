import 'vant/lib/index.css'
import 'vant/es/toast/style';
import App from "./App.vue";
import { createApp } from 'vue';
import router from "./router";

const app = createApp(App);
app.use(router);
app.mount('#app');



