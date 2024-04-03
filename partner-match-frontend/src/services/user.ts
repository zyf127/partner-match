// @ts-ignore
import myAxios from "../plugins/myAxios.js";
import {showFailToast} from "vant";
import router from "../router";

export const getCurrentUser = async () => {
    const res = await myAxios.get('/user/current');
    if (res.code === 0) {
        return res.data;
    } else {
        showFailToast('用户未登录');
        router.push('/user/login');
    }
};