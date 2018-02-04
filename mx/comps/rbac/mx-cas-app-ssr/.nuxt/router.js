import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const _41b17d12 = () => import('../pages/login.vue' /* webpackChunkName: "pages/login" */).then(m => m.default || m)
const _48d81c7a = () => import('../pages/manage/user.vue' /* webpackChunkName: "pages/manage/user" */).then(m => m.default || m)
const _411943e9 = () => import('../pages/manage/privilege.vue' /* webpackChunkName: "pages/manage/privilege" */).then(m => m.default || m)
const _c0f0aa3a = () => import('../pages/manage/loginHistory.vue' /* webpackChunkName: "pages/manage/loginHistory" */).then(m => m.default || m)
const _445e78da = () => import('../pages/manage/accredit.vue' /* webpackChunkName: "pages/manage/accredit" */).then(m => m.default || m)
const _463ffd36 = () => import('../pages/manage/account.vue' /* webpackChunkName: "pages/manage/account" */).then(m => m.default || m)
const _4f2605ca = () => import('../pages/manage/department.vue' /* webpackChunkName: "pages/manage/department" */).then(m => m.default || m)
const _648839a7 = () => import('../pages/manage/logs.vue' /* webpackChunkName: "pages/manage/logs" */).then(m => m.default || m)
const _48db8e24 = () => import('../pages/manage/role.vue' /* webpackChunkName: "pages/manage/role" */).then(m => m.default || m)
const _157f9e60 = () => import('../pages/index.vue' /* webpackChunkName: "pages/index" */).then(m => m.default || m)



if (process.client) {
  window.history.scrollRestoration = 'manual'
}
const scrollBehavior = function (to, from, savedPosition) {
  // if the returned position is falsy or an empty object,
  // will retain current scroll position.
  let position = false

  // if no children detected
  if (to.matched.length < 2) {
    // scroll to the top of the page
    position = { x: 0, y: 0 }
  } else if (to.matched.some((r) => r.components.default.options.scrollToTop)) {
    // if one of the children has scrollToTop option set to true
    position = { x: 0, y: 0 }
  }

  // savedPosition is only available for popstate navigations (back button)
  if (savedPosition) {
    position = savedPosition
  }

  return new Promise(resolve => {
    // wait for the out transition to complete (if necessary)
    window.$nuxt.$once('triggerScroll', () => {
      // coords will be used if no selector is provided,
      // or if the selector didn't match any element.
      if (to.hash && document.querySelector(to.hash)) {
        // scroll to anchor by returning the selector
        position = { selector: to.hash }
      }
      resolve(position)
    })
  })
}


export function createRouter () {
  return new Router({
    mode: 'history',
    base: '/',
    linkActiveClass: 'nuxt-link-active',
    linkExactActiveClass: 'nuxt-link-exact-active',
    scrollBehavior,
    routes: [
		{
			path: "/login",
			component: _41b17d12,
			name: "login"
		},
		{
			path: "/manage/user",
			component: _48d81c7a,
			name: "manage-user"
		},
		{
			path: "/manage/privilege",
			component: _411943e9,
			name: "manage-privilege"
		},
		{
			path: "/manage/loginHistory",
			component: _c0f0aa3a,
			name: "manage-loginHistory"
		},
		{
			path: "/manage/accredit",
			component: _445e78da,
			name: "manage-accredit"
		},
		{
			path: "/manage/account",
			component: _463ffd36,
			name: "manage-account"
		},
		{
			path: "/manage/department",
			component: _4f2605ca,
			name: "manage-department"
		},
		{
			path: "/manage/logs",
			component: _648839a7,
			name: "manage-logs"
		},
		{
			path: "/manage/role",
			component: _48db8e24,
			name: "manage-role"
		},
		{
			path: "/",
			component: _157f9e60,
			name: "index"
		}
    ],
    
    
    fallback: false
  })
}
