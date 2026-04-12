import { createApp } from 'vue'
import './style.css'
import router from './router'
import '@fortawesome/fontawesome-free/css/all.min.css'
import App from './App.vue'

const app = createApp(App)

createApp(App).mount('#app')
app.use(router)
app.mount('#app')