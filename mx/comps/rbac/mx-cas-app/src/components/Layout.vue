<template>
  <v-app light id="inspire">
    <v-navigation-drawer fixed clipped app v-model="drawer">
      <v-list dense>
        <template v-for="(item, i) in navData">
          <v-layout row v-if="item.heading" align-center :key="i">
          </v-layout>
          <v-list-group v-else-if="item.children" v-model="item.model" no-active>
            <v-list-tile slot="item" @click="handleClickNav(item.path)">
              <v-list-tile-action>
                <v-icon>{{item.model ? item.icon : item['icon-alt']}}</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{$t(item.text)}}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile v-for="(child, i) in item.children" :key="i" @click="handleClickNav(child.path)">
              <v-list-tile-action>
                <v-icon v-if="child.icon">{{child.icon}}</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{$t(child.text)}}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </v-list-group>
          <v-list-tile v-else @click="handleClickNav(item.path)">
            <v-list-tile-action>
              <v-icon>{{item.icon}}</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t(item.text)}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar color="blue darken-3" dark app clipped-left fixed>
      <v-toolbar-title :style="$vuetify.breakpoint.smAndUp ? 'width: 300px; min-width=250px' : 'min-width: 72px'"
                       class="ml-0 pl-3">
        <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
        <span class="hidden-xs-only">{{$t('app.title')}}</span>
      </v-toolbar-title>
      <div class="d-flex align-center hidden-xs-only" style="margin-left: auto">
        <v-tooltip bottom v-for="item in toolsData" :key="item.path">
          <v-btn icon @click="handleClickTools(item.path)" slot="activator">
            <v-icon>{{item.icon}}</v-icon>
          </v-btn>
          <span>{{$t(getNavName(item.path))}}</span>
        </v-tooltip>
        <v-btn icon large @click="handleClickAvatar">
          <v-avatar size="32px" class="purple">
            <img src="../assets/logo.png" alt="Vuetify">
          </v-avatar>
        </v-btn>
      </div>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <v-layout justify-center align-center>
          <router-view></router-view>
        </v-layout>
      </v-container>
    </v-content>
    <v-footer color="blue light-3" light app clipped-left fixed>
      <v-container fluid>
        <v-layout row>
          <v-flex xs8><span class="white--text hidden-xs-only">{{$t('app.copyright')}}</span></v-flex>
          <v-flex xs4 class="text-xs-right"><span class="white--text">{{$t('app.owner')}}</span></v-flex>
        </v-layout>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {navData, getNavName} from '../router/index'

  export default {
    data: () => ({
      drawer: null,
      toolsData: [
        {path: '/index', icon: 'apps'}, {path: '/hello', icon: 'notifications'}
      ],
      navData: navData
    }),
    methods: {
      getNavName(path) {
        return getNavName(path)
      },
      handleClickAvatar() {
        // TODO
        logger.debug('Click the avatar.')
      },
      handleClickTools(path) {
        logger.debug('Click the tools: %s.', path)
        if (path && typeof path === 'string' && path.length > 0) {
          this.$router.push(path)
        }
      },
      handleClickNav(path) {
        logger.debug('Click the tools: %s.', path)
        if (path && typeof path === 'string' && path.length > 0) {
          this.$router.push(path)
        }
      }
    }
  }
</script>
