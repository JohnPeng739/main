<template>
  <v-app light id="inspire">
    <v-navigation-drawer fixed clipped app v-model="drawer">
      <v-list dense>
        <template v-for="(item, i) in navData">
          <v-layout row v-if="item.heading" align-center :key="i">
            <v-flex xs6>
              <v-subheader v-if="item.heading">{{item.heading}}</v-subheader>
            </v-flex>
            <v-flex xs6 class="text-xs-center">
              <a href="#!" class="body-2 black--text">EDIT</a>
            </v-flex>
          </v-layout>
          <v-list-group v-else-if="item.children" v-model="item.model" no-active>
            <v-list-tile slot="item" @click="">
              <v-list-tile-action>
                <v-icon>{{item.model ? item.icon : item['icon-alt']}}</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{item.text}}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile v-for="(child, i) in item.children" :key="i" @click="">
              <v-list-tile-action>
                <v-icon v-if="child.icon">{{child.icon}}</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{child.text}}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </v-list-group>
          <v-list-tile v-else @click="">
            <v-list-tile-action>
              <v-icon>{{item.icon}}</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{item.text}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar color="blue darken-3" dark app clipped-left fixed>
      <v-toolbar-title :style="$vuetify.breakpoint.smAndUp ? 'width: 300px; min-width=250px' : 'min-width: 72px'"
                       class="ml-0 pl-3">
        <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
        <span class="hidden-xs-only">{{title}}</span>
      </v-toolbar-title>
      <v-text-field light solo prepend-icon="search" placeholder="search text" class="hidden-xs-only"
                    style="max-width: 500px; min-width: 128px"></v-text-field>
      <div class="d-flex align-center" style="margin-left: auto">
        <v-btn v-for="item in toolsData" :key="item.id" icon @click="handleClickTools(item.id)">
          <v-icon>{{item.icon}}</v-icon>
        </v-btn>
        <v-btn icon large @click="handleClickTools('avatar')">
          <v-avatar size="32px" class="purple">
            <img src="./assets/logo.png" alt="Vuetify">
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
          <v-flex xs6><span class="white--text">&copy; 2017</span></v-flex>
          <v-flex xs6 class="text-xs-right"><span class="white--text">mx institute</span></v-flex>
        </v-layout>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script>
  import {logger} from 'mx-app-utils'

  export default {
    data: () => ({
      title: 'Google Contacts',
      drawer: null,
      toolsData: [
        {icon: 'apps', id: 'apps'}, {icon: 'notifications', id: 'notify'}
      ],
      navData: [
        {icon: 'contacts', text: 'Contacts'},
        {icon: 'history', text: 'Frequently contacted'},
        {
          icon: 'keyboard_arrow_up', 'icon-alt': 'keyboard_arrow_down', text: 'Labels', model: true, children: [
          {icon: 'add', text: 'Create label'}
        ]
        },
        {
          icon: 'keyboard_arrow_up', 'icon-alt': 'keyboard_arrow_down', text: 'More', model: false, children: [
          {text: 'Import'}, {icon: 'notifications', text: 'Export'}, {text: 'Print'}
        ]
        }
      ]
    }),
    props: {
      soruce: String
    },
    methods: {
      handleClickTools(id) {
        logger.debug('Click the tools: %s.', id)
      }
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
</style>
