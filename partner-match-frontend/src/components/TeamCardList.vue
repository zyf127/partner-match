<template>
  <van-search
      v-model="searchText"
      show-action
      placeholder="请输入队伍关键词"
      @search="onSearch"
      @clear="onCancel"
  >
    <template #action>
      <div @click="onSearch">搜索</div>
    </template>
  </van-search>
  <van-tabs v-model:active="activeName" @change="doTabChange" @rendered="queryData">
    <van-tab title="公开">
      <template #title>
        <van-icon name="fire-o" color="#ee0a24" size="16px"/>
        公开
      </template>
    </van-tab>
    <van-tab title="加密">
      <template #title>
        <van-icon name="lock" size="16px"/>
        加密
      </template>
    </van-tab>
      <van-tab title="我的">
        <template #title>
          <van-icon name="user-o" size="16px"/>
          我的
        </template>
      </van-tab>
  </van-tabs>
  <div id="teamCardList">
    <van-card v-for="team in filteredTeamList"
              :title="team.teamName"
              :desc="team.teamDescription"
              :thumb="defaultAvatar">
      <template #tags>
        <van-tag plain type="primary" style="margin-right: 12px; margin-top: 5px">{{ teamStatusEnum[team.teamStatus] }}</van-tag>
        <van-tag plain type="warning" style="margin-right: 12px; margin-top: 5px">队长：{{team.userList[0].username}}</van-tag>
        <div>队伍人数：<strong>{{team.userList.length}} / {{team.maxNum}}</strong> 人 -- 剩余位置：<strong>{{team.maxNum - team.userList.length}}</strong></div>
        <div>创建时间：{{formatDateTime(team.createTime)}}</div>
        <div>过期时间：{{formatDateTime(team.expireTime)}}</div>
      </template>
      <template #footer>
        <span v-if="hasJoinTeam(team)">
            <van-popover v-if="team.userList[0].id === currentUser.id" v-model="showPopover" placement="top" style="--van-popover-light-background: rgba(255, 255, 255, 0)">
                <van-button size="small" type="primary" plain @click="toUpdateTeamPage(team.id)" style="margin-left: 2px; margin-right: 5px;">更新队伍</van-button>
                <van-button size="small" type="danger" plain @click="quitTeam(team.id)" style="margin-left: 5px; margin-right: 5px;">退出队伍</van-button>
                <van-button size="small" type="danger" plain @click="deleteTeam(team.id)" style="margin-left: 5px; margin-right: 2px;">解散队伍</van-button>
              <template #reference>
               <van-button type="primary" size="small" plain>...更多操作</van-button>
              </template>
            </van-popover>
            <van-button v-else size="small" type="danger" plain @click="quitTeam(team.id)">退出队伍</van-button>
        </span>
        <van-button v-else-if="team.userList.length === team.maxNum" size="small" disabled type="primary" plain hairline>已满员</van-button>
        <van-button v-else size="small" type="primary" plain @click="joinTeam(team.id, team.teamStatus)">加入队伍</van-button>
        <van-button size="small" type="primary" plain @click="showTeamDetail(team.id)">查看队伍</van-button>
      </template>
    </van-card>
    <van-dialog v-model:show="show" title="队伍密码" show-cancel-button @confirm="joinTeamPost" @cancel="show = false">
      <van-field
          v-model="teamJoinRequest.teamPassword"
          type="password"
          placeholder="请输入队伍密码"
          :rules="[{ required: true, message: '请填写密码'}, {validator: value => value.length <= 32, message: '密码不超过 32 个字符'}]"
      />
    </van-dialog>
  </div>
  <van-empty v-if="!teamList || teamList.length < 1" description="数据为空" />
</template>

<script setup lang="ts">
//@ts-nocheck
import {ref} from "vue";
import defaultAvatar from "../assets/defaultAvatar.png"
import {teamStatusEnum} from "../constants/team.ts";
import myAxios from "../plugins/myAxios";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
import {formatDateTime} from "../services/team.ts";
import {getCurrentUser} from "../services/user.ts";
import {UserType} from "../models/user";
import {TeamType} from "../models/team";
import {useRouter} from "vue-router";

const router = useRouter();

let teamList = [];

let currentUser: UserType = ref({});

const show = ref(false);

const teamJoinRequest = ref({
  teamId: -1,
  teamPassword: ''
});

const activeName = ref(Number(sessionStorage.getItem('activeName')));

let filteredTeamList: TeamType[] = ref([]);

const searchText = ref('');

const showPopover = ref(false);

const joinTeam = async (teamId, teamStatus) => {
  teamJoinRequest.value.teamId = teamId;
  if (teamStatus === 2) {
    show.value = true;
  } else {
    showConfirmDialog({
      message: '请确认是否加入该队伍?',
    }).then(async () => {
      await joinTeamPost()
    }).catch(() => {
      showSuccessToast("取消成功")
    })
  }
}

const joinTeamPost = async () => {
  const res = await myAxios.post('/team/join', {
    teamId: teamJoinRequest.value.teamId,
    teamPassword: teamJoinRequest.value.teamPassword
  });
  if (res['code'] === 0) {
    showSuccessToast('加入成功');
    await queryData();
  } else {
    showFailToast(res['description']);
  }
  teamJoinRequest.value.teamPassword = '';
}

const doTabChange = () => {
  if (activeName.value === 0) {
    filteredTeamList['value'] = teamList.filter(team => team.teamStatus === 0);
  } else if (activeName.value === 1) {
    filteredTeamList['value'] = teamList.filter(team => team.teamStatus === 2);
  } else if (activeName.value === 2) {
    filteredTeamList['value'] = teamList.filter(team => hasJoinTeam(team));
  }
}

const hasJoinTeam = (team) => {
  const userList = team.userList;
  const count = userList.filter(user => user.id === currentUser.id).length;
  return count === 1;
}

const queryData = async () => {
  loadData()
      .then(() => {
        doTabChange();
      });
}

const loadData = async () => {
  currentUser = await getCurrentUser()
  const res = await myAxios.get('/team/list');
  if (res['code'] === 0) {
    teamList = res.data;
  } else {
    showFailToast('加载队伍失败');
  }
}

const onSearch = async () => {
  searchText.value = searchText.value.trim();
  const res = await myAxios.get('/team/list', {
    params: {
      searchText: searchText.value
    }
  });
  if (res['code'] === 0) {
    teamList = res.data;
    doTabChange();
    showSuccessToast('搜索队伍成功');
  } else {
    showFailToast('搜索队伍失败');
  }
};
const onCancel = () => {
  searchText.value = ''
  queryData();
};

const quitTeam = async (teamId) => {
  const res = await myAxios.post('/team/quit', {
    teamId
  });
  if (res['code'] === 0) {
    showSuccessToast('退出成功');
    await queryData();
  } else {
    showFailToast('退出失败');
  }
  showPopover.value = false;
}

const deleteTeam = async (teamId) => {
  const res = await myAxios.post('/team/delete', {
    teamId
  });
  if (res['code'] === 0) {
    showSuccessToast('解散成功');
    await queryData();
  } else {
    showFailToast('解散失败');
  }
  showPopover.value = false;
}

const showTeamDetail = (teamId) => {
  sessionStorage.setItem('activeName', activeName.value);
  router.push({
    path: '/team/detail',
    query: {
      teamId
    }
  });
}

const toUpdateTeamPage = (teamId) => {
  sessionStorage.setItem('activeName', activeName.value);
  router.push({
    path: '/team/update',
    query: {
      teamId
    }
  })
}

</script>

<style scoped>
#teamCardList :deep(.van-image__img) {
  object-fit: unset;
  height: 110px;
}
</style>